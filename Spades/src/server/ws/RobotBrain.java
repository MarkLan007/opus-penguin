package server.ws;

import java.util.Arrays;
//package cardGame;

//import RobotBrain.Hand;

abstract public class RobotBrain {
	int trickCount = 0;
	int pid;
	Hand hand = new Hand();
	BatteryBrain batteryBrain = new BatteryBrain();
	boolean bSuitBroken = false;
	GameInterface cardGame; // for isHigher
	int tricksTaken=-1;
	
	final int nPlayers = 4;
	int mySeat = -1;

	void setPID(int n) {
		pid = n;
	}


	RobotBrain(int pid, GameInterface gameInterfaceCallbacks) {
		mySeat = pid; // can this change? If it does I'm screwed
		cardGame = gameInterfaceCallbacks;
	}

	int getBid() {
		int bid=cardGame.getBid(mySeat);
		return bid;
	}
	int getBid(int seat) {
		int bid=cardGame.getBid(seat);
		return bid;
	}
	int haveTricks() {
		return tricksTaken;
	}
	boolean bMoonPolice = false;
	boolean bMoonShooting = false;

	/*
	 * reset -- called per hand...
	 */
	void reset() {
		// seat should not change...
		trickCount = 0;
		currentTrick = null;
		// otherwise robot screwed up on first trick following
		hand.clear();
		batteryBrain.reset();
		bSuitBroken = false;
		// hearts specific...
		bMoonPolice = false;
		bMoonShooting = false;
		// spades specific
		tricksTaken = 0;
		// hand = new Hand();
	}

	boolean bThinkingOutLoud = true; // i.e. chattyrobot

	void thinkOutLoud(boolean b) {
		bThinkingOutLoud = b;
	}

	void brainDump() {
		bThinkingOutLoud = true;
	}

	void brainDump(String msg) {
		System.out.println("s" + mySeat + ">braindump:" + msg 
				+ " suitBroken=" + bSuitBroken
				+ " shootmoon=" + bMoonShooting 
				+ " moonpolice="+ bMoonPolice);
		brainDump(true);
	}
	
	void brainDump(Card c, String msg) {
		String cardstring;
		if (c == null)
			cardstring = "nil";
		else
			cardstring = c.encode();
		brainDump(msg + "<" + cardstring + ">");
	}
	
	void brainDump(boolean bNow) {
		if (!bNow)
			brainDump(); // just make this robot chatty...

		System.out.println("BrainContents:" + this.currentTrickId() + " c:{" + hand.getclubs().size() + "} d:{"
				+ hand.getdiamonds().size() + "} h:{" + hand.gethearts().size() + "} s:{" + hand.getspades().size()
				+ "}");
		System.out.println("Trick:" + this.currentTrickId() + " c:{" + hand.getclubs().encode() + "} d:{"
				+ hand.getdiamonds().encode() + "} h:{" + hand.gethearts().encode() + "} s:{"
				+ hand.getspades().encode() + "}");
	}

	/*
	 * getSomething no matter what - last chance return a card no matter what
	 */
	Card getSomething() {
		Card c = hand.randomCard();
		/*
		 * Card c = null; c = new Card(Rank.ACE, Suit.SPADES);
		 */
		if (c == null)
			c = hand.peek();
		if (c == null)
			c = new Card(Rank.ACE, Suit.SPADES);
		brainDump(c, "Uh oh. last chance to return a card");
		return c;
	}

