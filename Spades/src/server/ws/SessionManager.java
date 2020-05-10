package server.ws;

//import java.util.ArrayList;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

public class SessionManager {
	/*
	 * Create a managed class called UserSession to hold
	 * Session, username, and other internal data
	 */
	/*
	 * Upgrade to Vector, which is thread safe.
	 */
	static Vector<Session> sessionList=new Vector(20,5);
	/*
	 * This should and will be defended by semaphores... 
	 * Upgrade to using Vector, which is thread safe.
	 */
	//static ArrayList<Session> sessionList=new ArrayList();
	static void manageSession(Session sess) {
			sessionList.add(sess);
		}
	static int sessionListSize() { return sessionList.size(); }
	static Session getSession(int index) {
		return sessionList.get(index);
	}
	static void remove(Session sess) {
		sessionList.remove(sess);
	}

}
