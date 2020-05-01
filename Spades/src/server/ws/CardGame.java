package server.ws;

//Not used any more since server.ws integration
//package cardGame;
//import java.nio.channels.SocketChannel;
//import java.util.LinkedList;

public class CardGame implements GameInterface {
	int nPlayers = 4;
	private int nCurrentTurn = -1; // index of player with current turn or -1 if undefined
	Trick[] trickArray = new Trick[(52 / nPlayers) + 1];
	Trick currentTrick;
	final int LAST_TRICK = (52 / nPlayers);
	MailBoxExchange.PassType currentPass = MailBoxExchange.last();
	MailBoxExchange mbx = null;
	private int nTrickId = 0;

	public int getCurrentTrickId() {
		return nTrickId;
	}

	boolean bDebugCardCf = true;
	boolean bGameAborted = false;
	boolean bHandOver = false;
	boolean bGameInProgress = true;

	String sGameName = "_AnonHearts";

	public boolean gameInProgress() {
		return bGameInProgress;
	}

	/*
	 * TODO: global game shouldn't be managed this way; This is sad, of course; but
	 * there is only one game for now, and it is created in ttyhandler or WsServer
	 * Let him who is without sin cast the first stone. (Hey, I'll fix it soon...)
	 */
	static public CardGame theGame = null;

	Trick getCurrentTrick() {
		return currentTrick;
	}

	int getTurn() {
		return nCurrentTurn;
	}

	// some special cards
	final Card deuceClubs=new Card(Rank.DEUCE, Suit.CLUBS);
	final Card queenSpades=new Card(Rank.QUEEN, Suit.SPADES);
	
	/*
	 * true if in this GAME cfirst is higher than csecond; assume csecond was the
	 * one lead (or beat the one lead) isHigher(2C,AC) -> false isHigher(2C,3C) ->
	 * false isHigher(AC,3C) -> true isHigher(AH,2C) -> false
	 */
	boolean isHigher(Card cfirst, Card csecond) {
		/*
		 * if the suits are different the first card wins
		 */
		if (bDebugCardCf)
			gameErrorLog("?isHigher(" + cfirst.encode() + "," + csecond.encode() + ")");

		if (cfirst.suit != csecond.suit)
			return false;
		/*
		 * otherwise compare ranks, not ace is high
		 */
		if (csecond.rank == Rank.ACE)
			return false;
		if (cfirst.rank == Rank.ACE)
			return true;
		if (cfirst.rank.ordinal() > csecond.rank.ordinal())
			return true;
		else
			return false;
	}

	/*
	 * The hack that never got implemented: See "step" in ttyhandler boolean
	 * bStepTurns = false; // this and game stepping isn't really completely
	 * implemented, is it...
	 * 
	 * // Torn on game tracing: stepTurns(true); void stepTurns(boolean bOnOff) {
	 * bStepTurns = bOnOff; } boolean stepTurns() { return bStepTurns; }
	 */

	/*
	Boolean bShuffle = false;
	String sShuffleType = "none";
	
	void setShuffle(String stype) {
		bShuffle = true;	// obsolete... TODO: delete this boolean
		sShuffleType = stype;
	}*/

	// see 'new pack'

	Player[] playerArray = new Player[nPlayers];
	// Subdeck[] playerCards = new Subdeck[nPlayers]; // Key point: the game's
	// official copy of what's in the hand NOT part of player...
	/*
	 * Note: Must be able to change player without changing what's in the official
	 * part of the hand...
	 */

	// create players list, a deck (without faces), and names
	CardGame() {
		// default constructor; everything hunky dory with default values.

		// the name of the game is...
		// gameName = "Hearts";

		populatePlayers();
		// xxx reset as part of creation?
		resetGameScores();
		reset();
	}

	public void setName(String s) {
		sGameName = s;
	}

	public String getName() {
		return sGameName;
	}

	CardGame(String gamename) {
		this();
		setName(gamename);
	}

	private void populatePlayers() {
		//
		// Create robotPlayer interfaces to handle player messages...
		//
		int i;
		playerArray = new Player [nPlayers];
		for (i = 0; i < nPlayers; i++)
			if (playerArray[i] == null) {
				playerArray[i] = new RobotPlayer(i, this);
			} else {
				// player/robot player already exists, but since this is a
				// reset/new game clear out existing hand
				// xxx
				Player p = playerArray[i];
				System.out.println("Reset: Seat(" + i + ")" + p.getName() + p.getPID() + "(robot?" + p.isRobot() + ')');
				// robot reset isn't working.
				// just create a new one...
				if (p.isRobot())
					playerArray[i] = new RobotPlayer(i, this);
				else
					playerArray[i].reset();
			}
	}

	/*
	 * for (i=0; i<humanPlayerFree; i++) { //playerArray[i].isRobot = false; //
	 * playerArray[i].humanInterface = humanPlayers[i]; HumanPlayer hp = new
	 * HumanPlayer(i, humanPlayers[i], this); // // This setPID can only be done
	 * here because it is game specific, although the NIO layer must remember it. //
	 * TODO: bSuperUser should be set up here too. // (It's game specific. Some
	 * games may not allow there to be superusers...) // not needed: //
	 * hp.nioNetworkAccessMethods.setPID(i);
	 * 
	 * playerArray[i] = hp; // // Set the player-layer of value //
	 * playerArray[i].setPID(i); playerArray[i].setIsRobot(false);
	 * playerArray[i].setAsynch(true); }
	 * 
	 * }
	 */
// end delete nio routines...