	abstract Card determineBestCard(int actualTrickId);
	/*
	Card determineBestCard(int actualTrickId) {
		Card c = null;
		trickCount = actualTrickId;

		// sanity checks...
		if (hand.size() == 1)
			return hand.peek();
		if (trickCount == 0) {
			// Make sure I have 13 cards in my hand
			// or else declare a misdeal (or at least whine)
			if (hand.size() != 13)
				brainDump("*** Robot(?) only " + hand.size() + " cards ***");
				//System.out.println("*** Robot(?) cards=" + hand.size() + "***");
		}
		if (hand.find(Card.deuceOfClubs)) {
			brainDump("Robot: I have the 2...");
			// System.out.println("Robot: I have the 2...");
			return Card.deuceOfClubs;
		} else if (trickCount == 0 && cardLead == null) {
			// can't happen: first trick without any clubs...
			// System.out.println("Can't happen: Lead on first trick without the 2...");
			brainDump("Can't happen: Lead on first trick without the 2...");
			// but the show must go on...
		}

		// if have been moon shooting, make sure it's still possible.
		// otherwise change course
		if (bMoonShooting && !moonPossibleForMe())
			bMoonShooting = false;
		if (!bMoonShooting && moonPossibleForSomeone())
			bMoonPolice = true;
		if (bThinkingOutLoud) {
			//System.out.println("Robot(seat" + mySeat + "): I have the lead.");
			//System.out.println("...presumptiveSpade(" + presumptiveSpade + ")");
			brainDump("dbc:strategy checkpoint. ");		
			}

		// favor drawing out spades unless...
		Card presumptiveSpade = hand.highestCardUnder(Card.queenOfSpades);
		if (cardLead == null) { // I have the lead
			if (bThinkingOutLoud) {
				//System.out.println("Robot(seat" + mySeat + "): I have the lead.");
				//System.out.println("...presumptiveSpade(" + presumptiveSpade + ")");
				brainDump(presumptiveSpade, "Leading. presumptiveSpade:");
			}
			if (trickCount == 0) {
				// can't happen.
				// I have the lead on the first trick without the 2c
				//System.out.println("Can't happen: void in clubs and I have the lead on the first trick.");
				brainDump("Can't happen: void in clubs and I have the lead on the first trick.");
				c = getSomething();
				return c;
			}
			// Draw out the queen of spades, unless I have it
			// Play highest spade below queen if I have one
			// ... except if I have the queen
			if (batteryBrain.isPlayed(Card.queenOfSpades))
				presumptiveSpade = null;
			if (!hand.find(Card.queenOfSpades) && presumptiveSpade != null) {
				c = presumptiveSpade;
				// c = hand.getspades().peek(); // get lowest...
				// no... get a spade below the queen...
				// so if QS is still out, be carefull..
				if (bThinkingOutLoud) {
					brainDump(c, "draw out queen:");
					//System.out.println("Robot: Attempt to draw the queen out:" + c.encode());
				}
			}
			if (c == null) {
				if (bThinkingOutLoud)
					brainDump("Spades not a good choice. Look for a good lead.");
				c = hand.bestLead(presumptiveSpade); // get the best card to lead
			}
			if (c == null) {
				brainDump("Bestlead returned nothing; desparation.");
				c = getSomething();
			}
			// Any time you lead the QS, brain dump...
			if (c.equals(Card.queenOfSpades) || c.equals(Card.kingOfSpades))
				brainDump(c, "Yikes! *** About to Lead ***");
			if (bThinkingOutLoud)
				brainDump(c, "About to lead:");
			//System.out.println("Robot: About to play..." + c.encode());
			return c;
		}
		//
		// apply must-follow rule
		if (bThinkingOutLoud) {
			//System.out.println("Robot: must follow " + cardLead.suit + " if possible...");
			brainDump(cardLead, "Robot: following lead:");
		}
		if (trickCount == 0) { // first trick; highest club or slough
			if (hand.getclubs().size() > 0)
				if (!bMoonShooting)
					return hand.highest(Suit.CLUBS);
				else
					return hand.highest(Suit.CLUBS);
			// fall through to slough something
		}
		if (cardLead.suit == Suit.SPADES) {
			// has the king or ace been played? drop the q. Boom.
			// got lucky.
			if (cardInTrick(Card.aceOfSpades) || cardInTrick(Card.kingOfSpades)) {
				if (hand.find(Card.queenOfSpades))
					return Card.queenOfSpades;
			} else if (presumptiveSpade != null)
				return presumptiveSpade;
		}
		if (!hand.voidIn(cardLead.suit)) {
			// actually check if the qs has been played
			// if (alreadyPlayed(queenOfSpades))
			if (cardLead.suit == Suit.SPADES)
				c = hand.duckUnderSpade(currentTrick);
			else
				c = hand.duckUnder(currentTrick);
			if (bThinkingOutLoud)
				brainDump(c, "Ducking under");
				//System.out.println("s" + mySeat + " ducking under w/:" + c.encode());
			// if so, duck the lead (unless moonshooting...)
			// c = hand.highest(cardLead.suit);
			// c = hand.highestNon(cardLead.suit, queenOfSpades);
		} else { // Slough!
			if (bThinkingOutLoud) {
				brainDump("Can slough!");
				//System.out.println("Robot: can slough!");
			}
			// void in the lead suit. Slough something
			c = hand.bestSlough(trickCount);
			if (bThinkingOutLoud)
				brainDump(c, "Sloughing:"); 
				//((c==null) ? "nil" : c.encode()));
		}
		if (c == null) {
			c = hand.randomCard();
			brainDump(c, "s" + mySeat + ": Dithering. No result. Random:");
			//System.out.println("s" + mySeat + "Dithering. No result. Random:" + c.encode());
		}
		return c;
	}
*/
	
	/*
	 * RobotBrain::getPass
	 */
	Suit preferredPassSuits[] = {
			// hearts handled separately: Suit.HEARTS,
			// always hoard and don't pass spades except as below
			Suit.DIAMONDS, Suit.CLUBS };

	// final int nCardsToPass=3;
	static boolean bDebugPass = true;

