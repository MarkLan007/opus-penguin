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
// convert to this url
//@ServerEndpoint("/server/ws/{client}")
//@ServerEndpoint("/websocket/{client}")
//@ServerEndpoint("/server/ws/{client}")
//@ServerEndpoint("/server/ws/websocket/{client}")
@ServerEndpoint("/server/ws")
public class WsServer {
	
	//public class CardGame { }
	
	@OnOpen
	public void onOpen(Session sess, EndpointConfig endpointConfig){
		sess.setMaxIdleTimeout(1000000);
		//RemoteBasicEndpoint rbe = sess.getBasicRemote();
		//sess.getBasicRemote().getClass().getAnnotations().getClass().getCanonicalName();
		String sClientName = "default";
		String sFriendlyName = "default";
		String sReallyFriendlyName =
				sess.getPathParameters().get("client");
		String sPathParams = sess.getPathParameters().toString();
		System.out.println("onOpen... Opening at:" + sPathParams);
		//sPathParams = sess.
		if (sess.getRequestParameterMap().containsKey("client")) {
			//sClientName = sess.p
			sClientName = sess.getRequestParameterMap().toString();
			sFriendlyName = sess.getRequestParameterMap().get("client").get(0);
			/* sClientName =
			  sess.getRequestParameterMap().getOrDefault("client"); */
			// sClientName = sess.getPathParameters().get("client").			
			System.out.println("Yea! retrieved client name is" + sClientName);
			System.out.println("Yea! retrieved client name is" + sFriendlyName);
			System.out.println("Yea! retrieved really friendly client name is" + sReallyFriendlyName);
		}
		System.out.println("Open Connection ...");
		String gensym=newSessionIdentifier();
		SessionManager.manageSession(sess, sFriendlyName, gensym);
		// call gensym to create a name
		// and set the session name to that.
		//  SessionManager.setSessionIdentifier(sess, gensym);
		
		// Catch up...
		retrievePreviousConversation(sess);
	}
	