	/*
	 * Player p joins the game. so... if playertype is robot player, then replace
	 * with human player... -> See join(us,name) Note: robot players never join;
	 * they are added by populatePlayers
	 */
	/*
	 * This is obsolete and now wrong... boolean join(Player p) { int i; //
	 * Obsolete... always called // xxx
	 * //System.out.println("Obsolete function called:" + "join"); for (i = 0; i <
	 * nPlayers; i++) { if (playerArray[i] == null) { // playerArray[i] = p; //
	 * p.setPID(i); return true; } // // kick out first robot player I find and
	 * replace, taking the subdeck, and move Player cp = playerArray[i]; if
	 * (cp.isRobot()) { // escort player from game // todo: robot should send
	 * departing words... copyPlayer(cp,p); // p.subdeck = cp.subdeck; // p.score =
	 * cp.score; // p.setPID(cp.pid); playerArray[i] = p; return true; } } return
	 * false; }
	 */

	/*
	 * TODO: allow ability to add multiple humans Put human player into seat zero
	 * always, if there is a current game for now ... he will be dealt in at reset
	 * at any rate
	 */
	// No longer need NIOClientSessions since websockets are full duplex
	// NIOClientSession[] humanPlayers=new NIOClientSession[10];
	private int humanPlayerFree = 0;

	/*
	 * channelHasOwner -- take an accepted channel, and assume that it belongs to
	 * the last player to add as that players second channel that they will send
	 * back their moves on...
	 */
	/*
	 * NIOClientSession getChannelOwner(SocketChannel chann) { int i; for (i=0;
	 * i<humanPlayerFree; i++) if (humanPlayers[i].incomingChann == null) {
	 * humanPlayers[i].incomingChann = chann; return humanPlayers[i]; } return null;
	 * }
	 */

	/*
	 * boolean join(NIOClientSession human) { int replacedPlayer = 0;
	 * 
	 * gameErrorLog("Received connection and creating humanplayer.");
	 * humanPlayers[humanPlayerFree] = human; humanPlayerFree++; //Humans are here;
	 * reset the game... reset(); return true; }
	 */

	/*
	 * join(s) -- where s is "sessionid/friendlyName"
	 */
	/*
	 * boolean join(String s) { Player p = new Player(s); // doesn't check to see if
	 * already in the game... return join(p); }
	 */

	/*
	 * copyPlayer Note: if copying from human to robot set the sessionId but if
	 * copying from robot to human DO NOT set the sessionId
	 */
	void copyPlayer(Player from, Player to) {
		to.pid = from.pid;
		if (from.isRobot())
			;
		else
			to.userSession = from.userSession;
		to.bIsMyMove = from.bIsMyMove;
		to.subdeck = from.subdeck;
		to.handScore = from.handScore;
		to.totalScore = from.totalScore;
	}

	/*
	 * passed user session (for communications wiring) and internal session name
	 * called by WsServer -- called any time human player joins -- -- this is the
	 * only working/supported version of join --
	 */
	boolean join(UserSession us, String friendlyName) {
		HumanPlayer hp = new HumanPlayer(us);
		hp.setName(friendlyName);
		// todo: multiple human players...
		// find a robot player to displace
		int i;
		for (i = 0; i < nPlayers; i++) {
			Player p = playerArray[i];
			// take over the robot players seat
			// set the pid, and take the cards, and turn
			if (p.isRobot()) {
				// escort player from game
				// todo: robot should send departing words...
				// i.e. call player.disconnet() for robots?
				copyPlayer(p, hp);
				playerArray[i] = hp;
				us.setpid(i);
				us.setgame(this);
				return true;
			}

		}
		return false;
		// console.log("Cannot add player...")
	}

	/*
	 * TODO: actually use getPlayer instead of pulling things out of PlayerArray
	 */
	Player getPlayer(int index) {
		if (index >= 0 && index < nPlayers)
			return playerArray[index];
		return null;
	}

	/*
	 * resend the cards and game situation to us
	 */
	void resend(UserSession us) {
		System.out.println("Resending to:" + us.username);
		for (int i = 0; i < nPlayers; i++) {
			Player p = playerArray[i];
			if (p.userSession == us) {
				// found it!
				ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.ADD_CARDS, p.subdeck);
				// make sure the recipient is correct...
				// here, or a global problem? XXX
				pm.setSender(i); // pid should work, too...
				p.sendToClient(pm);
				// also send current trick and whether your turn
				return;
			}
		}