	Subdeck getPass(int nCardsToPass) {
		if (nCardsToPass != 3) {
			brainDump("Likely fatal. Unexpected pass number: " 
					+ nCardsToPass + "Ignored.");
			nCardsToPass = 3;
		}
		// sort the damn hand so you can debug it...
		for (Suit st : Suit.suits)
			hand.getSuit(st).sort();

		Subdeck pass = new Subdeck();
		// ideas
		// 1. pass Q if not enough backers
		// 2. go void in a suit, preferable hearts
		// 3. get down to 1 club
		// 4. Just be mean... high cards, 2c and a medium heart

		if (RobotBrain.bDebugPass) {
			//System.out.println("Select pass from:");
			brainDump("Select pass from:");
		}
		// idea 1. Emergency 1 jetison QS
		Subdeck spades = hand.getspades();
		boolean hasAS = (hand.find(Card.aceOfSpades));
		boolean hasKS = (hand.find(Card.kingOfSpades));
		boolean hasQS = (hand.find(Card.queenOfSpades));
		int topspades = hand.hasTop(Suit.SPADES);
		int totalSpades = spades.size();
		Subdeck hearts = hand.gethearts();
		int totalhearts = hearts.size();
		int tophearts = hand.hasTop(Suit.HEARTS);
		if (hasQS) { // Idea 1.
			// keep or pass?
			if (totalSpades > 3) // needed number of backers
				; // keep the queen
			else if (tophearts > 3)
				bMoonShooting = true;
			else if (topspades > 3)
				bMoonShooting = true;
			else {
				// Sorry. Got to fob off the queen...
				pass.add(Card.queenOfSpades);
				if (hasKS)
					pass.addWithMax(Card.kingOfSpades, nCardsToPass);
				if (hasAS)
					pass.addWithMax(Card.aceOfSpades, nCardsToPass);
			}
		} else if ((hasAS && hasKS && totalSpades < 4) || ((hasAS || hasKS) && totalSpades < 3)) {
			if (tophearts >= 3)
				bMoonShooting = true;
			else {
				if (hasAS)
					pass.addWithMax(Card.aceOfSpades, nCardsToPass);
				if (hasKS)
					pass.addWithMax(Card.kingOfSpades, nCardsToPass);
			}
		}
		int leftToPass = nCardsToPass - pass.size();
		if (leftToPass == 0)
			return pass;

		// Idea 3.
		// if I've got the highest hearts
		// get rid of as many low ones as I can
		if (tophearts > 0 || bMoonShooting) {
			bMoonShooting = true;
			; // I'm thinking moon or moon police
				// Can I get rid of non-top?
			int middlinghearts = totalhearts - tophearts;
			if (middlinghearts > 3)
				; // just hoard them
			else {
				for (int j = 0; j < middlinghearts; j++)
					if (!pass.addWithMax(hearts.elementAt(-(j + 1)), nCardsToPass))
						break;
			}
		} else if (hearts.size() <= leftToPass) { // i.e. void in hearts or can make myself void
			bMoonShooting = true;
			for (int j = 0; j < hearts.size(); j++)
				if (!pass.addWithMax(hearts.elementAt(-(j + 1)), nCardsToPass))
					break;
		} // otherwise accumulate hearts; don't try to pass them.

		leftToPass = nCardsToPass - pass.size();
		if (leftToPass == 0)
			return pass;

		// Idea 2. be void in things
		for (int i = 0; i < preferredPassSuits.length; i++) {
			// check hearts first...
			// don't try to be void in spades, except for above
			Suit suit = preferredPassSuits[i];
			Subdeck sd = hand.getSuit(suit);
			sd.sort();
			if (leftToPass >= sd.size()) { // yea!
				for (int j = 0; j < sd.size(); j++) {
					Card c = sd.elementAt(bMoonShooting ? -(j + 1) : j);
					pass.addWithMax(c, nCardsToPass);
				}
			}
		}
		leftToPass = nCardsToPass - pass.size();
		if (leftToPass == 0)
			return pass;

		// Still have cards I can pass?
		// preferentially pass the 2c
		if (hand.find(Card.deuceOfClubs) && !pass.find(Card.deuceOfClubs)) {
			pass.addWithMax(Card.deuceOfClubs, nCardsToPass);
		}
		leftToPass = nCardsToPass - pass.size();
		if (leftToPass == 0)
			return pass;

		// idea 4
		// if moon shooting get a low (already tried that)
		// make sure there is a heart in the pass
		// ptth.
		// fine... Just pick something
		int randomcards = 0;
		for (int tries = 0; pass.size() < nCardsToPass && tries < 50;) {
			Card c = hand.randomCard();
			if (c == null)
				break;
			if (pass.find(c)) // already passing it
				tries++;
			else {
				pass.addWithMax(c, nCardsToPass);
				randomcards++;
				System.out.println("Robot(" + pid + ")Randomly passing:" + c.encode());
			}
		}
		if (randomcards > 0)
			brainDump("Pass had " + randomcards + " random cards.");
		if (bDebugPass)
			System.out.println("Passing:" + pass.encode());
		
		int extraCards = 0;
		for (extraCards = 0; pass.size() > nCardsToPass; extraCards++)
			pass.pop();
		if (extraCards > 0)
			brainDump("Pass had " + extraCards + " to many cards.");
		if (pass.size() != 3)
			brainDump("*** Cannot find 3 cards to pass... ***");
		return pass;
	}

	/*
	 * RobotBrain::addCards and deleteCard; know what's in the hand
	 */
	void addCards(Subdeck sd) {
		hand.populate(sd);
		hand.sort();
	}

	void deleteCardXXX(Card c) {
		if (!hand.find(c)) {
			System.out.println("Holy Shit: *** Trying to delete a card I don't think I have! ***");
		}
		hand.deleteCard(c);
	}

	/*
	 * Essence of RobotBrain -- remember what has been played...
	 * RobotBrain::addCards
	 */
	//
	// TODO: notePlayed replaced by cardPlayed
	// Under construction for state memory
	Trick currentTrick = null;
	int currentLeader = -1;
	Card cardLead = null;
	// int iCurrentTrick=0;

	int currentTrickId() {
		return trickCount;
	}

	class BatteryBrain {
		// Boolean[] memory = new Boolean[52]; // sorry... initialized to false
		private Boolean[][] mem = new Boolean[Suit.suits.length][Rank.ranks.length];
		int pointTotals[] = new int[4];
		int count = 0; // count of cards played

		BatteryBrain() {
			reset();
			// todo: if being pulled into life in the middle of the game
			// find out what's been played already somehow...
		}

		/*
		 * reset -- called once per hand
		 */
		void reset() {
			// Arrays.fill(mem, false);
			for (Boolean[] row : mem)
				Arrays.fill(row, false);
			Arrays.fill(pointTotals, 0);
			count = 0;
			bMoonShooting = false;
		}

