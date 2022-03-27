package server.ws;

import java.io.IOException;
import java.io.Writer;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
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
// convert to this url
//@ServerEndpoint("/server/ws/{client}")
//@ServerEndpoint("/websocket/{client}")
//@ServerEndpoint("/server/ws/{client}")
//@ServerEndpoint("/server/ws/websocket/{client}")
//@ServerEndpoint("/server/ws")
@ServerEndpoint("/server/ws/{client}")
public class WsServer {

	// public class CardGame { }

	@OnOpen
	public void onOpen(Session sess, EndpointConfig endpointConfig) {
		sess.setMaxIdleTimeout(1000000);
		Principal principal = sess.getUserPrincipal();
		String pname = null;
		if (principal != null)
			pname = principal.getName();
		System.out.println("Temp/Principalname=" + pname);
		//

		// RemoteBasicEndpoint rbe = sess.getBasicRemote();
		// sess.getBasicRemote().getClass().getAnnotations().getClass().getCanonicalName();
		String sClientName = "default";
		String sFriendlyName = "default";
		String sReallyFriendlyName = sess.getPathParameters().get("client");
		String sPathParams = sess.getPathParameters().toString();
		Map<String, String> path = sess.getPathParameters();
		String p = path.get("path");
		if (p == null)
			p = "guest";
		System.out.println("p=" + p);
		System.out.println("onOpen... Opening at:" + sPathParams);
		// sPathParams = sess.
		if (sess.getRequestParameterMap().containsKey("client")) {
			// sClientName = sess.p
			sClientName = sess.getRequestParameterMap().toString();
			sFriendlyName = sess.getRequestParameterMap().get("client").get(0);
			/*
			 * sClientName = sess.getRequestParameterMap().getOrDefault("client");
			 */
			// sClientName = sess.getPathParameters().get("client").
			System.out.println("Yea! retrieved client name is" + sClientName);
			System.out.println("Yea! retrieved client name is" + sFriendlyName);
			System.out.println("Yea! retrieved really friendly client name is:" + sReallyFriendlyName);
			/*
			 * sReallyFriendlyName is the thing to setname to...
			 */
		}
		System.out.println("Open Connection ...");
		String gensym = newSessionIdentifier();
		SessionManager.manageSession(sess, sReallyFriendlyName, gensym);
		// call gensym to create a name
		// and set the session name to that.
		// SessionManager.setSessionIdentifier(sess, gensym);

		// Catch up...
		retrievePreviousConversation(sess);
		//
		// format welcome message
		String msg = CardGame.getFormattedAlertMsg(
				// "Successfully Connected to gameserver. Select 'Join' or create 'new' game
				// from menu."
				// "Successfully connected to Gameserver. Use 'Game' menu to 'Join' game, or
				// 'New' to create one."
				// "Successfully connected to Gameserver. 'Join' ongoing from 'Game' menu; 'New'
				// to create."
				"Successfully connected to Gameserver. From 'Game' menu, 'Join' ongoing or create 'New'.");
		write(sess, msg);
	}

	/*
	 * remove from broadcast queue... session has been closed if user was in any
	 * games, notify the game and remove from the broadcast queue
	 */
	@OnClose
	public void onClose(Session sess) {
		UserSession us = SessionManager.getUserSession(sess);

		if (us == null) // nothing more we can do...
			return;
		CardGame g = us.getGame();
		if (g == null) {
			; // not in a game nothing to do
		} else {
			// disconnect with the pid that CardGame gave us
			g.disconnect(us.getpid());
		}
		//
		// remove from broadcast queue, etc
		SessionManager.remove(sess);
		System.out.println("Close Connection ...");
		// if (e == java.io.EOFException) { }
		// I caught you, literally, you bastard!
		// ... see onerror catch clause
		// System.out.println("And crash, and dump stacktrace?");

	}

	static Vector<String> history = new Vector(10, 5);

	void sendLine(Session sess, String line) {
		RemoteEndpoint.Async asynchRemote = sess.getAsyncRemote();
		if (sess.isOpen())
			asynchRemote.sendText(line);
	}

