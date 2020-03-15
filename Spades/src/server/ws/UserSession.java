package server.ws;

import javax.websocket.Session;

// Eclipse created... Not what I hand in mind
//import server.ws.WsServer.CardGame;

//import server.ws.SessionManager.UserSession;

public class UserSession {	// wrapper for data to keep with Session
	int vc=0;			// vc - virtual channel
	int pid=0;			// game assigned player id assigned by join for rejoini
	Session session=null;
	boolean bSuperUser=false;
	// game variables
	CardGame game=null;		// placeholder for a pointer to a game;
	CardGameKernel cgk=null;	// kernel queue
	String username="";
	
	public UserSession(Session sess) {
		session = sess;
	}
	public UserSession(Session sess, int nvc) {
		session = sess;
		vc=nvc;
	}
	public Session getSession() {
		return session;
	}
	public boolean superuser() { return bSuperUser; }
	public void setSuperUser(boolean flag) {
		bSuperUser = flag;
	}

	public void setpid(int n) {
		pid = n;
	}
	
	/*
	 * setName - user-declared name
	 */
	public void setName(String name) {
		username = name;
	}
	public String getName() {
		return username;
		//return sessionList.get(index).username;
	}
	public void setvc(int id) {
		vc = id;
	}
	public int getvc() {
		return vc;
	}
	
	/*
	 * join - join or create a game
	 * todo: params type of game, robot players, in progress, etc
	 *  eventually store games in progress
	 */
	static CardGame theGame=null;
	static CardGameKernel thecgk=null;
	public boolean join() {
		boolean bNewGame=false;
		if (theGame == null) {
			bNewGame = true;
			CardGame g = new CardGame();
			game = g;
			theGame = g;
			System.out.println("New Game.");
		}
		else {
			game = theGame;
			System.out.println("Game in progress:" + 
					game.gameName);
		}
		if (!game.join(this)) {
			return false;
		}
		if (bNewGame || thecgk == null) {
			cgk = new CardGameKernel();
			thecgk = cgk;
			cgk.setCardGame(game);
		} else {
			cgk = thecgk;
		}
		if (bNewGame)
			game.reset();
		else
			game.resend(this);
		return true;
	}
	
	public void resume() {
		// check if idle?
		cgk.resume();
	}
}