		/*
		 * played - note that card is played called when trick clears not when the
		 * current trick is being contested
		 */
		void played(Card c) {
			mem[c.suit.ordinal()][c.rank.ordinal()] = true;
			count++;
		}

		/*
		 * isPlayed -- if I don't really know return false -- this is really I remember
		 * that the card has been played
		 */
		boolean isPlayed(Card c) {
			return mem[c.suit.ordinal()][c.rank.ordinal()];
		}

		int countSuitPlayed(Suit st) {
			int count = 0;
			int suit = st.ordinal();
			for (int j = Rank.first().ordinal(); j <= Rank.last().ordinal(); j++)
				if (mem[suit][j])
					count++;
			return count;
		}

		Card highestNonPlayed(Suit st) {
			Card ctemp = new Card(Rank.ACE, st);
			for (Rank r : rankOrder) {
				ctemp.rank = r;
				if (!isPlayed(ctemp))
					return ctemp;
			}
			return null;
		}

		/*
		 * higherCardOutThere - walk through memory looking to see if a higher card is
		 * out there; make sure it's not a card I have in my hand
		 */
		boolean higherCardOutThere(Card card) {
			int suit = card.suit.ordinal();
			Card ctemp = new Card(Rank.ACE, card.suit);
			for (Rank r : rankOrder) {
				ctemp.rank = r;
				if (cardGame.isHigher(card, ctemp))
					return false; // i.e. card is higher than current candidate, so no...
				if (!mem[suit][r.ordinal()]) {
					// found something not played
					if (!hand.find(ctemp))
						return true; // I don't have it
				}
			}
			return false;
		}

		// return the number higher cards out there found
		int higherCardsOutThere(Card card) {
			int found = 0;
			int suit = card.suit.ordinal();
			Card ctemp = new Card(Rank.ACE, card.suit);
			for (Rank r : rankOrder) {
				ctemp.rank = r;
				if (cardGame.isHigher(card, ctemp))
					return found; // i.e. card is higher than current candidate, so no...
				if (!mem[suit][r.ordinal()]) {
					// found something not played
					if (!hand.find(ctemp))
						found++; // I don't have it
				}
			}
			return found;
		}

	} // batterybrain

	/*
	 * moon is possible for who if moon is possible for one player return seat
	 * otherwise -1
	 */
	int moonPossibleForWho() {
		int playersWithPoints = 0;
		int seatid = -1;
		for (int i = 0; i < nPlayers; i++)
			if (batteryBrain.pointTotals[i] > 0) {
				playersWithPoints++;
				seatid = i;
			}
		if (playersWithPoints <= 1)
			return seatid;
		return -1;
	}

	boolean moonPossibleForSomeone() {
		int playersWithPoints = 0;
		for (int i = 0; i < nPlayers; i++)
			if (batteryBrain.pointTotals[i] > 0)
				playersWithPoints++;
		return playersWithPoints <= 1;
	}

	boolean moonPossibleForMe() {
		// int myPoints=0;
		int othersWithPoints = 0;
		for (int i = 0; i < nPlayers; i++)
			if (i == mySeat)
				; // myPoints = batteryBrain.pointTotals[i];
			else if (batteryBrain.pointTotals[i] > 0)
				othersWithPoints++;
		return othersWithPoints == 0;
	}
	

	void cardPlayed(int seat, Card c) {
		if (currentTrick == null)
			newTrick();
		if (currentTrick.subdeck.size() == 0) {
			cardLead = c;
			currentTrick.leader = seat;
		}
		currentTrick.subdeck.add(c);
	}

	void newTrick() {
		cardLead = null;
		Trick t = new Trick(trickCount);
		if (currentTrick != null && currentTrick.suitBroken()) {
			t.breakSuit();
			bSuitBroken = true;
		}
		currentTrick = t;
		trickCount++;
	}

	// true if card is in trick, false otherwise
	boolean cardInTrick(Card card) {
		boolean found = false;
		if (currentTrick == null)
			;
		for (Card c : currentTrick.subdeck)
			if (c.equals(card)) {
				found = true;
				break;
			}
		return found;
	}

	/*
	 * ToDO: do something with who won the trick... Me for example. compute moon
	 * shooting, etc.
	 */
	void trickCleared(Trick trick) {
		// Show the trick... aaa
		String s = trick.encode();
		//System.out.println("Robot" + pid + " clearing:" + s);
		if (bThinkingOutLoud)
			brainDump("Clearing trick:" + s);
		//
		// Keep real time score to for moon shooting / moon policing
		int winner = trick.winner;
		for (Card c : trick.subdeck) {
			batteryBrain.played(c);
			// found you, you little bastard...
			//if (c.suit == Suit.HEARTS) {
			if (c.suit == cardGame.getSuitThatMustBeBroken()) {
				batteryBrain.pointTotals[winner]++;
				bSuitBroken = true;
			} else if (c.equals(Card.queenOfSpades))
				batteryBrain.pointTotals[winner] += 13;
		}
		newTrick();
	}


	// ++ methods that use hand and battery brain
	// part of hand...

	// TODO: consolidate all these comparison functions into
	// cardgame.
	//
	// ych. this shouldn't go here... Should be in Cardgame...
	// use rankOrder...
	/*
	 * Specific cardgame Should have
	 *  static final Rank rankOrder[]={...};
	 *  // isHigher knows about trumps
	 *  static boolean isHigher(c1, c2);
	 *  and for Spades...
	 *  isHigher(bC1playedFirst, c1, c2);
	 *  static boolean higherRank(r1, r2);
	 */
	final Rank rankOrder[] = { Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK, Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN,
			Rank.SIX, Rank.FIVE, Rank.FOUR, Rank.THREE, Rank.DEUCE };