	String comHistory = "$hist";
	String comHelp = "$help";

	/*
	 * todo: make sure that sender has su privs
	 */
	void processSUCommand(Session sess, String message) {
		String msg = message.toLowerCase();
		if (msg.contains(comHistory))
			retrievePreviousConversation(sess);
		else if (msg.contains(comHelp)) {
			sendLine(sess, comHistory + "\n" + comHelp);
		} else
			sendLine(sess, "Unrecognized command.");
	}

	/*
	 * Note:ignoring fragmentation/reassembly
	 */
	boolean remoteDebug = true;
	boolean bBypassKernel = true; // by pass kernel synchronization; invoke g.process direction

	// rudimentary game-session manager
	// ++
	/*
	 * No-ops for now
	 */
	String getSessionInfo(String msg) {
		return "";
	}

	/*
	 * Session info is leading: # SxxGxxPx#
	 */
	String stripSessionInfo(String msg) {
		return msg;
	}

	static CardGame[] cardGames = new CardGame[4];
	static SpadesGame[] spadesGames = new SpadesGame[4];

	// returns a fake protocol message that will cause the score dialog to put it up
	// for user
	String listGames() {
		String sTemp = "";
		for (int i = 0; i < cardGames.length; i++) {
			if (cardGames[i] == null)
				continue;
			sTemp += "$" + cardGames[i].sGameName + "=" + cardGames[i].nPlayers + "." + cardGames[i].bGameInProgress;
		}
		sTemp += '$';
		sTemp += "#game#Players#InProgress?#";
		ProtocolMessage returnMessage = new ProtocolMessage(ProtocolMessageTypes.GAME_QUERY, sTemp);

		return returnMessage.encode();
		/*
		 * for (i = 0; i < nPlayers; i++) { var sessionName="(none)"; if
		 * (!playerArray[i].isRobot()) sessionName =
		 * playerArray[i].userSession.sessionId;
		 * 
		 * sTemp = sTemp + "$" + playerArray[i].getName() + "=" + sessionName + "." +
		 * playerArray[i].isRobot() ; } sTemp += '$'; sTemp +=
		 * "#User#Session Id#IsRobot?#";
		 * 
		 */
	}

	/*
	 * ToDO: this is a placeholder...
	 */
	CardGame lookupGameFromSession(String sessionString) {
		if (sessionString.isEmpty())
			return cardGames[0];
		return cardGames[0];
	}

	/*
	 * intern - makes this game the default game of the type
	 *  -- clobbers the old game.
	 *  This is hugely temporary
	 */
	boolean internDefault(CardGame g) {
		for (int i = 0; i < cardGames.length; i++) {
			String gtype = g.getGameType();
			if (cardGames[i] == null) 
				continue;
			if (gtype.equalsIgnoreCase(cardGames[i].getGameType()))
				cardGames[i] = g;
		}
		return false;
	}

	/*
	 * Add a parameter for actual gensymed-name... ... returns first game of the
	 * requested type
	 */
	CardGame lookupGameByType(String stype) {
		makeDefaultGames();
		int i;
		System.out.println("LookupGame: Under construction... relying on default game to be returned.");
		for (i = 0; i < cardGames.length; i++) {
			String gname="";
			if (cardGames[i] != null)
				gname=cardGames[i].getGameType();
			if (cardGames[i] != null && stype.equalsIgnoreCase(cardGames[i].getGameType())) {
				// found it
				return cardGames[i];
			}
		}
		return null;
	}

	/*
	 * TODO: This could be astonishing to users if this isn't generalized...
	 * (somewhat fixed. Still temporary)
	 */
	void setNewDefaultGame() {
		bMakeDefaultGames = true;
		makeDefaultGames();
		//cardGames[0] = new HeartsGame();
	}

	void setNewDefaultGame(CardGame g) {
		internDefault(g);
		//cardGames[0] = g;
	}

	boolean bMakeDefaultGames = true;

