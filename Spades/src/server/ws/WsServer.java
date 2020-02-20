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

/* xxx
 * So apparently the server endpoint sets up the listener on the 
 * server under the subdirectory that is the name of the project.
 * todo: Can this be changed in an xml file somewhere?
 */ 
@ServerEndpoint("/websocketendpoint")
public class WsServer {
	
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
	@OnMessage
	public void onMessage(String message, boolean last, Session sess){

		System.out.println("Message from the client: " + message);
		if (message.startsWith("$")) {
			processSUCommand(sess, message);
		}
		// String echoMsg = "Echo from the Server: " + message;
		if (isJCL(message)) {
			// process and write return string to sender, but do not save, echo, or broadcast
			var returnString = processJCL(message);
			// write it back... xxx yyy
			sendLine(sess, returnString);
		} else {
			history.add(message);
			broadcast(message);
		}
		return ; // avoid timeout, return nada... echoMsg;
	}

	String jclPattern = "//";
	Pattern jclRegex = Pattern.compile(jclPattern);
	public String processJCL(String com) {
		return "//+Server processed:" + com;	// for now
	}
	
	public boolean isJCL(String commandString) {
		Matcher m = jclRegex.matcher(commandString);
		if (m.find(0)) {
			return true;
		}
		return false;	
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
	public void broadcast(String msg) {
		int i;
		int n=SessionManager.sessionListSize();
		for (i=0; i<n; i++) {
			Session sess=SessionManager.getSession(i);
			//Basic br=sess.getBasicRemote();
			RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); 			
			if (verbose) {
				System.out.println("writing(" + i + "):" + msg);
			}
			if (sess.isOpen())
				asynchRemote.sendText(msg);
		}
	}
}