	// true if r1 > r2; false otherwise;
	boolean cardGameHigherRank(Rank r1, Rank r2) {
		if (r1 == r2)
			return false;
		// one is higher... if r1, true, otherwise false
		for (Rank r : rankOrder)
			if (r == r1)
				return true;
			else if (r == r2)
				return false;
		return false;
	}

	/*
	 * return the highest card in a suit not played null if all cards in suit played
	 */
	Card highestCardOutThere(Suit st) {
		int suit = st.ordinal();
		for (Rank r : rankOrder) {
			Card c = new Card(r, st);
			if (!batteryBrain.mem[suit][r.ordinal()] && !hand.find(c))
				return c;
		}
		return null;
	}
// -- methods that use hand and battery brain

	/*
	 * canLoseTrick if there is a card that I could play that could be taken by
	 * someone else
	 * 
	 * return the lead loser that I'm longest in... i.e. fewest number of cards I
	 * have to lose to shoot moon...
	 * 
	 * return null if no candidates
	 */
	Card leadLoser() {
		for (Suit st : Suit.suits) {
			Subdeck cards = hand.getSuit(st);
			// NOTE: highercardoutthere doesn't know
			// about the game order!!!
			for (Card c : cards) {
				if (batteryBrain.higherCardOutThere(c))
					return c;
			}
		}
		return null;
	}
	
	/*
	 * card most likely to lose lead, or null if nothing
	 */
	Card leadLoserNon(Suit skip) {
		for (Suit st : Suit.suits) {
			if (st == skip)
				continue;
			Subdeck cards = hand.getSuit(st);
			// NOTE: highercardoutthere doesn't know
			// about the game order!!!
			for (Card c : cards) {
				if (batteryBrain.higherCardOutThere(c))
					return c;
			}
		}
		return null;
	}

	/*
	 * duckUnder(Trick trick) - do everthing possible to duck the trick if can't be
	 * ducked either be lowest or if inevitable take with highest return null only
	 * if no cards of the suit in hand
	 * 
	 * duckunder(trick) - avoid taking this trick if possible if not possible take
	 * with the highest card in the suit. special case for spades/queen of spades
	 * handled separately
	 * 
	 * I think it properly belonds here, in robotBrain, but
	 * my belong in heartsRobotBrain as being hearts-specific.
	 */
	Card duckUnder(Trick ct) {
		Card cardToDuck = highestCardInTrick(ct);
		Card candidate = duckUnder(cardToDuck);
		if (candidate == null) { // couldn't find one
			candidate = hand.lowest(cardToDuck.suit);
			// otherwise take with highest (unless moonshooting)
			if (ct.subdeck.size() == 3) // if not, am I last?
				candidate = hand.highest(cardToDuck.suit);
		}
		return candidate;
	}

	/*
	 * duckUnder - if possible, return a card under the one given null if there
	 * aren't any.
	 */
	Card duckUnder(Card cardToDuck) {
		// Card cardToDuck=highestCardInTrick(ct);
		Suit st = cardToDuck.suit;
		Subdeck myCards = hand.getSuit(st);
		Card candidate = null;

		// can I get under?
		// find the highest card below the lead card
		for (Card c : myCards)
			// get highest card below lead
			if (cardGame.isHigher(c, cardToDuck))// i.e. 2c,Ac -> F
				if (candidate == null)
					candidate = c;
				else
					candidate = candidate.higherCard(c);
		return candidate;
	}

	/*
	 * currentLeader - card that is the current leader of a trick
	 * Note. this is called from spades as super.highestCardInTrick...
	 */
	Card highestCardInTrick(Trick trick) {
		int i = 0;
		Suit st = null;
		// final int nPlayers = 4;
		Card leadingCard = null;
		/*
		 * enumerate the trick, and get highest TODO: integrate with code in CardGame
		 * this should only get done once...
		 */
		for (Card c : trick.subdeck) {
			// the actual player who won the trick is n players away from the leader or the
			// leader
			if (i == 0) {
				leadingCard = c;
				st = c.suit;
			} else if (c.suit == st && cardGame.isHigher(c, leadingCard)) { // true if c is higher than leadingcard
				// if (bDebugCardCf)
				// gameErrorLog("->T");
				leadingCard = c;
				// currentTrick.winner = (i + currentTrick.leader) % nPlayers;
			} else {
				// if (bDebugCardCf)
				// gameErrorLog("->F");
				// card was sloughed
			}
			i++;
		}
		return leadingCard;
	}
	
	// TODO: shouldn't hand by independant of RobotBrain
	// usable by any robot
	class Hand {
		// Subdeck clubs = new Subdeck(), diamonds = new Subdeck(), hearts = new
		// Subdeck(), spades = new Subdeck();
		Subdeck[] suits = new Subdeck[4];

		Subdeck getSuit(Suit suit) {
			return suits[suit.ordinal()];
		}

		void sort() {
			for (Subdeck suit : suits)
				if (suit.size() > 0)
					suit.sort();			
		}
		
		/*
		 * peek - return the first card you find...
		 *  was getFirstCard
		 */
		Card peek() {
			for (Subdeck suit : suits)
				if (suit.size() > 0)
					return suit.peek();
			return null;
		}

