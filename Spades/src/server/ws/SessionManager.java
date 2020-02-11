package server.ws;

import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

public class SessionManager {
	/*
	 * This should and will be defended by semaphores... 
	 */
	static ArrayList<Session> sessionList=new ArrayList();
	static void manageSession(Session sess) {
			sessionList.add(sess);
		}
	static int sessionListSize() { return sessionList.size(); }
	static Session getSession(int index) {
		return sessionList.get(index);
	}

}
