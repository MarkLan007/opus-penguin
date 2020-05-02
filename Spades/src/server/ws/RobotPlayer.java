package server.ws;

//package cardGame;

/*  
 *  RobotPlayer -- implement clientSend by processing synchronously...
 */

public class RobotPlayer extends Player implements PlayerInterface {

	// On review...
	// this is RobotPlayer's 2nd copy of the cards.
	// player has official copy as part of game.
	// robot brain has it's own copy.
	// the only thing this is used for if the 2c is asked for
	// on the first play. Could easily delegate this to robotbrain.
	// and then hand could disappear entirely from RobotPlayer...
	//
	// Maybe I need a referee class in game for queries.
	// ref.canIplay()
	// or for godlike play
	// ref.doesAnyoneElseHave(Suit)
	//
	// TODO: hand should be private to robotPlayer; (Afraid to do this right now...)
	Subdeck hand = null; // new Subdeck(); // This is the robot player's copy of the cards; sent in via
							// ADD_CARD etc.

	/*
	 * this is only a testing and debugging function for robotplayers
	 */
	@Override
	public Subdeck getRemoteHand() {
		return hand;
	}

	int getGameTrickCount() {
		if (cardGame == null)
			System.out.println("Fatal Error: no cardgame.");
		return cardGame.getCurrentTrickId();
	}

	@Override
	public void sendToClient(ProtocolMessage pm) {
		/*
		 * Just digest and process the message synchronously
		 */
		if (pm.type == ProtocolMessageTypes.PLAYER_ERROR) {
			int trickid;
			if (cardGame != null)
				trickid = cardGame.getCurrentTrickId();
			else
				trickid = -2;
			badRobot(playerName, trickid, pid, subdeck, pm);
		}
		processLocalMessage(pm);
	}

	private void badRobot(String playerName, int nTrickId, int pid, Subdeck subdeck, ProtocolMessage pm) {
		System.out.println(playerName + " has been a bad robot for illegal play.");
		System.out.println("On " + nTrickId + "th Trick with <" + subdeck.size() + ">=[" + subdeck.encode() + "] has been carded for:");
		System.out.println(pm.encode());
	}

	@Override
	public void sendToServer(ProtocolMessage pm) {
		logClientError("RobotPlayer:" + pm.sender + " Sending<" + pm.type + ">" + pm.subdeck.encode() + " to server.");
		// integration hack
		// MainServer.cgk.enqueue(pm);
		MainServer.enqueue(pm);
	}

	@Override
	void reset() {
		GameInterface gi=cardGame;
		super.reset();
		robotBrain.reset();	// clear the brain too...
		
		// but make sure you don't clobber cardgame
		if (cardGame == null) {
			System.out.println("Uh oh... Little Lost robot");
		}
		if (gi != null && cardGame == null) {
			System.out.println("Self-help employed...");
			setCardgame(gi);	// self help
		}
	}
	
	@Override
	public void logClientError(String s) {
		MainServer.echo(s);
	}

	/*
	 * Note. Can only create a robot player with a pid (Probably true for human
	 * players, too...) Also requires a compatible GameInterface for callbacks to
	 * playCard, passCards and query!
	 */
	GameInterface cardGame = null; // this is here; RobotPlayer and later HumanPlayer have different
									// implementations of these interfaces

	@Override
	public void setCardgame(GameInterface gameInterfaceCallbacks) {
		cardGame = gameInterfaceCallbacks;
	}

	void setPID(int id) {
		pid = id;
	}

	int getPID() {
		return pid;
	}

	int self() {
		return pid;
	}

	static int iSym = 0;

	RobotBrain robotBrain = null; // new RobotBrain();

	RobotPlayer(int pid, GameInterface gameInterfaceCallbacks) {
		// super();
		setAsynch(false); // { Because I am a robot, I can be called synchronously }
		isRobotPlayer = true;
		hand = new Subdeck();
		robotBrain = new RobotBrain();
		robotBrain.setPID(pid);
		setPID(pid);
		setCardgame(gameInterfaceCallbacks);
		setName("robot" + iSym++);
	}