		int size() {
			int ncards = 0;
			// Suit st;
			int i = 0;
			for (Suit st = Suit.first(); i < Suit.size(); st = st.next(), i++) {
				ncards += suits[st.ordinal()].size();
			}
			return ncards;
		}

		void clear() {
			int i = 0;
			for (Suit s = Suit.first(); i < Suit.size(); i++) {
				int j = s.ordinal();
				if (suits[j] == null)
					suits[j] = new Subdeck();
				else
					suits[j].clear();
				s = s.next();
			}
		}

		Hand() {
			clear();
		}

		Subdeck getclubs() {
			return suits[Suit.CLUBS.ordinal()];
		}

		Subdeck getdiamonds() {
			return suits[Suit.DIAMONDS.ordinal()];
		}

		Subdeck gethearts() {
			return suits[Suit.HEARTS.ordinal()];
		}

		Subdeck getspades() {
			return suits[Suit.SPADES.ordinal()];
		}

		public int count(Suit suit) {
			Subdeck sd = suits[suit.ordinal()];
			return sd.count(suit); // or size...
		}

		public int countNon(Suit suit) {
			int n = 0;
			for (Suit st : Suit.suits)
				if (st != suit)
					n += suits[st.ordinal()].size();
			return n;
		}

		boolean voidIn(Suit st) {
			return suits[st.ordinal()].size() == 0;
		}

		int hasTop(Suit suit) {
			int top = 0;
			for (Rank rank : rankOrder)
				if (find(new Card(rank, suit)))
					top++;
				else
					break;
			return top;
		}

		/*
		 * lowestNonPoint - lowest card not worth points
		 * Review: Hearts specific??? Not properly part of Hand???
		 */
		Card lowestNonPoint() {
			Card candidate = null;
			for (Suit suit : Suit.suits) {
				if (suit == Suit.HEARTS)
					continue;
				Subdeck sd = getSuit(suit);
				//
				// the lowest card in a suit I can't run
				// Can I run this suit?
				// if not, return the lowest card
				if (sd.size() == 0)
					continue;
				if (suit == Suit.SPADES) {
					// Damn. One spade and it's the queen
					if (sd.find(Card.queenOfSpades) && sd.size() == 1) {
						continue;
					}
					sd.sort();
					Card c1 = sd.elementAt(-1); // lowest
					Card c2 = sd.elementAt(-2); // second
					return (c1.rank != Rank.QUEEN) ? c1 : c2;
				}
				Card c = lowest(suit);
				if (!cardGameHigherRank(c.rank, candidate.rank)) {
					candidate = c;
				}
			}
			return candidate;
		}

		/*
		 * return a generally toxic card from the hand called by slough
		 * Review: Hearts specific??? Not properly part of Hand???
		 */
		Card toxicCard(boolean moonpolice) {
			// moonpolice or not...
			if (find(Card.queenOfSpades))
				return Card.queenOfSpades;
			Card candidate = peek();
			if (size() == 1)
				return candidate; // Oh well

			Subdeck hearts = getSuit(Suit.HEARTS);
			if (hearts.size() >= 1) {
				hearts.sort();
				candidate = highest(Suit.HEARTS);
				// if moonpolice don't return last heart if
				// I can help it
				if (!moonpolice || hearts.size() < 2)
					return candidate;
				return hearts.elementAt(2);
			} else if (!moonpolice && hearts.size() > 0)
				return highest(Suit.HEARTS);
			// TODO: see if I'm being run out of hearts
			// i.e. highest outstanding, can I keep from
			// getting run out of them...
			//
			if (getspades().size() > 0) {
				if (hand.find(Card.kingOfSpades))
					return Card.kingOfSpades;
				if (hand.find(Card.aceOfSpades))
					return Card.aceOfSpades;
			}
			for (Suit suit : Suit.suits) {
				int cardsLeft = getSuit(suit).size();
				if (moonpolice) {
					if (cardsLeft > 1)
						return getSuit(suit).elementAt(2);
				}
			}
			if (candidate == null)
				System.out.println("RobotPlayer: ToxicCard: nothing mean to play...");
			else
				System.out.println("RobotPlayer: Can't do any better..." + candidate.encode());
			return candidate;
		}

		/*
		 * versions of highestCard in the Shortest Suit (ignorning st)
		 */
		// Card highestCardShortestSuit() { }
		// Card highestCardShortestSuitNon(Suit st) { }

		Card highestCardShortestSuitNon(Suit notSuit1, Suit notSuit2) {
			int ncards = 0;
			Card candidate = null;
			for (Suit st : Suit.suits) {
				int n = getSuit(st).size();
				if (st == notSuit1 || st == notSuit2)
					;
				else if (voidIn(st))
					;
				else if (ncards == 0 && !voidIn(st)) {
					ncards = n;
					candidate = highest(st);
				} else if (n < ncards) {
					ncards = n;
					candidate = highest(st);
				}
			}
			return candidate;
		}

		/*
		 * return the highest card that I could still play over if I had to...
		 */
		/* -- not used not tested --
		Card highestCardNotTop(Suit notSuit1, Suit notSuit2) {
			int ncards = 0;
			Card candidate = null;
			for (Suit st : Suit.suits) {
				if (st == notSuit1 || st == notSuit2)
					continue;
				int n = getSuit(st).size();
				// do I have the highest outstanding card in suit?
				//
				Card c = batteryBrain.highestNonPlayed(st);
				if (ncards == 0 && !voidIn(st)) {
					ncards = n;
					candidate = highest(st);
				} else if (n < ncards) {
					ncards = n;
					candidate = highest(st);
				}
			}
			return candidate;
		} */