		System.out.println("Canot find player in game to resend to:" + us.username);
	}

	void gameErrorLog(String sError) {
		MainServer.ttyLogString(sError);
	}

	/*
	 * TODO: getGameStatus compare score totaling with other routine totalScores()
	 * and consolodate
	 */
	String getGameStatus() {
		return getFormattedGameScore();
		/*
		 * String sStatus = ""; // // Format status string for (int i = 0; i < nPlayers;
		 * i++) { String sCurrent = ""; if (playerArray[i] != null) { Player p =
		 * playerArray[i]; sCurrent = p.playerName + '.' + p.handScore + "." +
		 * p.totalScore; if (nCurrentTurn == i) sCurrent = '#' + sCurrent; } sStatus =
		 * sStatus + '$' + sCurrent; } sStatus = sStatus + '$'; return sStatus;
		 */
	}

	/*
	 * called when a player's turn is complete (doesn't consider whether game is over)
	 */
	void resetTurn() {
		nCurrentTurn = -1;
	}

	void updateTurn() {
		/*
		 * Current turn is set for the first turn at deal (2c)
		 */
		if (nCurrentTurn == -1)
			return;

		nCurrentTurn++;
		if (nCurrentTurn >= nPlayers)
			nCurrentTurn = 0;
	}

	void updateTurn(int nplayer) {
		nCurrentTurn = nplayer;
	}

	/*
	 * get the game's idea of what cards a player has
	 */
	Subdeck getCards(int iplayer) {
		Player p = playerArray[iplayer];
		if (p == null)
			return null; // can't happen
		return p.subdeck; // subdeck is the server side representation of the cards that are dealt or
							// passed to the hand
	}

	/*
	 * tell the (next or first) player to make a move by sending it YOUR_TURN. cf
	 * updateTurn(); TODO: send the subdeck of the current trick.
	 * 
	 * Ok, this seems like it's really wrong... This was an infinite that
	 * continually sent YOUR_TURN to the same client... It should send it once, and
	 * the response will come back either from human player or robot player thereby
	 * moving the game along...
	 */
	void sendFirstMove() {
		/*
		 * figure out who has the two set 'your move' for that player call send next
		 * move
		 */
	}

	void sendNextMove() {

		// while (nCurrentTurn != -1) {
		if (nCurrentTurn == -1) {
			// really just the hand is over, not the game...
			// TODO:
			// so should rotate the pass order pt=nextPassType();
			// shuffle and deal
			//
			//  is this where I should shuffle and deal?
			handOver();
			return;
		}
		ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.YOUR_TURN);
		Player p = playerArray[nCurrentTurn];
		/*
		 * check if player has any cards left to play; if not return... ?
		 */
		// This sends to client; fine;
		// pause before processing any response when stepping
		/*
		 * This message should included the cards already played in the trick.. xxx ,,,
		 * No it shouldn't. But it should send a %msg text attachment if first move,
		 * ?0%msg: lead the 2c if leading, ?0%msg: your lead else ?0%msg: your turn
		 */
		/*
		 * TODO: should not send your turn if the game is over...
		 */
		String msg;
		if (nCurrentTurn == -1 && (currentTrick == null || currentTrick.subdeck.size() == 0))
			msg = "%Play the 2 of clubs";
		else if (currentTrick == null || currentTrick.subdeck.size() == 0) // i.e. your leading, and not first trick
			msg = "%Your lead.";
		else
			msg = "%Your turn.";
		pm.setUsertext(msg);
		p.sendToClient(pm);
		// }
		if (nCurrentTurn == -1) {
			gameErrorLog("nCurrentTurn went to -1. Hand over...");
			handOver();
		}
	}

	/*
	 * initiatePass -- set up exchnage mail boxes, set routing, and send the pass
	 * cards request
	 * 
	 * don't forget... At end of hand, set currentPass to the next passtype
	 */
	void initiatePass(MailBoxExchange.PassType pt) {
		if (pt == MailBoxExchange.PassType.PassHold)
			return;
		// number of players, number of cards to pass for error checking
		mbx = new MailBoxExchange(pt, nPlayers, 3);
		// Now send the pass messages, telling the user what
		// the pass type is. Remember...
		// cardString of the form 'NCards to pass left' where N is the actual number
		mbx.setRouting(pt);
		ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.PASS_CARD,
				3 + "Pass 3 cards to the " + currentPass);
		broadcastUpdate(pm);
	}

	/*
	 * one of these is enough void initiatePass() { MailBoxExchange.PassType pass;
	 * 
	 * pass = MailBoxExchange.PassType.PassHold; if (pass ==
	 * MailBoxExchange.PassType.PassHold) return; // number of players, number of
	 * cards to pass for error checking MailBoxExchange mbx = new
	 * MailBoxExchange(currentPassType, nPlayers, 3); // Now send the pass messages,
	 * telling the user what ProtocolMessage pm = new
	 * ProtocolMessage(ProtocolMessageTypes.PASS_CARD, 3 + "Pass 3 cards to the " +
	 * currentPassType); broadcastUpdate(pm); }
	 */

	/*
	 * WTF version of broadcastUpdate...
	 *
	 */
	/*
	 * broadcastUpdate - send the same message to all the players works even if
	 * there is no current turn... i.e. during pass
	 */
	void broadcastUpdate(ProtocolMessage pmsg) {
		int i, j;
		if (nCurrentTurn > -1)
			j = nCurrentTurn;
		else
			j = 0;
		for (i = 0; i < nPlayers; i++) {
			Player p = playerArray[j++];
			p.sendToClient(pmsg);
			if (j >= nPlayers)
				j = 0;
		}

	}

	boolean validPid(int pid) {
		if (pid >= 0 && pid < nPlayers)
			return true;
		return false;
	}

	// i.e. who
	void sendFormattedPlayerInfo(int pid) {
		if (!validPid(pid))
			return;
		Player p = playerArray[pid];
		System.out.println("New Query Code...");
		ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.GAME_QUERY, getFormattedPlayerInfo());
		p.sendToClient(pm);
	}

	void sendFormattedPlayerInfo(int pid, boolean su) {
		if (!validPid(pid))
			return;
		Player p = playerArray[pid];
		System.out.println("New Query Code...");
		String playerinfo;
		if (su)
			playerinfo = getFormattedPlayerHand();
		else
			playerinfo = getFormattedPlayerInfo();
		ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.GAME_QUERY, playerinfo);
		p.sendToClient(pm);
	}

	String peek(int pid) {
		String cardString = "Invalid Pid";
		if (pid >= 0 && pid < nPlayers) {
			Player p = playerArray[pid];
			Subdeck sd = p.subdeck;
			cardString = sd.encode();
		}
		return cardString;
	}

	String getFormattedPlayerInfo() {
		int i;
		String sTemp = "";
		for (i = 0; i < nPlayers; i++) {
			var sessionName = "(none)";
			if (!playerArray[i].isRobot())
				sessionName = playerArray[i].userSession.sessionId;

			sTemp = sTemp + "$" + playerArray[i].getName() + "=" + sessionName + "." + playerArray[i].isRobot();
		}
		sTemp += '$';
		sTemp += "#User#Session Id#IsRobot?#";
		return sTemp;

	}

	String getFormattedPlayerHand() {
		int i;
		String sTemp = "";
		for (i = 0; i < nPlayers; i++) {
			var sessionName = "(none)";
			if (!playerArray[i].isRobot())
				sessionName = playerArray[i].userSession.sessionId;

			sTemp = sTemp + "$" + playerArray[i].getName() + "=" + sessionName + "." + playerArray[i].subdeck.encode();
		}
		sTemp += '$';
		sTemp += "#User#Session Id#IsRobot?#";
		return sTemp;

	}

	String getFormattedGameScore() {
		int i;
		String sTemp = "";
		boolean bGameInProgress = nCurrentTurn != -1;
		String x = "";
		for (i = 0; i < nPlayers; i++) {
			if (bGameInProgress && i == nCurrentTurn)
				x = "*";
			else
				x = "";
			sTemp = sTemp + "$" + x + playerArray[i].getName() + "=" + playerArray[i].handScore + "."
					+ playerArray[i].totalScore;
		}
		return sTemp + '$' + "#Player#Points#Totals#";
	}

	private int gensymName = 1;

	String uniqueName(String fname) {
		for (int i = 0; i < nPlayers; i++)
			if (playerArray[i].playerName.equalsIgnoreCase(fname))
				return fname + gensymName++;
		return fname;
	}

	/*
	 * totalScores - total the scores for the hand checks for moonshooting and
	 * game-end
	 */
	void totalScores() {
		int i, iTrickTotal;
		for (i = 0; i < nTrickId; i++) {
			Trick t = trickArray[i];
			// sum up the no of hearts in the trick, the QS and give to the winner
			iTrickTotal = 0;
			for (Card c : t.subdeck.subdeck) {
				if (c.suit == Suit.HEARTS)
					iTrickTotal++ ;
				else if (c.rank == Rank.QUEEN && c.suit == Suit.SPADES)
					iTrickTotal = iTrickTotal + 13;
			}
			playerArray[t.winner].handScore = playerArray[t.winner].handScore + iTrickTotal;
		}
		/*
		 * Check to see if someone shot the moon! shot the moon if p[i] has pts && no
		 * one else does so count the number of nonzero pts
		 */
		int playersWithPts = 0;
		int moonShooter = -1;
		for (i = 0; i < nPlayers; i++)
			if (playerArray[i].handScore > 0) {
				playersWithPts++;
				moonShooter = i;
			}
		if (playersWithPts == 1) {
			// then moonShooter is not -1 and shot the moon
			for (i = 0; i < nPlayers; i++)
				if (i == moonShooter)
					playerArray[i].handScore = 0;
				else
					playerArray[i].handScore = 26;
		}
		/*
		 * Not here... do this in turn if (winnerDetermined) handOver();
		 *   Wait, shouldn't I do a resetHand here?
		 *   scores are totaled but players are not notified.
		 */
		// resetHand();
		// ToDo:
		// should sort by total score...
	}

	/*
	 * Update the trick with the (legally played) card, send updated trick to
	 * players
	 */
	void trickUpdate(int nsender, Card card) {
		/*
		 * add the card to the trick's subdeck, and see if hearts are broken
		 */
		currentTrick.subdeck.add(card);
		if (card.suit == Suit.HEARTS) {
			currentTrick.breakHearts();
		}

		/*
		 * broadcast the played card to all the players (no matter what: whether last
		 * card or not)
		 */
		ProtocolMessage bmsg = new ProtocolMessage(ProtocolMessageTypes.CURRENT_TRICK, card);
		bmsg.setSender(nsender);
		broadcastUpdate(bmsg);
		updateTurn();

		/*
		 * i.e Everyone has played, determine the winner.
		 */
		if (currentTrick.subdeck.size() >= nPlayers) {
			currentTrick.bClosed = true;
			// Figure out who won, set bWinner;
			int i = 0;
			Card leadingCard = null;
			/*
			 * everyone has played, determine who won the trick, and set it closed
			 */
			for (Card c : currentTrick.subdeck.subdeck) {
				// the actual player who won the trick is n players away from the leader or the
				// leader
				if (i == 0) {
					leadingCard = c;
					currentTrick.winner = currentTrick.leader;
				} else if (isHigher(c, leadingCard)) { // true if c is higher than leadingcard
					if (bDebugCardCf)
						gameErrorLog("->T");
					leadingCard = c;
					currentTrick.winner = (i + currentTrick.leader) % nPlayers;
				} else {
					if (bDebugCardCf)
						gameErrorLog("->F");
					// card was sloughed
				}
				i++;
			}	// if trick is closed

			nTrickId++;
			/*
			 * now broadcast the completed trick with a CURRENT_TRICK message for the closed
			 * trick; Encode the trick and send it.
			 */
			bmsg = new ProtocolMessage(ProtocolMessageTypes.TRICK_CLEARED);
			bmsg.trick = currentTrick;
			broadcastUpdate(bmsg);

			Trick lastTrick = currentTrick;
			gameErrorLog("TrickCleared:" + nTrickId + " Leader:" + currentTrick.leader + "Winner:" + lastTrick.winner
					+ "<" + currentTrick.subdeck.encode() + ">");
			/*
			 * prepare for the next trick..
			 */
			currentTrick = new Trick(nTrickId);
			if (lastTrick.heartsBroken())
				currentTrick.breakHearts();
			currentTrick.leader = lastTrick.winner; // the leader is the player who won the last trick...
			trickArray[nTrickId] = currentTrick;
			/*
			 * the person who plays next is the current trick winner!
			 */
			updateTurn(lastTrick.winner);
			if (nTrickId >= LAST_TRICK) {
				// Game is over; total score and reset
				totalScores();
				resetTurn();	
				// no next turn till after a new deal
				// Don't resetHand here... it clobbers the scores
				// before anyone sees them...
				//resetHand();
				// should just reset hand...
				// Don't be too quick to reset
				// reset(true); // shuffle this time...
				return;
			}
		} // if everyone has played; trick is cleared

	} // trickUpdate

	/*
	 * playCard - detect if the sender can legally play the card; emit error message
	 * if not if legal, send updates and progress turn
	 */
	public void playCard(int nsender, Card c) {
		Player p = playerArray[nsender];
		Subdeck pcards = p.subdeck; // server side official representation of the player's hand
		ProtocolMessage returnMessage = null;
		ProtocolMessageTypes mtype = ProtocolMessageTypes.PLAY_CARD;

		gameErrorLog("Msg<" + mtype + ">from(" + nsender + ") " + pcards.encode());
		if (c == null) {
			returnMessage = new ProtocolMessage(ProtocolMessageTypes.PLAYER_ERROR, "%MSG:Protocol error no card!%");
			p.sendToClient(returnMessage);
			return;
		}
		gameErrorLog("Housekeeping: player(" + nsender + ") plays <" + c.encode() + ">");

		if (!pcards.find(c)) {
			gameErrorLog("Housekeeping: find failed<" + c.encode() + "> from(" + nsender + ") subdeck size("
					+ pcards.size() + "){" + pcards.encode() + "}");
			returnMessage = new ProtocolMessage(ProtocolMessageTypes.PLAYER_ERROR,
					"%MSG:Player" + p.pid + " doesn't have <" + c.rank + c.suit + ">!%");
			p.sendToClient(returnMessage);
			return;
		}
		//
		// So I know that the player has that card.
		// easy restriction on the first trick
		if (nTrickId == 0) {
			// does the player have the 2c and didn't play it?
			if (!c.equals(deuceClubs) && p.has(deuceClubs)) {
				returnMessage = new ProtocolMessage(ProtocolMessageTypes.PLAYER_ERROR,
						"%MSG:Player" + p.pid + " must lead <" + deuceClubs.rank + deuceClubs.suit + ">!%");
				p.sendToClient(returnMessage);
				return;
			}
		}
		// trickier restrictions on the first trick
		// did the player lay a the qs?
		// did the player lay a heart (and had non-hearts)
		if (nTrickId == 0) {
			if (c.suit == Suit.HEARTS) {
				// almost certainly a foul but check...
				//int nonHearts=
				int nonPoints = p.countNon(Suit.HEARTS);;
				if (p.has(queenSpades))
					nonPoints--;
				if (nonPoints > 0) {
					returnMessage = new ProtocolMessage(ProtocolMessageTypes.PLAYER_ERROR,
							"%MSG:Player" + p.pid + " Illegal:" + c.encode() + ". must play <Non-QS Non-Heart Card>!%");
					p.sendToClient(returnMessage);
					return;
				}
			}
		}

		// Is this a legal follow?
		// It's a legal follow if it's the same as the lead
		// or if it's a slough then the player doesn't have any cards the same suit as
		// the lead
		if (currentTrick.subdeck.size() > 0) { // a follow
			Card cLead = currentTrick.subdeck.peek();
			Suit leadSuit = cLead.suit;
			if (c.suit == leadSuit) {
				// Yea! Player followed suit. It's legal.
			} else {
				// didn't follow lead. Is that ok?
				if (!p.subdeck.isVoid(leadSuit)) {
					// Uh oh. Player could have followed suit but didn't. Error detected!
					returnMessage = new ProtocolMessage(ProtocolMessageTypes.PLAYER_ERROR,
							"%MSG:Player" + p.pid + " required to follow suit <" + leadSuit + ">!%");
					p.sendToClient(returnMessage);
					return;
				}
				if (c.suit == Suit.HEARTS) {
					// Hearts are broken
					currentTrick.breakHearts();
					// broadcast message?
				}
			}
		} else { // lead...
			// Is this a legal lead? hhh
			// If it's a heart, then hearts must be broken, or only has hearts
			if (c.suit == Suit.HEARTS) {
				// lead a heart. Is that ok?
				if (currentTrick.heartsBroken())
					; // ok
				else if (p.subdeck.hasOnly(Suit.HEARTS))
					; // ok
				else {
					returnMessage = new ProtocolMessage(ProtocolMessageTypes.PLAYER_ERROR,
							"%MSG:Player" + p.pid + " Cannot lead a heart until hearts are broken!%");
					p.sendToClient(returnMessage);
					return;
				}
			}
		}

		// TODO: add to the trick and send to players
		trickUpdate(nsender, c);

		/*
		 * Delete the games copy of the card in hand, and then send message to client to
		 * delete the same card
		 */
		gameErrorLog("Housekeeping: delete<" + c.encode() + "> from {" + pcards.encode() + "}");
		pcards.delete(c.rank, c.suit);
		returnMessage = new ProtocolMessage(ProtocolMessageTypes.DELETE_CARDS, c);
		p.sendToClient(returnMessage);
		gameErrorLog("Play_Card successful.");
		/*
		 * A turn has successfully completed. the next player is up (updateTurn). then
		 * call nextMove() to enqueue message.
		 */
		// No: don't do this if there is a current turn winner which was already set by
		// trickupdate...
		// Note: updateturn is set in trickupdate...
		// updateTurn();
		sendNextMove();
		// break;
	}

	// * xxx new (but unimplemented) gameInterface methods here...

	/*
	 * send client an error message sError with string s in the message format that
	 * the client can display, and log it
	 */
	public void sendPlayerUserErrorMsg(Player p, String sError) {
		gameErrorLog("Error:" + p.pid + ")" + sError);
		ProtocolMessageTypes mtype = ProtocolMessageTypes.PLAYER_ERROR;
		ProtocolMessage pm = new ProtocolMessage(mtype, "%MSG:" + sError + "%");
		p.sendToClient(pm);
	}

	/*
	 * passCards - detect if the sender can legally pass these cards; TODO: i.e. are
	 * we in a pass? emit error message if not if legal, add to the mailbox
	 */
	public void passCards(int nsender, Subdeck sd) {
		int recipient = mbx.lookupRecipient(nsender);

		mbx.route(recipient, sd);
		if (!mbx.isFull())
			return; // waiting for more players to pass their cards
		//
		// Mailbox is full! time to route cards to players, deleting old cards
		// and start play!
		// foreach mailbox, delete cards in from and add cards to to;
		for (int i = 0; i < mbx.size(); i++) {
			MailBoxExchange.MailBox mb = mbx.itemAt(i);
			int from = mb.from, to = mb.to;
			sd = mb.contents;
			System.out.println("Passing:(" + sd.size() + ")=" + sd.toString());
			Player p = playerArray[from];
			/*
			 * Actually delete the cards to the player's hand internally for (c: sd.subdeck)
			 * { }
			 */
			// delete the card from the player's (server-side representation) of
			// the hand i.e. subdeck. (ss routines)
			// i.e. the game has to know the actual state of the player's hand
			for (var c : sd.subdeck)
				p.ssDeleteCard(c);
			ProtocolMessage pmFrom = new ProtocolMessage(ProtocolMessageTypes.DELETE_CARDS, sd);
			// is iterator destructive? Seems to be. Recreate subdeck...
			p.sendToClient(pmFrom);
			sd = mb.contents;
			// actually add the card to the player subdeck
			p = playerArray[to];
			for (var c : sd.subdeck)
				p.ssAddCard(c);
			ProtocolMessage pmTo = new ProtocolMessage(ProtocolMessageTypes.ADD_CARDS, sd);
			p.sendToClient(pmTo);
		}
		// yea! Pass successfully completed
		// buckle-up...
		go();
	}

	public void bidTricks(int nsender, Subdeck cards) {
		gameErrorLog("Can't happen: Game received unimplemented BID request. Bidding not yet implemented.");
	}

	public void query(int nsender, String s) {
		gameErrorLog("Can't happen: Game received unimplemented QUERY_* request.");
	}

	public void superuser(int nsender, String s) {
		gameErrorLog("Can't happen: Game received unimplemented SUPERUSER request.");
	}

	/*
	 * process a (well-formed previously parsed) protocol message from a client in a
	 * game context
	 * 
	 * .. call serverErroLog(string) to log errors
	 */
	boolean bMessageDebug = true;
	private boolean bPlayInitiated = false;

	void process(ProtocolMessage m) {
		if (bMessageDebug)
			System.out.println("CGProcess: " + m.type + ":" + m.usertext);

		// ..is there such a player?
		int nSender = m.sender;
		if (nSender < 0 || nSender >= nPlayers) {
			// no such player; I'm somehow processing a kernel message; can't happen
			gameErrorLog("Can't happen: Game sent a game kernel message. msg ignored.");
			return;
		}
		Player p = playerArray[nSender];
		Subdeck pcards = p.subdeck; // server side official representation of the player's hand
		ProtocolMessage returnMessage = null;

		gameErrorLog("Msg<" + m.type + ">from(" + nSender + ") " + pcards.encode());

		switch (m.type) {
		case PLAY_CARD: // CARD
			// Whose turn is it? Is it from the right user
			// is it a legal play?
			// ..does player have that card?
			// ..hearts/spade? Are they Broken
			// if legal, add card to the trick
			// Send msg to delete card from the players hand with a protocol msg.
			// delete card from players internal representation subdeck
			// update the turn, and send trick to next player
			// is the sender the player with the turn?
			if (nSender != nCurrentTurn) {
				returnMessage = new ProtocolMessage(ProtocolMessageTypes.PLAYER_ERROR,
						"%MSG:Not your turn player" + nSender + ". It's Player" + nCurrentTurn + "'s turn!%");
				p.sendToClient(returnMessage);
				return;
			}
			// get the card played
			// gameErrorLog("Housekeeping: play<" + m.subdeck.encode() + ">");
			Card c = m.subdeck.peek();
			playCard(nSender, c);
			break;

		case PASS_CARD: // CARD*
			// is it appropriate to pass card now?
			// does the player exist and have that card?
			// determine who card should be sent to
			// Send msg to delete card from the senders hand
			// has the player to whom the card should be sent passed cards? If not queue it
			// up
			// empty queue of passes if any built up
			//
			// todo: emit error if not in a pass
			System.out.println("Server>Pass UserText:" + m.usertext);
			Subdeck passcards = new Subdeck(m.usertext);
			passCards(nSender, passcards);
			// make sure I'm in a pass
			// make sure user has cards. If not reject and resend pass message.
			// ...Here? or in passcards...
			break;

		case GAME_QUERY:
			System.out.println("New Query Code...");
			returnMessage = new ProtocolMessage(ProtocolMessageTypes.GAME_QUERY, getFormattedGameScore());
			p.sendToClient(returnMessage);
			break;

		case SUPER_USER:
			String sStatus = getGameStatus();
			break;
		default:

			// Unhandled message in process game kernel.
			// TODO: Log errors, etc.
		}
	}

	void sendScore(int playerId) {
		// xxx
		Player p = playerArray[playerId];
		String sTemp = getFormattedGameScore();
		ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.PLAYER_SCORES, sTemp);
		pm.setSender(playerId);
		p.sendToClient(pm);
	}

	public boolean isAborted() {
		return bGameAborted;
	}

	void gameOver() {
		bGameInProgress = false;
	}

	/*
	 * handOver - normal successful completion display scores and determine if game
	 * is over
	 */
	void handOver() {
		bHandOver = true;
		bPlayInitiated = false;
		boolean winnerDetermined = false;
		gameErrorLog("Hand completed.");

		// Total the score
		for (int i = 0; i < nPlayers; i++) {
			playerArray[i].totalScore += playerArray[i].handScore;
			if (playerArray[i].totalScore >= 100)
				winnerDetermined = true;
		}

		// at this point hand has normally and successfully finished
		// get the score and broadcast it
		String sTemp = getFormattedGameScore();
		ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.PLAYER_SCORES, sTemp);
		broadcastUpdate(pm);

		/*
		 * the only way to advance the current pass is to successfully complete a hand.
		 */
		currentPass = currentPass.next();
		System.out.println("Next pass:" + currentPass);
		// indicate game over or reset the hand
		if (winnerDetermined)
			gameOver();
		else {
			reset();
		}
	}

	void abort() {
		bGameAborted = true;
		bGameInProgress = false;
	}

	/*
	 * two versions of reset - used from //jcl shuffle or not. public interface to
	 * resetHand... //misdeal and //newdeal
	 */
	/*
	 * Current Thinking on reset... reset resets the hand to reset the game create a
	 * new one i.e. jcl: //new
	 * 
	 * TODO: Attempting to start a terminated game could work... just do a gameReset
	 */
	public void reset() {
		resetHand();
	}

	public void reset(Boolean shuffle) {
		//bShuffle = shuffle;
		reset();
	}

	// jcl: //start
	public boolean start() {
		// shuffle? Not here. Cards already assigned.
		if (bPlayInitiated) {
			return false;
		} else {
			initiatePlay();
		}
		return true;
	}

	/*
	 * setFirstMove - set the player who leads, and set up trick
	 */
	public void setFirstMove() {
		int i;
		Player p;
		Subdeck sd;
		Card theTwo = deuceClubs;

		// go through players list
		// find who has the two of clubs.
		for (i = 0; i < nPlayers; i++) {
			p = playerArray[i];
			sd = p.subdeck;
			if (sd.find(theTwo))
				nCurrentTurn = i;
		}
		if (nCurrentTurn == -1) {
			// Uh oh. Can't happen. Nobody had the two of clubs...
			System.out.println("Catastrophic error: No one has the two...");
			// TODO:
			// broadcast error... For this?
			abort();
		}

		/*
		 * set up the first trick now that we know who leads
		 */
		nTrickId = 0;
		Trick t = new Trick(nTrickId);
		t.leader = nCurrentTurn;
		trickArray[nTrickId] = t;
		currentTrick = t;
	}

	/*
	 * resetHand - shuffle and deal jcl //misdeal and //newdeal
	 */
	public void resetHand() {
		resetHandScores();
		bGameAborted = false;
		bHandOver = false;
		nCurrentTurn = -1;

		/*
		 * Knock out cards from client and reset robot players note humanPlayer.reset
		 * calls super.reset()...
		 */
		for (int i = 0; i < nPlayers; i++) {
			Player p = playerArray[i];
				p.reset();
			
		}
		deal();
	}

	private void resetHandScores() {
		for (int i = 0; i < nPlayers; i++) {
			Player p = playerArray[i];
			p.handScore = 0;
		}
	}

	/*
	 * create a pack of cards and deal them
	 */
	void deal() {
		int i;
		Player p;

		//
		// Create a new pack of cards and shuffle them
		//
		Subdeck pack = new Subdeck(52);	// 52, t, seatid
		// shuffle manipulated by subdeck...
		//if (bShuffle)
			pack.shuffle();
		//
		// deal official copy of cards
		//
		/*
		 * no need to set current turn here the two might get passed to someone else
		 */
		i = 0;
		for (Card c : pack) { // ; pack.size() > 0; i++) {
			// c = pack.pullTopCard();
			/*
			 * if there aren't a full complement of players cards get thrown away for now...
			 * should abort and throw an exception and a hissy fit.
			 */
			if (playerArray[i] != null) {
				if (playerArray[i].subdeck == null)
					gameErrorLog("can't happen:null subdeck.");
				//
				// TODO: call ssAddCard
				// is ssAddCard still a thing?
				playerArray[i].subdeck.add(c);
			}
			i = (i + 1) % nPlayers;
		}

	}
	/*
	 * send welcome message holding session information to get back into the game.
	 * Then..
	 * 
	 * 1. Initiate a pass message to players ~ when pass is complete, game will
	 * start 2. otherwise find the two commence play
	 * 
	 * Retrieve results Then start the game idea... Passing: waiting for... to
	 * everyone then start... This is the first really asynch thing to be done...
	 */

	void initiatePlay() {
		int i;
		Player p;
		/*
		 * Don't setup trick till after the (maybe) pass
		 *
		 */

		//
		// send (individual) welcome message
		// shouldn't join do this? No because user might be waiting for multiple
		// joins...
		for (i = 0; i < nPlayers; i++) {
			p = playerArray[i];
			ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.PLAYER_WELCOME, p.getName());
			p.sendToClient(pm);
		}
		//
		// send clear-hand =* message before adding the dealt cards?
		/*
		 * Need to modify the robots to understand this ProtocolMessage deleteAll = new
		 * ProtocolMessage(ProtocolMessageTypes.DELETE_CARDS); deleteAll.usertext = "*";
		 * broadcastUpdate(deleteAll);
		 */
		// no. not here... resetGameScores();
		//
		// Now send the protocol message to add cards to the players hand
		for (i = 0; i < nPlayers; i++) {
			p = playerArray[i];
			ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.ADD_CARDS, p.subdeck);
			p.sendToClient(pm);
		}

		// Log the hands for later post-mortem diagnostics
		for (i = 0; i < nPlayers; i++) {
			Subdeck sd = playerArray[i].subdeck;
			gameErrorLog("Housekeeping: subdeck size(" + sd.size() + "){" + sd.encode() + "}");
		}

		// So do the pass, if a pass hand.
		// when the pass is complete it will call send next move
		// otherwise sendnextmove
		//
		// Send the message to the first player to start...
		//
		if (currentPass != MailBoxExchange.PassType.PassHold) {
			initiatePass(currentPass);
			// once pass cards messages are complete, the exchange will call go();
		} else {
			go();
		}
		bPlayInitiated = true;
	}

	/*
	 * go - Pass is complete. Start play
	 */
	void go() {
		setFirstMove();
		sendNextMove();
	}

	private void resetGameScores() {
		currentPass = MailBoxExchange.PassType.PassHold;
		for (int i = 0; i < nPlayers; i++) {
			Player p = playerArray[i];
			p.totalScore = 0;
		}
	}

	/*
	 * disconnect -- delete human player entry that this was attached to, and add
	 * robot player -- called when communications terminate to keep the game going
	 * --
	 */
	@Override
	public void disconnect(int pid) {
		/*
		 * create new robot player, copy the human player's state and send message on to
		 * the new addition...
		 */
		RobotPlayer robot = new RobotPlayer(pid, this);
		Player p = playerArray[pid];
		System.out.println("Disconnect: Seat(" + pid + ")" + p.getName());
		copyPlayer(p, robot);
		playerArray[pid] = robot;
		if (nCurrentTurn == pid) {
			ProtocolMessage pm = new ProtocolMessage(ProtocolMessageTypes.YOUR_TURN);
			robot.process(pm);
		}
	}

}