	void makeDefaultGames() {
		if (bMakeDefaultGames)
			;
		else
			return;
		bMakeDefaultGames = false;

		cardGames[0] = new HeartsGame();
		cardGames[1] = new SpadesGame(); // this is temporary? TODO: change this?
	}

	CardGame getDefaultGame(String sGameType) {
		makeDefaultGames();
		if (cardGames[0] == null) {
			// This can be deleted, right? makeDefaultGames should work right?
			System.out.println("Generating new (default) game...");
			cardGames[0] = new HeartsGame();
			cardGames[1] = new SpadesGame(); // this is temporary? TODO: change this?
		}
		if (sGameType.equalsIgnoreCase("spades"))
			return cardGames[1];
		else if (sGameType.equalsIgnoreCase("hearts"))
			return cardGames[0];
				
		return cardGames[1];
	}

	CardGame getDefaultGame() {
		makeDefaultGames();
		return cardGames[1];
	}

	int globalSessionId = 0;

	/*
	 * newSessionIdentifier - gensym
	 */
	String newSessionIdentifier() {
		return "#S" + globalSessionId++ + "gXpX#";
	}
	// --

	static int stringToNumber(String sparam, int defaultValue) {
		for (int n = 0; n < sparam.length(); n++) {
			char c = sparam.charAt(n);
			if (c < '0' || c > '9') {
				return defaultValue;
			}
		}
		int number = Integer.parseInt(sparam);
		return number;
	}