		/*
		 * return lowest card that isn't the top card in hand!
		 * 
		 * lowest card in suit with fewest top cards... not quite right... Suppose there
		 * is a hole? ... should return lowest card if there is a hole Key point: not
		 * dynamic; has to do with hand now, not on the board ***
		 */
		Card lowestCardNotTop(Suit notSuit1, Suit notSuit2) {
			int ncards = 0;
			Card candidate = null;
			//
			// TODO: look for the biggest hole...
			for (Suit st : Suit.suits) {
				if (st == notSuit1 || st == notSuit2)
					continue;
				if (voidIn(st))
					continue;
				int n = hasTop(st);
				if (ncards == 0) { // no top cards in suit
					candidate = lowest(st);
					ncards = n;
				} else if (n < ncards) {
					ncards = n;
					candidate = highest(st);
				}
			}
			return candidate;
		}

		Card lowestCardInHand(Suit notSuit1, Suit notSuit2) {
			Card candidate = null;
			//
			// TODO: look for the biggest hole...
			for (Suit st : Suit.suits) {
				if (st == notSuit1 || st == notSuit2)
					continue;
				if (voidIn(st))
					continue;
				Card c = lowest(st);
				if (candidate == null)
					candidate = c;
				if (!cardGame.isHigher(candidate, c))
					candidate = c;
			}
			return candidate;
		}




		/*
		 * highestCardUnder - return highest card in same suite from hand below that is
		 * below card, according to game's rank order.
		 * 
		 * return null if no such card isHigher(c1,c2) t if c1 > c2 code is suspect...
		 * Not fully tested..
		 */
		Card highestCardUnder(Card card) {
			Subdeck sd = getSuit(card.suit);
			Card bestSoFar = null;
			for (Card c : sd)
				if (c.equals(card) || cardGame.isHigher(c, card)) // i.e. 2c,Ac -> F So AS,QS continue...
					;
				else if (bestSoFar == null)
					bestSoFar = c;
				else if (cardGame.isHigher(c, bestSoFar))
					bestSoFar = c;
			return bestSoFar;
		}

		/*
		 * highest -- highest card in suit
		 */
		Card highest(Suit st) {
			Subdeck sd = suits[st.ordinal()];
			Card highestSoFar = null;
			for (Card c : sd)
				if (highestSoFar == null)
					highestSoFar = c;
				else
					highestSoFar = highestSoFar.higherCard(c);

			return highestSoFar;
		}

		/*
		 * lowest -- lowest card in suit ... new
		 */
		Card lowest(Suit st) {
			Subdeck sd = suits[st.ordinal()];
			Card lowestSoFar = null;
			for (Card c : sd)
				if (lowestSoFar == null)
					lowestSoFar = c;
				else
					lowestSoFar = lowestSoFar.lowerCard(c);

			return lowestSoFar;
		}

		// return nth (starting at 0) card in suit
		// sort the damn suit and count up...
		Card lowest(Suit st, int nth) {
			Subdeck sd = suits[st.ordinal()];
			sd.sort();
			Card lowestSoFar = null;
			for (Card c : sd)
				if (lowestSoFar == null)
					lowestSoFar = c;
				else
					lowestSoFar = lowestSoFar.lowerCard(c);

			return lowestSoFar;
		}

		/*
		 * return the highest non card in the suit, if possible... return a higher card
		 * than cToAvoid rather than return null.
		 */
		Card highestNon(Card cToAvoid) {
			Subdeck sd = suits[cToAvoid.suit.ordinal()];
			Card highestSoFar = null;

			// Note: the only time you return cToAvoid is
			// if it's the only card in the suit
			if (sd.size() == 1 && sd.find(cToAvoid))
				return cToAvoid;
			for (Card c : sd)
				if (highestSoFar == null) {
					if (c.rank != cToAvoid.rank)
						highestSoFar = c;
				} else if (c.rank != cToAvoid.rank)
					highestSoFar = highestSoFar.higherCard(c);

			if (highestSoFar == null && sd.find(cToAvoid))
				return cToAvoid; // can't be helped...
			return highestSoFar;
		}

		void populate(Subdeck sd) {
			for (Card c : sd.subdeck) {
				suits[c.suit.ordinal()].add(c);
			}
			String type;
			boolean misdeal=false;
			if (sd.size() == 13)
				type = "dealt";
			else if (sd.size() == 3)
				type = "passed";
			else {
				type = "huh?";
				misdeal = true;
			}
			System.out.println("Brain:" + type + "[" + sd.encode() + "]");
			if (misdeal) {
				brainDump("fatal: I've been passed the wrong number of cards");
				//cardGame.declareMisdeal(pid, playerName + ":" + pm.usertext);
			}
		} // populate


		/*
		 * find - find a card in the hand
		 */
		boolean find(Card c) {
			Subdeck suit = suits[c.suit.ordinal()];
			return suit.find(c);
			/*
			 * Subdeck sSuit=null;
			 * 
			 * switch (c.suit) { case CLUBS: sSuit = clubs; break; case DIAMONDS: sSuit =
			 * clubs; break; case HEARTS: sSuit = clubs; break; case SPADES: sSuit = clubs;
			 * break; } return sSuit.find(c);
			 */
		}