	/*
	 * robotPlay -- play a card from subdeck -- Note: subdeck sd is ignored. Uses
	 * hand and robotbrain... this is new.
	 */
	void robotPlay(Subdeck sd) {
		Card c;
		/*
		 * NoBrainer (literally): Always play the 2C if you have it
		 */
		if (hand.find(Rank.DEUCE, Suit.CLUBS)) {
			c = new Card(Rank.DEUCE, Suit.CLUBS);
			cardGame.playCard(getPID(), c); // yyy
			/*
			 * outmsg = new ProtocolMessage(getPID(), ProtocolMessageTypes.PLAY_CARD, c);
			 * playerErrorLog("RobotPlayer" + getPID() + ": Playing 2C.<" + c.encode() +
			 * ">"); sendToServer(outmsg);
			 */
			return;
		}

		/*
		 * Otherwise use robotBrain to determine what to play...
		 */
		c = robotBrain.playCard(getGameTrickCount());
		if (c == null) {
			// Uh oh... brain failed
			// note cardLead and currentTrick
			System.out.println(getName() + ":Catastrophic Brain failure on lead:"); 
//					+ cardLead.toString() );
			robotBrain.brainDump(true);
			c = robotBrain.playAnything();
		}
		cardGame.playCard(getPID(), c); // yyy
	}

	void robotPlay(ProtocolMessage pm) {
		robotPlay(pm.subdeck);
	}
	
	void addToHand(Card c) {
		hand.add(c);
	}

	boolean deleteFromHand(Card c) {
		if (hand.find(c)) {
			hand.delete(c.rank, c.suit);
			return true;
		}
		return false;
	}

	/*
	 * Implement the PlayerInterface methods here these get called from cardgame as
	 * player.addCards(), etc. xxx
	 */
	// called from the server
	// TODO: these should be sendAddCards, sendDeleteCards, shouldn't they?
	public void addCards(Subdeck sd) {
		String sTemp = "";
		playerErrorLog("RobotPlayer" + getPID() + ": adding " + sd.size() + " cards.");
		// Subdeck sd = m.subdeck.subdeck;
		for (Card cd : sd.subdeck) {
			sTemp = sTemp + cd.encode();
			addToHand(cd);
		}
		playerErrorLog("RobotPlayer" + getPID() + ": Dealt/passed " + sd.sdump() + " cards:" + sTemp);
	}

	public void deleteCard(Card c) {
		if (deleteFromHand(c)) {
			playerErrorLog("RobotPlayer" + getPID() + ": successfully deleted: <" + c.rank + c.suit + ">.");
		} else {
			playerErrorLog("RobotPlayer" + getPID() + ": cannot delete <" + c.rank + c.suit + ">.");
		}

	}

	//
	// TODO: notePlayed et. all: This is a total hack and should be revisited
	Trick currentTrick = null;
	int currentLeader = -1;
	Card cardLead = null;

	//
	// Obsolete: Replaced by RobotBrain functions
	void notePlayed(int nplayer, Card c) {
		if (currentTrick == null)
			newTrick();
		else if (currentTrick.subdeck.size() == 0) {
			cardLead = c;
			currentTrick.leader = nplayer;
		}
		currentTrick.subdeck.add(c);
	}

	// called by trick_update
	void newTrick() {
		cardLead = null;
		currentTrick = new Trick(4);
	}

	public void updateCard(int player, Card c) {
		playerErrorLog("RobotPlayer" + "Unimplementd updateCard");
	}

	public void updateTrick(int lplayer, int wplayer, Subdeck sd) {
		playerErrorLog("RobotPlayer" + "Unimplementd updateTrick");
	}

	public void errorMsg(String st) {
		playerErrorLog("RobotPlayer" + " ErrorMsg ignored");
	}

	public void yourTurn(int nleader, Subdeck sd) {
		playerErrorLog("RobotPlayer" + getPID() + ": My turn.");
		/*
		 * current trick has accumulated the trick
		 */
		robotPlay(sd);
	}

	public void yourPass(int ncards) {
		// playerErrorLog("RobotPlayer" + "Passing Under Construction!");
		// for now, get random cards and pass them...
		Subdeck sd = robotBrain.getPass(3);
		cardGame.passCards(self(), sd);
	}