	/*
	 * remove from broadcast queue...
	 * session has been closed
	 *  if user was in any games, notify the game and 
	 *  remove from the broadcast queue
	 */
	@OnClose
	public void onClose(Session sess) {
		UserSession us = SessionManager.getUserSession(sess);

		if (us == null)	// nothing more we can do...
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

	// returns a fake protocol message that will cause the score dialog to put it up for user
	String listGames() {
		String sTemp="";
		for (int i=0; i<cardGames.length; i++) {
			if (cardGames[i] == null)
				continue;
			sTemp += "$" + cardGames[i].sGameName + "="
					+ cardGames[i].nPlayers + "."
					+ cardGames[i].bGameInProgress ;
		}
		sTemp += '$';
		sTemp += "#game#Players#InProgress?#";
		ProtocolMessage returnMessage = new ProtocolMessage(ProtocolMessageTypes.GAME_QUERY,
				sTemp);

		return returnMessage.encode();
		/*
		 * 		for (i = 0; i < nPlayers; i++) {
			var sessionName="(none)";
			if (!playerArray[i].isRobot())
				sessionName = playerArray[i].userSession.sessionId;
					
			sTemp = sTemp + "$" + playerArray[i].getName() + "=" 
					+ sessionName + "."
					+ playerArray[i].isRobot() ;
		}
		sTemp += '$';
		sTemp += "#User#Session Id#IsRobot?#";

		 */
	}
	CardGame lookupGameFromSession(String sessionString) {
		if (sessionString.isEmpty())
			return cardGames[0];
		return cardGames[0];
	}
	boolean intern(CardGame g) {
		for (int i=0; i<cardGames.length; i++)
			if (cardGames[i] == null) {
				cardGames[i] = g;
				return true;
			}
		return false;
	}
	
	CardGame lookupGameByName(String sname) {
		int i;
		for (i=0; i<cardGames.length; i++)
			if (cardGames[i] != null &&
				sname.equalsIgnoreCase(cardGames[i].getName())) {
				// found it
				return cardGames[i];
			}
		return null;
	}
	void setNewDefaultGame() {
		cardGames[0] = new CardGame();
	}
	CardGame getDefaultGame() {
		if (cardGames[0] == null) {
			System.out.println("Generating new (default) game...");
			cardGames[0] = new CardGame();
			// cardGames[0].reset();	// but don't initiate play
			}
		return cardGames[0];		
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
	for (int n=0; n<sparam.length(); n++) {
		char c=sparam.charAt(n);
		if (c < '0' || c > '9') {
			return defaultValue;
		}
	}
	int number=Integer.parseInt(sparam);
	return number;
	}

	/*
	 * Main role of onMessage is to detect JCL commands and process them,
	 * but it also wraps the protocol messages by calling join with 
	 * the Session info necessary to write back to it.
	 * Subsequently it keeps track of what game the session is associated with
	 * so that it route messages to the right game. (held together in UserSession)
	 */
	@OnMessage
	public void onMessage(String message, boolean last, Session sess) {

		CardGame g=null;
		String sname="";
		String sparam="";

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
			String sRemoteSession = getSessionInfo(message);
			if (ProtocolMessage.isProtocolMessage(message)) {
				String msg=stripSessionInfo(message);
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
				//g=lookupGameFromSession(sRemoteSession);
				if (g == null)
					g = getDefaultGame();
					*/
				// tell the client what protocol message parsed as:
				if (remoteDebug)
					write(us, "saw:" + pm.type + "sender:" + pm.sender + "{" + pm.usertext + "}");
				g.process(pm);
/*
				if (bBypassKernel) {
					// sleep before every move to let writes finish
					// temporary hack...
					// xxx yyy
					/// Causes Tomcat warnings for memory leak?
					//    CardGameKernel.msleep(10);
					g.process(pm);
					// was...
					//us.game.process(pm);
					break;
				}
				/*
				 * cgk is completely disabled because
				 *  messages coming into onMessage are serialized
				 *
				us.cgk.enqueue(pm);
				// now tell the kernel to resume if it is idle...
				if (us.cgk.isIdle())
					us.cgk.resume();
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
			us.cgk.resume();
			break;
		case JCLResume:
			if (us.cgk.isIdle())
				us.cgk.resume();
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
			System.out.println("name="+sName);
			us.setName(sName);
			write(us, us.sessionId + us.getName());
			System.out.println("name="+sName);
			break;
		case JCLSuperUser:
			us.setSuperUser(true);
			write(us, "With great power comes great responsibility...");
			break;
		case JCLWho:
			int playerId=us.getpid();
			if (playerId == -1) {
				write(us, "Not currently in a game; can't get status");
				break;
			}
			us.game.sendFormattedPlayerInfo(playerId, us.isSuperUser());		
			break;
		case JCLPeek:
			// argc, argv...
			playerId=us.getpid();
			if (!us.isSuperUser()) {
				write(us, "No peeking!");
				break;
			}
			if (playerId == -1) {
				write(us, "Not currently in a game; can't peek");
				break;
			}
			String s = "";
			int nparam=0;
			sname="";
			sparam="";
			if (jcl.argc() > 1) {	// argc is always at least 1
				sname=jcl.getName(1);
				sparam=jcl.getValue(1);
				nparam = stringToNumber(sparam,0);
				}
			else {
				sparam = "?";
			}
			//us.game.sendFormattedPlayerInfo(playerId);		
			String cards=us.game.peek(nparam);
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
		case JCLStart:
			sname="";
			sparam="";
			if (jcl.argc() > 1) {	// argc is always at least 1
				sname=jcl.getName(1);
				sparam=jcl.getValue(1);
				}
			/*
			 * sparam is in reality ignored for now
			 */
			g=lookupGameFromSession(sparam);
			if (g == null)
				g = getDefaultGame();
			if (!g.start()) {
				write(us, "Play Initiated. Can't start. Use 'misdeal' or 'reset'  to reset.");
			}
			break;
		case JCLNew:	// create a new game...
			sname="";
			sparam="";
			String sUserMsg="new game created. Join in to keep playing!";
			int argc=jcl.argc();
			if (argc > 1) {	// argc is always at least 1
				sname=jcl.getName(1);
				sparam=jcl.getValue(1);
				sUserMsg = "new game created:" + sparam + ". Join by name to play!";
				g = new CardGame(sparam);
				if (!intern(g)) {
					write(us, "Created but can't intern game:" + g.getName());
				}
			else
				setNewDefaultGame(); 
				// No parameter? Just create a new nameless game
			}
			System.out.println("new game:" + sname);
			write(us, sUserMsg);
			break;
		case JCLLs:	// list available games...
			sname="";
			sparam="";
			// ggg
			sparam = listGames();
			write(us, sparam); // need a "wrap as protocol message command from game"
			break;
		case JCLJoin:
			// here is the confluence of the http server and the gameserver
			// create a game if one does not exist, and insert this session into it
			// xxx
			System.out.println("User: '" + us.username + "' Joining...");
			//boolean byGod=false;
			sname="";
			sparam="";
			if (jcl.argc() > 1) {	// argc is always at least 1
				sname=jcl.getName(1);
				sparam=jcl.getValue(1);
				g = lookupGameByName(sparam);
				}
			else {
				/*
				 * sparam needs to be managed more intensely
				 */
				/*
				 * Wait... there is no game in session... this is join after all...
				 */
				g = lookupGameFromSession(sparam);
			}
			if (g == null)
				g = getDefaultGame();
			us.game = g;
			String friendlyName=us.getName();
			friendlyName = g.uniqueName(friendlyName);
			if (!g.gameInProgress()) {
				write(us, "Game not in progress. Create a new one.");
				break;
			}
			boolean bJoinStatus = g.join(us, friendlyName);
			if (bJoinStatus) {
				// joined ok...
				//
				write(us, "Successfully joined game:" + g.sGameName + " as " + friendlyName + "...");
				write(us, "issue start command to initiate play.");
			}
			else if (!bJoinStatus  && (sparam.contains("bygod") ||
					(sname.contains("bygod")))) {
				broadcast("Game reset by divine providence. Mortals will need to rejoin.");
				System.out.println("Game reset by divine providence. Mortals will need to rejoin.");
				//byGod = true;
				g.reset();
				if (g.join(us, us.sessionId + "/God"))
					write(us, "%msg%ByGod join successfull");
				else
					write(us, "%msg%Divine intervention failed. Still can't join.");
				System.out.println("Apocalyptic reset complete...");
				//us.joinBygod();
			}
			else {
				System.out.println("New User cannot join.");
				write(us, "New User cannot join. Game full.");
				// Uh oh...
				// xxx
			}
			/*
			if (!us.join() && (sparam.contains("bygod") ||
					(sname.contains("bygod")))) {
				broadcast("Game reset by divine providence. Mortals will need to rejoin.");
				System.out.println("Game reset by divine providence. Mortals will need to rejoin.");
				byGod = true;
				us.joinBygod();
			}
			else if (!us.join()) {
				System.out.println("New User cannot join.");
				write(us, "New User cannot join. Game full.");
				// Uh oh...
				// xxx
			}
			*/
			break;
		case JCLRejoin:
			sparam="";
			if (jcl.argc() > 1) {	// argc is always at least 1
				sname=jcl.getName(1);
				sparam=jcl.getValue(1);
				}
			write(us, "Rejoin("+sparam+") by session not yet implemented.");
			// lookup usersession by session id
			// and take it over
			// request a resend on the joiners behalf
			// i.e. send a welcome message
			// 
			// can also (implement) capture a seat here...
			break;
		case JCLReset:
			/*
			 * Lookup game by session and reset...
			 */
			if (us.game != null)
				g = us.game;
			else
				g = getDefaultGame();
			write(us, "Game reset ordered.");
			System.out.println("Reset game ordered...");
			g.reset();
			/*
			if (us.game == null) {
				System.out.println("No game to reset.");
				break;
			}
			us.game.reset();
			*/
			break;
		case JCLResend:
			// not to be confused with not-yet-implemented
			// ... JCLRefresh which sends the whole context
			us.game.resend(us);
			break;
		case JCLRefresh:
			us.game.refresh(us.pid);
			break;
		case JCLScore:
			playerId=us.getpid();
			if (playerId == -1) {
				write(us, "Not currently in a game; can't get status");
				break;
			}
			us.game.sendScore(playerId);			
			//write(us, "" + jcl.type + "(" + playerId + "): under construction");
			break;
		case JCLStatus:
			playerId=us.getpid();
			if (playerId == -1) {
				write(us, "Not currently in a game; can't get status");
				break;
			}
			us.game.sendScore(playerId);			
			//write(us, "" + jcl.type + "(" + playerId + "): under construction");
			break;
		case JCLMisdeal:
			/*
			 * just deal again.
			 *  -- don't show score, don't advance pass type
			 */
			if (us.game == null) {
				write(us, "Can't declare a misdeal. You aren't in a game.");
				break;
			}
			if (jcl.type == UserJCLCommand.JCLType.JCLMisdeal) {
				write(us, "Misdeal declared:");
				System.out.println("Player(" + us.getpid() +") declares misdeal.");				
			}
			us.game.reset();
			// does not advance passtype!
			//us.game.start();
			write(us, "Reseting Hand: passtype unchaged");
			break;
		case JCLNewdeal:
			/*
			 * just skip to the next deal, as if the last
			 * trick was played out
			 *  -- show score, advance pass type
			 */
			if (us.game == null) {
				write(us, "Can't redeal. You aren't in a game.");
				break;
			}
			if (jcl.type == UserJCLCommand.JCLType.JCLMisdeal) {
				write(us, "Misdeal declared:");
				System.out.println("Player(" + us.getpid() +") declares misdeal.");				
			}
			us.game.handOver();
			write(us, "newdeal: passtype advanced.");
			// only way to advance passtype!
			break;
		case JCLShuffle:
			// set shuffle game-wide
			// for debugging purposes.
			String sShuffleType="";
			if (jcl.argc() > 1)
				sShuffleType = jcl.getValue(1);
			else
				sShuffleType = "none";
			// Can't do that here. This will shuffle with the default type...
			// g = getDefaultGame();
			if (Subdeck.isValidShuffleType(sShuffleType)) {
				// unfortunately cards are distributed at join
				// to affect the shuffle, must do it globally
				//us.game.setShuffle(sShuffleType);
				Subdeck.setShuffle(sShuffleType);
				write(us, "shuffle:" + sShuffleType);
			}
			else
				write(us, "invalid shuffle type:" + sShuffleType 
						+ "-use none, random, clubs, or high");
			//write(us, "" + jcl.type + "(" + sShuffleType + "): under construction");
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
	boolean bNeverSleep=true;
	public void write(UserSession us, String msg) {
		Session sess=us.getSession();
		if (bNeverSleep)
			;
		else if (bBypassKernel)
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
		//boolean verbose=true;	// local version...
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

			//RemoteEndpoint.Async asynchRemote=sess.getAsyncRemote(); 			
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