	/*
	 * Main role of onMessage is to detect JCL commands and process them, but it
	 * also wraps the protocol messages by calling join with the Session info
	 * necessary to write back to it. Subsequently it keeps track of what game the
	 * session is associated with so that it route messages to the right game. (held
	 * together in UserSession)
	 */
	@OnMessage
	public void onMessage(String message, boolean last, Session sess) {

		CardGame g = null;
		String sname = "";
		String sGameType = "";
		String sparam = "";

		if (bLowLevelIODebug) {
			System.out.println("Message from the client: " + message);
			if (message.startsWith("$")) {
				processSUCommand(sess, message);
			}
		}
		// String echoMsg = "Echo from the Server: " + message;
		UserSession us = SessionManager.getUserSession(sess);
		// begin extensive remodel...
		UserJCLCommand jcl = new UserJCLCommand(message);
		switch (jcl.type) {
		case JCLNotJCL:
			// Non-jcl, non SU command
			// if (is protocol... enqueue to cgk for play...
			String sRemoteSession = getSessionInfo(message);
			if (ProtocolMessage.isProtocolMessage(message)) {
				String msg = stripSessionInfo(message);
				ProtocolMessage pm = new ProtocolMessage(msg);
				if (us.game == null) {
					write(us, "Unexpected protocol message: You aren't currently in a game.");
					return;
				}
				// Never trust the client about the seat id.
				pm.setSender(us.pid);
				g = us.game;
				/*
				 * This would be good to do here...
				 *
				 * //g=lookupGameFromSession(sRemoteSession); if (g == null) g =
				 * getDefaultGame();
				 */
				// tell the client what protocol message parsed as:
				if (remoteDebug)
					write(us, "saw:" + pm.type + "sender:" + pm.sender + "{" + pm.usertext + "}");
				g.process(pm);
				/*
				 * if (bBypassKernel) { // sleep before every move to let writes finish //
				 * temporary hack... // xxx yyy /// Causes Tomcat warnings for memory leak? //
				 * CardGameKernel.msleep(10); g.process(pm); // was... //us.game.process(pm);
				 * break; } /* cgk is completely disabled because messages coming into onMessage
				 * are serialized
				 *
				 * us.cgk.enqueue(pm); // now tell the kernel to resume if it is idle... if
				 * (us.cgk.isIdle()) us.cgk.resume();
				 */
				break;
			}
			// otherwise chatstring message
			// chat function, broadcast to all
			String echoString = us.username + ">" + message;
			history.add(echoString);
			broadcast(echoString);
			break;
		case JCLPoke:
			write(us, "Poke not implemented now");
			// us.cgk.resume();
			break;
		case JCLResume:
			/*
			 * kernel disabled... resume now resumes an idle game after humans left if
			 * (us.cgk.isIdle()) us.cgk.resume();
			 */
			sname = "";
			sparam = "";
			if (jcl.argc() > 1) { // argc is always at least 1
				sname = jcl.getName(1);
				sparam = jcl.getValue(1);
			}
			if (sparam.length() > 0) {
				g = lookupGameFromSession(sparam);
				write(us, "Resume-from-session is unimplemented");
			}
			if (g == null)
				g = getDefaultGame();
			if (!g.start()) {
				String msg = "Play Initiated. Can't start. User 'resume' or use 'misdeal', 'newdeal' or 'reset' to reset.";
				String fmsg = CardGame.getFormattedAlertMsg(msg);
				write(sess, fmsg);
				// send to client as a dialog...
			}

			break;
		case JCLPrevious:
			if (us.game == null)
				write(us, "Not in a game...");
			if (us.game.sendPreviousTrick(us.pid))
				;
			else
				write(us, "No previous trick.");
			break;
		case JCLSetname:
			String sName;
			if (jcl.argc() > 1)
				sName = jcl.getValue(1);
			else
				sName = "Ghost-in-Machine";
			System.out.println("name=" + sName);
			us.setName(sName);
			write(us, us.sessionId + us.getName());
			if (us.game != null) {
				int pid = us.getpid();
				us.game.setName(pid, sName);
				System.out.println("Game" + us.game.gameId + " Player" + pid + "=" + sName);
			} else {
				System.out.println("name=" + sName);
			}
			break;
		case JCLNote:
			int argc = jcl.argc();
			String s = "UserNote> ";
			// System.out.println("Argc=" + argc +
			// ((argc > 1) ? " Parameters..." : "."));
			for (int j = 0; j < jcl.argc(); j++)
				s = s + jcl.getName(j) + " ";
			// System.out.println("Arg" + j
			// + ">" +jcl.getName(j) + "=" + jcl.getValue(j));
			System.out.println(s);
			break;
		case JCLSu: // was JCLSuperUser
			us.setSuperUser(true);
			write(us, "With great power comes great responsibility...");
			break;
		case JCLWho:
			int playerId = us.getpid();
			if (playerId == -1) {
				write(us, "Not currently in a game; can't get status");
				break;
			}
			us.game.sendFormattedPlayerInfo(playerId, us.isSuperUser());
			break;
		case JCLPeek:
			// argc, argv...
			playerId = us.getpid();
			if (!us.isSuperUser()) {
				write(us, "No peeking!");
				break;
			}
			if (playerId == -1) {
				write(us, "Not currently in a game; can't peek");
				break;
			}
			s = "";
			int nparam = 0;
			sname = "";
			sparam = "";
			if (jcl.argc() > 1) { // argc is always at least 1
				sname = jcl.getName(1);
				sparam = jcl.getValue(1);
				nparam = stringToNumber(sparam, 0);
			} else {
				sparam = "?";
			}
			// us.game.sendFormattedPlayerInfo(playerId);
			String cards = us.game.peek(nparam);
			s = "Peek(" + nparam + ")=" + /* cards.length()/2 + */ "<" + cards + ">";
			write(us, s);
			break;
		case JCLWhoAmI:
			s = "";
			if (us.superuser())
				s = s + "+";
			s += us.sessionId + us.getName();
			if (us.game != null)
				s += " AKA " + "g" + "0" + us.pid;
			write(us, s);
			break;
		case JCLBid:
			if (us.game == null) {
				String msg = "Not currently in a game; 'join' existing game, or 'new' to create.";
				String pmsg = CardGame.getFormattedAlertMsg(msg);
				write(us, pmsg);
				break;				
			}
			g = us.game;
			if (!g.nameOfTheGame().equalsIgnoreCase("spades")) {
				String msg = "Can only bid in spades (or some game that's implemented yet...)";
				String pmsg = CardGame.getFormattedAlertMsg(msg);
				write(us, pmsg);
				break;
			}
			// //bid returns the bids
			if (jcl.argc() == 1) {
				// get the bids and write them back...
				for (int i=0; i<g.nPlayers; i++) {
					int bid = g.getBid(i);
					write(us, "p(" + i + ")->" + bid + ".");
				}
			}
			// bid n [seat] sets the bid
			if (jcl.argc() > 1) { // argc is always at least 1
				String sBid = jcl.getValue(1);
				int iSeat = us.getpid();
				String sSeat="";
				if (jcl.argc() > 2) {
					sSeat = jcl.getValue(2);
					iSeat = Integer.parseInt(sSeat);
				}
				String xstring = "SettingBid:" + sBid + " iseat=" + iSeat;
				System.out.println(xstring);
				g.setBid(Integer.parseInt(sBid), iSeat);
				write(us, xstring);
			} else {
				// String sdefault=us.getDefaultGame();
				System.out.println("Using default:" + sGameType);
				g = lookupGameByType(sGameType);
			}
			break;
		case JCLStart:
			if (us.game == null) {
				// format error dialog and send back;
				String msg = "Not currently in a game; 'join' existing game, or 'new' to create.";
				String pmsg = CardGame.getFormattedAlertMsg(msg);
				write(us, pmsg);
				break;
			}
			g = us.game;
			if (!g.start()) {
				write(us, "Play already initiated. Can't start. Use 'misdeal' or 'reset'  to reset.");
			}
			break;
		case JCLLs: // list available games...
			sname = "";
			sparam = "";
			// ggg
			sparam = listGames();
			write(us, sparam); // need a "wrap as protocol message command from game"
			break;
		case JCLJoin:
			// Join hearts|spades [gameid]
			// create a game if one does not exist, and insert this session into it
			System.out.println("User: '" + us.username + "' Joining...");
			sGameType = "spades";
			sparam = "";
			if (jcl.argc() > 1) { // argc is always at least 1
				String sGameId = "?G0551"; // not yet implemented... Random id...
				sGameType = jcl.getValue(1);
				if (sGameType.equalsIgnoreCase("hearts"))
					;
				else if (sGameType.equalsIgnoreCase("spades"))
					;
				else {
					write(us, "invalid game specified:" + sname);
					break;
				}
				System.out.println("Join with param:" + sGameType);
				if (jcl.argc() > 2)
					sGameId = jcl.getValue(2); // not yet implemented
				System.out.println("Joining:" + sGameType + " gameid=" + sGameId);
				g = lookupGameByType(sGameType);
			} else {
				// String sdefault=us.getDefaultGame();
				System.out.println("Using default:" + sGameType);
				g = lookupGameByType(sGameType);
			}
			if (g == null)
				g = getDefaultGame(sGameType);
			us.game = g;
			String friendlyName = us.getName();
			friendlyName = g.uniqueName(friendlyName);
			if (!g.gameInProgress()) {
				write(us, "Game not in progress. Create a new one.");
				break;
			}
			boolean bJoinStatus = g.join(us, friendlyName);
			if (bJoinStatus) {
				// joined ok...
				//
				// First set the user's name from the session into the game
				if (us.username != "" && us.pid != -1) {
					g.setName(us.pid, us.username);
				}
				//
				String gameName = sname + 0;
				String msg = friendlyName + " Successfully joined game: " + gameName
						+ ". Select 'start' from menu to initiate play. (or wait for other people to join.)";
				String pmsg = CardGame.getFormattedAlertMsg(msg);
				write(us, pmsg);
				// Announce new player to other humans
				// hold off on this...
				// shouldn't game do all this???
				// g.broadcast(true, friendlyName + "seat:" + us.getpid() + " Has joined the
				// game.");
				// write(us, "Successfully joined game:" + g.sGameName + " as " + friendlyName +
				// "...");
				// write(us, "issue start command to initiate play.");
			} else if (!bJoinStatus && (sparam.contains("bygod") || (sname.contains("bygod")))) {
				// doubt bygod works anymore...
				broadcast("Game reset by divine providence. Mortals will need to rejoin.");
				System.out.println("Game reset by divine providence. Mortals will need to rejoin.");
				// byGod = true;
				g.reset();
				if (g.join(us, us.sessionId + "/God"))
					write(us, "%msg%ByGod join successfull");
				else
					write(us, "%msg%Divine intervention failed. Still can't join.");
				System.out.println("Apocalyptic reset complete...");
			} else {
				System.out.println("New User cannot join.");
				write(us, "New User cannot join. Game full.");
			}
			break;
		case JCLNew: // create a new game... clobber the old
			sname = "";
			sGameType = "";
			sparam = "";
			if (jcl.argc() > 1) { // argc is always at least 1
				String sGameId = "hearts"; // not yet implemented...
				sname = jcl.getName(1);
				sGameType = jcl.getValue(1);
				System.out.println("new game of:" + sGameType);
				if (jcl.argc() > 2)
					sGameId = jcl.getValue(2);
				System.out.println("New:" + sGameType + " gameid=" + sGameId);
				if (sGameType.equalsIgnoreCase("spades"))
					g = new SpadesGame();
				else if (sGameType.equalsIgnoreCase("hearts"))
					g = new HeartsGame();
				else {
					// Grievous error... No such game type;
					write(us, "Grievous error: No such game type. Ignored.");
					break;
				}
				// intern the game
				internDefault(g);
			} else {
				// Must designate a type of game to create...
				// but if you are in a game, use the type
				write(us, "Missing Param to new. Command ignored.");
				break;
				// g = lookupGameFromSession(sparam);
			}

			setNewDefaultGame(g);
			// g = getDefaultGame();
			g.join(us, us.username);
			String gameName = g.getName();
			String msg = "Created and joined game: " + gameName
					+ ". Select 'start' from menu to initiate play. (or wait for other people to join.)";
			String pmsg = CardGame.getFormattedAlertMsg(msg);
			write(us, pmsg);

			break;
		case JCLRejoin:
			sparam = "";
			if (jcl.argc() > 1) { // argc is always at least 1
				sname = jcl.getName(1);
				sparam = jcl.getValue(1);
			}
			write(us, "Rejoin(" + sparam + ") by session not yet implemented.");
			// lookup usersession by session id
			// and take it over
			// request a resend on the joiners behalf
			// i.e. send a welcome message
			//
			// can also (implement) capture a seat here...
			break;
		case JCLResend:
			// not to be confused with
			// ... JCLRefresh which sends the whole context
			us.game.resend(us);
			break;
		case JCLRefresh:
			playerId = us.getpid();
			if (playerId == -1) {
				write(us, "Not currently in a game; can't refresh");
				break;
			}
			us.game.refresh(playerId);
			break;
		case JCLScore:
			playerId = us.getpid();
			if (playerId == -1) {
				write(us, "Not currently in a game; can't get status");
				break;
			}
			us.game.sendScore(playerId);
			// write(us, "" + jcl.type + "(" + playerId + "): under construction");
			break;
		case JCLStatus:
			playerId = us.getpid();
			if (playerId == -1) {
				write(us, "Not currently in a game; can't get status");
				break;
			}
			us.game.sendScore(playerId);
			// write(us, "" + jcl.type + "(" + playerId + "): under construction");
			break;
		case JCLReplay:
			if (us.game == null) {
				write(us, "You aren't in a game. Nothing to replay.");
				break;
			}
			us.game.replay();
			write(us, "Replay. Duplicate mode initiated. 'Start' to commence play.");
			break;
		case JCLMisdeal:
			/*
			 * just deal again. -- don't show score, don't advance pass type
			 */
			if (us.game == null) {
				write(us, "Can't declare a misdeal. You aren't in a game.");
				break;
			}
			if (jcl.type == UserJCLCommand.JCLType.JCLMisdeal) {
				write(us, "Misdeal declared:");
				System.out.println("Player(" + us.getpid() + ") declares misdeal.");
			}
			// reset the hand
			us.game.reset();
			// does not advance passtype!
			// us.game.start();
			write(us, "Reseting Hand: passtype unchaged");
			break;
		case JCLNewdeal:
			/*
			 * just skip to the next deal, as if the last trick was played out -- show
			 * score, advance pass type
			 */
			if (us.game == null) {
				write(us, "Can't redeal. You aren't in a game.");
				break;
			}
			if (jcl.type == UserJCLCommand.JCLType.JCLMisdeal) {
				write(us, "Misdeal declared:");
				System.out.println("Player(" + us.getpid() + ") declares misdeal.");
			}
			us.game.handOver();
			write(us, "newdeal: passtype advanced.");
			// only way to advance passtype!
			break;
		case JCLReset:
			/*
			 * Lookup game by session and reset...
			 */
			/*
			 * Todo: param to say what to reset.. All to reboot, if su...
			 */
			if (us.game == null) {
				write(us, "Not in a game; can't reset.");
				break;
			} else
				g = us.game;
			write(us, "Game reset ordered. Everyone must 'join again");
			System.out.println("Reset game ordered...");
			g.resetGame();
			break;
		case JCLShuffle:
			// set shuffle game-wide
			// for debugging purposes.
			String sShuffleType = "";
			if (jcl.argc() > 1)
				sShuffleType = jcl.getValue(1);
			else
				sShuffleType = "none";
			// Can't do that here. This will shuffle with the default type...
			// g = getDefaultGame();
			if (Subdeck.isValidShuffleType(sShuffleType)) {
				// unfortunately cards are distributed at join
				// to affect the shuffle, must do it globally
				// us.game.setShuffle(sShuffleType);
				Subdeck.setShuffle(sShuffleType);
				write(us, "shuffle:" + sShuffleType);
			} else
				write(us, "invalid shuffle type:" + sShuffleType + "-use none, random, clubs, or high");
			// write(us, "" + jcl.type + "(" + sShuffleType + "): under construction");
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
		int nMessages = history.size();
		if (bLowLevelIODebug) {
			System.out.println("attempting to write(" + nMessages + ") to: ...");
		}
		RemoteEndpoint.Async asynchRemote = sess.getAsyncRemote();
		String conversation = "";
		int n = 0;
		for (String s : history) {
			conversation += s + "\n";
			n++;
		}
		if (sess.isOpen()) {
			// asynchRemote.sendText(conversation);
			write(sess, conversation);
		} else
			System.out.println("attempting to write(" + n + ") to: closed channe.");
	}

	@OnError
	public void onError(Session sess, Throwable e) {
		Throwable cause = e.getCause();
		/* normal handling... */
		if (cause != null)
			System.out.println("Error-info: cause->" + cause);
		try {
			// Likely EOF (i.e. user killed session)
			// so just Close the input stream as instructed
			sess.close();
		} catch (IOException ex) {
			System.out.println("Handling eof, A cascading IOException was caught: " + ex.getMessage());
			ex.printStackTrace();
		} catch (IllegalStateException ex) {
			System.out.println("IllegalStateException - writing to closed channel?" + ex.getMessage());
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			System.out.println("Yikes: NullPointer!" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			System.out.println("Session error handled. (likely unexpected EOF) resulting in closing User Session.");
			if (e != null)
				e.printStackTrace();
			/*
			 * if (e != null) { System.out.println("Error: cause->" + cause);
			 * System.out.println("Error: cause->" + e.getLocalizedMessage()); } else {
			 * System.out.println("Error: Finally: cause->" + e.getLocalizedMessage()); }
			 */
		}
	}

	/*
	 * recent addition; don't use; use sendline
	 *
	 * public void write(Session sess, String msg) { RemoteEndpoint.Async
	 * asynchRemote=sess.getAsyncRemote(); if (bLowLevelIODebug) {
	 * System.out.println("writing(" + msg + "):"); } if (sess.isOpen())
	 * asynchRemote.sendText(msg);
	 * 
	 * }
	 */

	static boolean bLowLevelIODebug = false;
	boolean bNeverSleep = true;

	/*
	 * underlying routine called by all -- added late so could write a message
	 * before a user session is actually created...
	 */
	static public void write(Session sess, String msg) {
		/*
		 * to thwart the error: 'remote endpoint was in state [TEXT_FULL_WRITING] which
		 * is an invalid state for called method use basic' try, try again...
		 */
		RemoteEndpoint.Basic basicRemote = sess.getBasicRemote();
		int i = 0; // tries...
		boolean bWriteSucceeded = false;
		if (bLowLevelIODebug) {
			System.out.println("send(" + "--connecting--" + "):" + msg);
		}
		if (sess.isOpen()) {
			for (i = 0; i < 10 && !bWriteSucceeded; i++) {
				try {
					// Crap. Sometimes getting this:
					// The remote endpoint was in state [TEXT_FULL_WRITING] which is an invalid
					// state for called method
					basicRemote.sendText(msg);
					bWriteSucceeded = true;
				} catch (IOException e) {
					// Enhance the auto-generated catch block to try again...
					// TODO: check for if (e == java.io.EOFException))
					if (i < 10 && !bWriteSucceeded) {
						CardGameKernel.msleep(20);
						System.out.println("Write tried:" + i + "times and failed.");
						continue;
					} else if (!bWriteSucceeded) {// Admit failure.
						System.out.println("Write failed: " + i + " retries. Dumping stackTrace");
						e.printStackTrace();
					}
				}
			}
			// succeed on first time and say nothing...
			if (bWriteSucceeded) {
				if (i > 1)
					System.out.println("Write took " + i + " tries to succeed.");
			} else {
				System.out.println("Write failed after " + i + " retries.");
			}
		}
	}

	static public void write(UserSession us, String msg) {
		Session sess = us.getSession();
		write(sess, msg);
		/*
		 * if (bNeverSleep) ; else if (bBypassKernel) CardGameKernel.msleep(10); //
		 * still necessary? /* to prevent the error: remote endpoint was in state
		 * [TEXT_FULL_WRITING] which is an invalid state for called method use basic
		 * (rather than asynch) and just block and wait / RemoteEndpoint.Basic
		 * basicRemote=sess.getBasicRemote(); //RemoteEndpoint.Async
		 * asynchRemote=sess.getAsyncRemote(); if (bLowLevelIODebug) {
		 * System.out.println("send(" + us.username + "):" + msg); } if (sess.isOpen())
		 * try { basicRemote.sendText(msg); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * //asynchRemote.sendText(msg);
		 */
	}

	// static version of write...
	static public void send(UserSession us, String msg) {
		Session sess = us.getSession();
		write(sess, msg);
		// boolean bLowLevelIODebug=true; // local version...
		/*
		 * RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); if
		 * (bLowLevelIODebug) { System.out.println("send(" + us.username + "):" + msg);
		 * } if (sess.isOpen()) asynchRemote.sendText(msg); RemoteEndpoint.Basic
		 * basicRemote=sess.getBasicRemote(); //RemoteEndpoint.Async
		 * asynchRemote=sess.getAsyncRemote(); if (bLowLevelIODebug) {
		 * System.out.println("send(" + us.username + "):" + msg); } /
		 * RemoteEndpoint.Basic basicRemote=sess.getBasicRemote(); if (sess.isOpen())
		 * try { basicRemote.sendText(msg); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * //asynchRemote.sendText(msg);
		 */
	}

	public void broadcast(String msg) {
		int i;
		int n = SessionManager.sessionListSize();
		for (i = 0; i < n; i++) {
			UserSession uSess = SessionManager.getUserSession(i);
			Session sess = uSess.getSession();
			// Basic br=sess.getBasicRemote();
			RemoteEndpoint.Basic basicRemote = sess.getBasicRemote();

			// RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote();
			if (bLowLevelIODebug) {
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