		int jRandom(int upperBound) {
			double d = Math.random();
			int i = (int) (d * 100.0);
			return i % upperBound;
		}

		Subdeck randomSuit() {
			int iSuit = jRandom(4);
			Suit suit = Suit.value(iSuit);
			Subdeck sd = null;
			// don't return a suit with no cards in it, if we can help it.
			for (int i = 0; i < Suit.size(); i++) {
				sd = suits[suit.ordinal()];
				if (sd.size() == 0)
					if (suit == Suit.last())
						suit = Suit.first();
					else 
						suit = suit.next();
				else
					break;
			}
			return sd;
			/*
			 * switch (iSuit) { case 0: sd = clubs; break; case 1: sd = diamonds; break;
			 * case 2: sd = hearts; break; case 3: sd = spades; break; default:
			 * System.out.println("randomSuit: Can't happen:" + iSuit); sd = hearts; }
			 */

		}

		boolean coinFlip() {
			int coin = jRandom(2);
			return coin == 1 ? true : false;
		}

		/*
		Card randomNon(Suit nonst) {

			// how many are there non-suit left?
			int i=0;
			for (Suit st : Suit.suits) {
				if (st == nonst)
					continue;
				else 
					i += hand.count(st);
			}
			if (i == 0)
				return null;
			else if (i == 1)
				return peek();
			
		}
		*/
		Card randomCard() {
			// int iSuit = jRandom(4);
			if (size() == 0)
				return null;
			if (size() == 1)
				return peek();

			Subdeck rs = randomSuit();
			// randomSuit is guaranteedd to work...
			// this is unnecessary...
			for (int i = 0; rs.size() == 0; i++) {
				rs = randomSuit();
				if (i > 150) {
					System.out.println("randomCard: Can't happen, too many attempts:" + i);
					break;
				}
			}
			// rs is a non-void suit
			int isuitsize = rs.size();
			int index = 0;
			if (isuitsize >= 2)
				index = jRandom(isuitsize-1);
			Card c = rs.elementAt(index);
			return c;
			/*
			 * // just returns the first card in the suit.. if (coinFlip()) return
			 * rs.peekLast(); else return rs.peek();
			 */
		}

		int remainingSpades() {
			int spadesPlayed = batteryBrain.countSuitPlayed(Suit.SPADES);
			return 13 - (hand.count(Suit.SPADES) + spadesPlayed);
		}

		int remainingCardsIn(Suit st) {
			int suitPlayed = batteryBrain.countSuitPlayed(st);
			return 13 - (hand.count(st) + suitPlayed);
		}



		/*
		 * if (presumptiveSpade != null && !hand.find(queenOfSpades)) return
		 * presumptiveSpade; // simple; draw out the queen if (presumptiveSpade != null
		 * && hand.find(queenOfSpades)) { // ok, not so simple // do I have the A & K?
		 * Otherwise able to shoot moon? // are most of the spades out?? // Can I run
		 * everyone else out of spades? // What's the best suit-candidate to slough //
		 * Am I void in anything now? int spadesOutThere=remainingSpades(); int
		 * spadesInHand=hand.count(queenOfSpades.suit); if (spadesOutThere == 0) { //
		 * got it! // lead something low ;
		 * 
		 * } else if (spadesInHand > spadesOutThere) { // if there is a spade lower than
		 * the queen play it // otherwise play the K or A } } if (getspades().size() >
		 * 0) return getspades().subdeck.peek();
		 * 
		 * if (bHeartsBroken && remainingCardsIn(Suit.HEARTS) > 0) { // // don't play a
		 * card no-one else has if (hand.find(threeOfHearts)) // if you have both play 3
		 * first... return threeOfHearts; if (hand.find(deuceOfHearts)) return
		 * deuceOfHearts; } /* lead lowest card in shortest suit, maybe? until I can do
		 * some analysis...
		 */
		// should I claim the rest?
		// }

		// May as well claim the rest
		// snark guess I have the rest...
		// return hand.randomCard();
		/*
		 * if (getclubs().subdeck.size() > 0) return getclubs().subdeck.peek(); if
		 * (getdiamonds().subdeck.size() > 0) return getdiamonds().subdeck.peek(); /*
		 * TODO: leading the 2H could be good, if hearts broken
		 *
		 * if (gethearts().subdeck.size() > 0) return gethearts().subdeck.peek(); return
		 * null;
		 */
		// }

		// maybe...
		// not used in this form...
		/*
		 *
		 * // strategy functions... boolean willRunTable() { boolean bRunPossible =
		 * false; //Suit suit=null; for (Suit st : Suit.suits) if
		 * (hand.getSuit(st).size() > 0 && remainingCardsIn(st) > 0) { bRunPossible =
		 * true; // i.e. you have all of suit st //suit = st; } return false; }
		 */


		/*
		 * hand utility functions
		 */
		void addCard(Card c) {
			Subdeck sd = hand.getSuit(c.suit);
			sd.add(c);
		} // add

		void deleteCard(Card c) {
			Subdeck sd = hand.getSuit(c.suit);

			// Subdeck sd = suits[c.suit.ordinal()];
			sd.delete(c);
			/*
			 * switch (c.suit) { case CLUBS: hand.clubs.delete(c); break; case DIAMONDS:
			 * hand.diamonds.delete(c); break; case HEARTS: hand.hearts.delete(c); break;
			 * case SPADES: hand.spades.delete(c); break; }
			 */

			return;
		} // delete

	} // Hand

} // RobotBrain
