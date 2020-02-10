package server.ws;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
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
		System.out.println("Open Connection ...");
		SessionManager.manageSession(sess);
	}
	
	@OnClose
	public void onClose(){
		System.out.println("Close Connection ...");
	}
	
	ArrayList<String> history=new ArrayList();
	
	@OnMessage
	public String onMessage(String message, boolean last, Session sess){
		System.out.println("Message from the client: " + message);
		String echoMsg = "Echo from the Server: " + message;
		history.add(message);
		int i=SessionManager.sessionListSize();
		System.out.println("broadcast(" + i + "):" + message);
		for (String s:history) {
			broadcast(s);
			}
		return echoMsg;
	}

	@OnError
	public void onError(Throwable e){
		e.printStackTrace();
	}

	boolean verbose=true;
	public void broadcast(String msg) {
		int i;
		int n=SessionManager.sessionListSize();
		for (i=0; i<n; i++) {
			Session sess=SessionManager.getSession(i);
			Basic br=sess.getBasicRemote();
			Writer w=null;
			try {
				w=br.getSendWriter();
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			try {
				w.write(msg);
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			if (verbose) {
				System.out.println("writing(" + i + "):" + msg);
			}
		}
	}
}
