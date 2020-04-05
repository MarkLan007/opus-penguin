package server.ws;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import server.ws.UserJCLCommand.JCLType;

/* 
 * So apparently the server endpoint sets up the listener on the 
 * server under the subdirectory that is the name of the project.
 * todo: Can this be changed in an xml file somewhere?
 */ 
//@ServerEndpoint("/gameserver")
@ServerEndpoint("/server/ws")
public class WsServer {
	
	//public class CardGame { }
	
	@OnOpen
	public void onOpen(Session sess){
		sess.setMaxIdleTimeout(1000000);
		System.out.println("Open Connection ...");
		SessionManager.manageSession(sess);
		// Catch up...
		retrievePreviousConversation(sess);
	}
	
	/*
	 * remove from broadcast queue...
	 */
	@OnClose
	public void onClose(Session sess){
		SessionManager.remove(sess);
		System.out.println("Close Connection ...");
	}  
	
	static Vector<String> history=new Vector(10, 5);
	
	void sendLine(Session sess, String line) {
		RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); 
		if(sess.isOpen())
			asynchRemote.sendText(line);
	}
	
	String comHistory="$hist";
	String comHelp="$help";
	/*
	 * todo: make sure that sender has su privs
	 */
	void processSUCommand(Session sess, String message) {
		String msg=message.toLowerCase();
		if (msg.contains(comHistory))
			retrievePreviousConversation(sess);
		else if (msg.contains(comHelp)) {
			sendLine(sess, comHistory + "\n" + comHelp);
		}
		else
			sendLine(sess, "Unrecognized command.");
	}
	/*
	 * Note:ignoring fragmentation/reassembly
	 */
	boolean remoteDebug=true;
	boolean bBypassKernel=true;	// by pass kernel synchronization; invoke g.process direction

	@OnMessage
	public void onMessage(String message, boolean last, Session sess) {

		System.out.println("Message from the client: " + message);
		if (message.startsWith("$")) {
			processSUCommand(sess, message);
		}
		// String echoMsg = "Echo from the Server: " + message;
		UserSession us = SessionManager.getUserSession(sess);
		// begin extensive remodel...
		UserJCLCommand jcl = new UserJCLCommand(message);
		switch (jcl.type) {
		case JCLNotJCL:
			// Non-jcl, non SU command
			// if (is protocol... enqueue to cgk for play...
			if (ProtocolMessage.isProtocolMessage(message)) {
				ProtocolMessage pm = new ProtocolMessage(message);
				// tell the client what protocol message parsed as:
				if (remoteDebug)
					write(us, "saw:" + pm.type + "sender:" + pm.sender + "{" + pm.usertext + "}");
				if (bBypassKernel) {
					// sleep before every move to let writes finish
					// temporary hack...
					// xxx yyy
					CardGameKernel.msleep(10);
					us.game.process(pm);
					break;
				}
				us.cgk.enqueue(pm);
				// now tell the kernel to resume if it is idle...
				if (us.cgk.isIdle())
					us.cgk.resume();
				break;
			}
			// otherwise chatstring message
			// chat function, broadcast to all
			String echoString = us.username + ">" + message;
			history.add(echoString);
			broadcast(echoString);
			break;
		case JCLPoke:
			us.cgk.resume();
			break;
		case JCLResume:
			if (us.cgk.isIdle())
				us.cgk.resume();
			break;
		case JCLSetname:
			String sName;
			sName = jcl.getValue(1);
			us.setName(sName);
			break;
		case JCLSuperUser:
			us.setSuperUser(true);
			break;
		case JCLWhoAmI:
			String s = "name=" + us.getName();
			if (us.superuser())
				s = s + "+";
			write(us, s);
			break;
		case JCLJoin:
			// here is the confluence of the http server and the gameserver
			// create a game if one does not exist, and insert this session into it
			// xxx
			System.out.println("User:" + us.username + "Joining...");

			if (!us.join()) {
				System.out.println("New User cannot join.");
				// Uh oh...
				// xxx
			}
			break;
		case JCLReset:
			System.out.println("Reset game ordered...");
			if (us.game == null) {
				System.out.println("No game to reset.");
				break;
			}
			us.game.reset();
			break;
		case JCLResend:
			us.game.resend(us);
			break;
		case JCLError: // JCL command but malformed
		case JCLCommandNotRecognized: // Command is not recognized
		case JCLCommandNotImplemented: // Command is not implemented
		default:
			write(us, "badcommand:" + jcl.type + " ignored:" + message);
			System.out.println("Bad JCL:" + message);
			break;
		}

		return;
	}
	
	/*
	 * retrievePrevious... send everything in history to new attendee
	 */
	void retrievePreviousConversation(Session sess) {
		int nMessages=history.size();
		System.out.println("attempting to write(" + nMessages + ") to: ...");

		RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); 
		String conversation = "";
		int n=0;
		for (String s:history) {
			conversation += s + "\n";
			n++;
			}
		if(sess.isOpen())
			asynchRemote.sendText(conversation);
		else
			System.out.println("attempting to write(" + n + ") to: closed channe.");
	}
	
	@OnError
	public void onError(Throwable e){
		e.printStackTrace();
	}

	/*
	 * recent addition; don't use; use sendline
	 *
	public void write(Session sess, String msg) {
		RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); 			
		if (verbose) {
			System.out.println("writing(" + msg + "):");
		}
		if (sess.isOpen())
			asynchRemote.sendText(msg);
		
	}
	*/
	
	boolean verbose=true;
	public void write(UserSession us, String msg) {
		Session sess=us.getSession();
		if (bBypassKernel)
			CardGameKernel.msleep(10);	// still necessary?
		/*
		 * to prevent the error: 
		 * 		remote endpoint was in state [TEXT_FULL_WRITING] which is 
		 * 		an invalid state for called method
		 * use basic (rather than asynch) and just block and wait
		 */
		RemoteEndpoint.Basic basicRemote=sess.getBasicRemote();
		//RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote();
		if (verbose) {
			System.out.println("send(" + us.username + "):" + msg);
		}
		if (sess.isOpen())
			try {
				basicRemote.sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//asynchRemote.sendText(msg);		
	}
	// static version of write...
	static public void send(UserSession us, String msg) {
		Session sess=us.getSession();
		boolean verbose=true;	// local version...
		/*RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); 			
		if (verbose) {
			System.out.println("send(" + us.username + "):" + msg);
		}
		if (sess.isOpen())
			asynchRemote.sendText(msg);		
		RemoteEndpoint.Basic basicRemote=sess.getBasicRemote();
		//RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote();
		if (verbose) {
			System.out.println("send(" + us.username + "):" + msg);
		}
		*/
		RemoteEndpoint.Basic basicRemote=sess.getBasicRemote();
		if (sess.isOpen())
			try {
				basicRemote.sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//asynchRemote.sendText(msg);		

	}
	
	public void broadcast(String msg) {
		int i;
		int n=SessionManager.sessionListSize();
		for (i=0; i<n; i++) {
			UserSession uSess=SessionManager.getUserSession(i);
			Session sess=uSess.getSession();
			//Basic br=sess.getBasicRemote();
			RemoteEndpoint.Basic basicRemote=sess.getBasicRemote();

			RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); 			
			if (verbose) {
				System.out.println("writing(" + i + "):" + msg);
			}
			if (sess.isOpen())
				try {
					basicRemote.sendText(uSess.username + ">" + msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
