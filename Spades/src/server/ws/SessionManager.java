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
	static Vector<UserSession> sessionList=new Vector(20,5);
	/*
	 * This should and will be defended by semaphores... 
	 * Upgrade to using Vector, which is thread safe.
	 */
	//static ArrayList<Session> sessionList=new ArrayList();
	static void manageSession(Session sess, String susername) {
		int vc=sessionList.size();
		UserSession userSession=new UserSession(sess, vc);
		userSession.setName(susername);
		//userSession.setvc(sessionList.size());
			sessionList.add(userSession);
		}
	static void manageSession(Session sess, String susername, String sessionIdentifier) {
		int vc=sessionList.size();
		UserSession userSession=new UserSession(sess, vc);
		userSession.setName(susername);
		userSession.sessionId = sessionIdentifier;
		//userSession.setvc(sessionList.size());
			sessionList.add(userSession);
		}
	static int sessionListSize() { return sessionList.size(); }
	static UserSession getUserSession(int index) {
		return sessionList.get(index);
	}
	static UserSession getUserSession(Session session) {
		for (UserSession us: sessionList) {
			if (us.session == session) {
				return us;
			}
		}
		return null;		
	}
	static void remove(UserSession session) {
		sessionList.remove(session);
	}

	private static UserSession sessionLookup(Session session) {
		for (UserSession us: sessionList) {
			if (us.session == session) {
				return us;
			}
		}
		return null;
	}
	
	public static boolean setSessionIdentifier(Session sess, String newSessionIdentifier) {
		UserSession us=sessionLookup(sess);
		if (us == null)
			return false;
		us.sessionId = newSessionIdentifier;
		return true;
	}

	public static String getSessionIdentifier(Session sess) {
		UserSession us=sessionLookup(sess);
		if (us == null)
			return "";
		return us.sessionId;
	}
	
	static void remove(Session session) {
		// find the UserSession that has session in it and remove
		UserSession goner=null;
		for (UserSession us: sessionList) {
			if (us.session == session) {
				goner = us;
				break;
			}
		}
		if (goner != null)
			sessionList.remove(goner);
	}

}