	/*
	 * processLocalMessage -- make sure you don't modify the sublist.
	 */
	void processLocalMessage(ProtocolMessage m) {
		int i, len;
		String sTemp = "";
		boolean bAbEnd = false;
		Card c = null;
		// TrickModifiers tm=null;

		switch (m.type) {
		// Messages that must be responded to first, here...
		case YOUR_TURN: // !CARD* [cards in trick already played]
			yourTurn(0, m.subdeck);
			break;
		case PASS_CARD: // CARD*
			// instructed to pass card(s)
			// get cards and send to server with responding pass card message
			yourPass(3);
			break;
		case ADD_CARDS: // CARD*
			addCards(m.subdeck);
			robotBrain.addCards(m.subdeck);
			break;
		case DELETE_CARDS: // CARD+
			len = m.subdeck.size();
			/* clobbers the subdeck!!!
			for (i = 0; i < len; i++) {
				c = m.subdeck.pop();
				// playerErrorLog("RobotPlayer" + getPID() + ": + <" + c.rank + c.suit + ">.");
				sTemp = sTemp + "<" + c.rank + c.suit + ">";
				deleteCard(c);
				robotBrain.deleteCard(c);
			} */
			for (Card goner : m.subdeck.subdeck) {
				deleteCard(goner);
				robotBrain.deleteCard(goner);
			} 
break;
		case TRICK_CLEARED:
			robotBrain.trickCleared(m.trick);
			// TODO: parse and use the actual trick
			/*
			 * playerErrorLog("Clearing in RobotPlayer" + getPID() + "***Trick:" +
			 * robotBrain.currentTrickId() + " leader:" + m.sender + // Isn't the message
			 * sender the player of this card? " winner:" + m.trickModifiers.winnerid + //
			 * Winner isn't known yet... needs to be cleared "<" + m.subdeck.encode() +
			 * ">");
			 */
			// newTrick(); // clear the trick
			break;
		case CURRENT_TRICK: // &CARD*
			/*
			 * tm = m.trickModifiers; if (tm.player != m.sender) {
			 * System.out.println("Uh oh: player=" + tm.player + " and sender=" + m.sender);
			 * }
			 */
			c = m.subdeck.subdeck.peek();
			notePlayed(m.sender, c); // TODO: Isn't it a bit redundant to have BOTH ofo these???
			robotBrain.cardPlayed(m.sender, c);
			// notePlayed(tm.player, c); // TODO: Isn't it a bit redundant to have BOTH ofo
			// these???
			// robotBrain.cardPlayed(tm.player, c);
			// Isn't
			// the
			// message
			// sender
			// the
			// player
			// of
			// this
			// card?

			playerErrorLog("RobotPlayer" + getPID() + "***Trick" + robotBrain.currentTrickId() + ": Player" + m.sender + 
					": Card<" + m.subdeck.encode() + "> ***");
			/*
			 * playerErrorLog("RobotPlayer" + getPID() + "***Trick:" + "??" + " leader:" +
			 * m.sender + // Isn't the message sender the player of this card? " winner:" +
			 * m.trickModifiers.winnerid + // Winner isn't known yet... needs to be cleared
			 * "<" + m.subdeck.encode() + ">");
			 */
			// TODO: Isn't this all I need???

			break;
		/*
		 * c=m.subdeck.peekLast(); if (m.trickModifiers == null ) {
		 * playerErrorLog("RobotPlayer" + getPID() + ": ill formed update msg." +
		 * " Card played <" + c.rank + c.suit + ">."); break; } tm = m.trickModifiers;
		 * playerErrorLog("RobotPlayer" + getPID() + ": seeing player" + tm.player +
		 * " played <" + c.rank + c.suit + ">."); if (m.sender != tm.player) System.out.
		 * println("Can't happen: trickmodifer/sender doesn't agree with msg sender");
		 * notePlayed(tm.player, c); break;
		 */
		case PLAYER_SCORES: // $ Player-score, Player-score...
			playerErrorLog("RobotPlayer" + getPID() + " scores:" + m.usertext);
			break;
		case BROKEN_SUIT: // B hearts/spades are broken
		case PLAYER_ERROR: // %Text {Please play 2c, Follow Suit, Hearts/Spades not broken, not-your-turn,
							// don't-have-that-card, user-error}
			// TODO: ok, play another card...
			// brain failed so just get something...
			break;
		case PLAY_CARD: // CARD
			// this is a client-to-server message; should never be seen
			break;
		case SUPER_USER: // S Text
			// sent unhandled message
			if (bAbEnd) {
				playerErrorLog("RobotPlayer: msg:<" + m.type + ":" + m.usertext + "> unimplemented.");
				// integration hack...
				// MainServer.cgk.pause();
				MainServer.pause();

			}
		case GAME_QUERY:
			break;
		default:
			break;
		} // switch
	}

}
