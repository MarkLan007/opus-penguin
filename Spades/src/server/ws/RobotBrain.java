package server.ws;

import java.util.Arrays;
//package cardGame;

//import RobotBrain.Hand;

public class RobotBrain {
	int trickCount = 0;
	int pid;
	Hand hand = new Hand();
	BatteryBrain batteryBrain = new BatteryBrain();
	boolean bHeartsBroken = false;
	boolean bMoonPolice = false;
	boolean bMoonShooting = false;

	final int nPlayers = 4;
	int mySeat = -1;

	void setPID(int n) {
		pid = n;
	}

	/*
	 * true if hearts broken on previous trick
	 */
	boolean heartsAlreadyBroken() {
		return bHeartsBroken;
	}

	/*
	 * hearts have been player OR I have nothing but hearts left...
	 */
	boolean heartsBroken() {
		if (bHeartsBroken)
			return true;
		// Now I could have nothing but hearts left...
		// look at all my suits, and if there is at least one non-heart
		// then hearts aren't forced open
		int i = 0;
		for (Suit st = Suit.first(); i < Suit.size(); st = st.next(), i++)
			if (st != Suit.HEARTS && hand.getSuit(st).size() > 0)
				return false;
		return true;
	}

	RobotBrain(int pid) {
		mySeat = pid; // can this change? If it does I'm screwed
	}

	void reset() {
		// seat should not change...
		trickCount = 0;
		currentTrick = null;
		// otherwise robot screwed up on first trick following
		hand.clear();
		batteryBrain.reset();
		bHeartsBroken = false;
		bMoonPolice = false;
		bMoonShooting = false;
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
		if (hand.find(deuceOfClubs)) {
			brainDump("Robot: I have the 2...");
			// System.out.println("Robot: I have the 2...");
			return deuceOfClubs;
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

		/*
		 * favor drawing out spades unless...
		 */
		Card presumptiveSpade = hand.highestCardUnder(queenOfSpades);
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
			if (batteryBrain.isPlayed(queenOfSpades))
				presumptiveSpade = null;
			if (!hand.find(queenOfSpades) && presumptiveSpade != null) {
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
			if (c.equals(queenOfSpades) || c.equals(kingOfSpades))
				brainDump(c, "Yikes! *** About to Lead ***");
			if (bThinkingOutLoud)
				brainDump(c, "About to lead:");
			//System.out.println("Robot: About to play..." + c.encode());
			return c;
		}
		/*
		 * apply the must-follow rule
		 */
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
			if (cardInTrick(aceOfSpades) || cardInTrick(kingOfSpades)) {
				if (hand.find(queenOfSpades))
					return queenOfSpades;
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
		boolean hasAS = (hand.find(aceOfSpades));
		boolean hasKS = (hand.find(kingOfSpades));
		boolean hasQS = (hand.find(queenOfSpades));
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
				pass.add(queenOfSpades);
				if (hasKS)
					pass.addWithMax(kingOfSpades, nCardsToPass);
				if (hasAS)
					pass.addWithMax(aceOfSpades, nCardsToPass);
			}
		} else if ((hasAS && hasKS && totalSpades < 4) || ((hasAS || hasKS) && totalSpades < 3)) {
			if (tophearts >= 3)
				bMoonShooting = true;
			else {
				if (hasAS)
					pass.addWithMax(aceOfSpades, nCardsToPass);
				if (hasKS)
					pass.addWithMax(kingOfSpades, nCardsToPass);
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
		if (hand.find(deuceOfClubs) && !pass.find(deuceOfClubs)) {
			pass.addWithMax(deuceOfClubs, nCardsToPass);
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
				if (CardGame.isHigher(card, ctemp))
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
				if (CardGame.isHigher(card, ctemp))
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
	
	boolean badHearts() {
		Subdeck hearts=hand.gethearts();
		if (hearts.size() == 0)
			return false;	// no bad hearts
		
		Card lowestHeart=hand.lowest(Suit.HEARTS);
		int beaters = batteryBrain.higherCardsOutThere(lowestHeart);
		//if (batteryBrain.higherCardOutThere(lowestHeart))
			//return false;
		
		int tophearts = hand.hasTop(Suit.HEARTS);
		if (tophearts > beaters) {
			brainDump("Close but... go launch!");
			return false;
		} else {
			brainDump("Close but NOT going for it...");
		}
		
		return true;
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
		if (currentTrick != null && currentTrick.heartsBroken()) {
			t.breakHearts();
			bHeartsBroken = true;
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
			if (c.suit == Suit.HEARTS) {
				batteryBrain.pointTotals[winner]++;
				bHeartsBroken = true;
			} else if (c.equals(queenOfSpades))
				batteryBrain.pointTotals[winner] += 13;
		}
		newTrick();
	}

	Card aceOfSpades = new Card(Rank.ACE, Suit.SPADES);
	Card kingOfSpades = new Card(Rank.KING, Suit.SPADES);
	Card queenOfSpades = new Card(Rank.QUEEN, Suit.SPADES);
	Card deuceOfClubs = new Card(Rank.DEUCE, Suit.CLUBS);
	Card deuceOfHearts = new Card(Rank.DEUCE, Suit.HEARTS);
	Card threeOfHearts = new Card(Rank.THREE, Suit.HEARTS);

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
					if (sd.find(queenOfSpades) && sd.size() == 1) {
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
		 * biggestLoser -- card least likely to take tricks
		 * Review: Hearts specific??? Not properly part of Hand???
		 */
		Card biggestLoser(Suit suitToAvoid) {
			Card candidate = null;
			int beaters = 0;

			for (Suit suit : Suit.suits) {
				if (suit == suitToAvoid)
					continue;
				Subdeck sd=getSuit(suit);
				//
				// the lowest card in a suit I can't run
				// Can I run this suit?
				// if not, return the lowest card
				if (sd.size() == 0)
					continue;
				candidate = highest(suit);
				int b = batteryBrain.higherCardsOutThere(candidate);
				if (b > beaters) {
					beaters = b;
					candidate = lowest(suit);
				}
			}
			// i.e. lowest card in a suit that there are higher
			// cards out there...
			if (candidate != null)
				return candidate;

			// No big losers! ok, good news
			if (bThinkingOutLoud) {
				System.out.println("Robot(seat" + mySeat + "): Preparing for moon landing.");
			}

			candidate = lowestNonPoint();
			if (candidate != null)
				return candidate;
			// uh oh. Bad news: no nonpt cards
			if (bThinkingOutLoud) {
				System.out.println("Robot(seat" + mySeat + "): Abort! No nonpt cards to slough!");
			}
			if (find(queenOfSpades))
				return queenOfSpades;
			return highest(Suit.HEARTS);
		}

		/*
		 * return a generally toxic card from the hand called by slough
		 * Review: Hearts specific??? Not properly part of Hand???
		 */
		Card toxicCard(boolean moonpolice) {
			// moonpolice or not...
			if (find(queenOfSpades))
				return queenOfSpades;
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
				if (hand.find(kingOfSpades))
					return kingOfSpades;
				if (hand.find(aceOfSpades))
					return aceOfSpades;
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
					continue;
				if (ncards == 0 && !voidIn(st)) {
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
				if (!CardGame.isHigher(candidate, c))
					candidate = c;
			}
			return candidate;
		}

		/*
		 * currentLeader - card that is the current leader of a trick
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
				} else if (c.suit == st && CardGame.isHigher(c, leadingCard)) { // true if c is higher than leadingcard
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

		/*
		 * duckUnderSpade - different because I don't care if I take a spade trick
		 * (usually). And if I have the queen I want to avoid playing it unless someone
		 * has a higher card on the trick already. (or if the only cards left to be
		 * played are the AK ... ... already determined if I can slough... // the whole
		 * point of this is to avoid the queen // so this algorithm will return the
		 * queen unless // it's pulled from my myCards and only return it // as a last
		 * resort
* 		 * Review: Hearts specific??? Not properly part of Hand???
		 */
		Card duckUnderSpade(Trick trick) {
			Card cardToDuck = highestCardInTrick(trick);
			Suit st = cardToDuck.suit;
			Subdeck myCards = hand.getSuit(st);
			// if I have the queen and it's my only spade,
			// just admit defeat.
			boolean iHaveTheQueen = myCards.find(queenOfSpades);
			if (iHaveTheQueen) {
				if (trick.cardPlayed(aceOfSpades) || trick.cardPlayed(kingOfSpades))
					return queenOfSpades;
				if (hand.getspades().size() == 1) {
					// snark: Oh crap. Can't be helped.
					return queenOfSpades;
				}
			} else if (trick.cardPlayed(queenOfSpades)) {
				return duckUnder(trick);
			}
			// TODO: might do something else if I'm moonshooting
			// or want to be the moonpolice
			return duckUnder(trick);

			/*
			 * // Great find another spade. // can I get under? for (Card c : myCards) //
			 * get highest card below lead if (CardGame.isHigher(c, cardToDuck))// i.e.
			 * 2c,Ac -> F if (candidate == null) candidate = c; else candidate =
			 * candidate.higherCard(c);
			 * 
			 * if (candidate == null) { // couldn't find one candidate = hand.lowest(st); if
			 * (ct.subdeck.size() == 3) // if not, am I last? candidate = hand.highest(st);
			 * } // otherwise take with highest (unless moonshooting) if (candidate == null)
			 * candidate = lastResort; return candidate;
			 */
		}

		/*
		 * duckUnder(Trick trick) - do everthing possible to duck the trick if can't be
		 * ducked either be lowest or if inevitable take with highest return null only
		 * if no cards of the suit in hand
		 * 
		 * duckunder(trick) - avoid taking this trick if possible if not possible take
		 * with the highest card in the suit. special case for spades/queen of spades
		 * handled separately
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
				if (CardGame.isHigher(c, cardToDuck))// i.e. 2c,Ac -> F
					if (candidate == null)
						candidate = c;
					else
						candidate = candidate.higherCard(c);
			return candidate;
		}

		/*
		 * commandLead -- bad name...
		 * rubicon -- cross or turn back
		 *   considering whether to go for moon or bleed out high cards
		 *   return null if nothing good.
		 */
		Card commandLead() {
			// note: could have something like this:
			// c:{8C4C} d:{JD7D3D} h:{AH5H} s:{KSQS}
			// can I draw out spades to slough queen?
			// can I lead the queen and force it to be taken
			// can I shoot moon? 
			// do I have vulnerable hearts?
			// is there a path to the moon?
			Card presumptiveLead = highestCardShortestSuitNon(Suit.HEARTS, Suit.SPADES);
			
			/*
			 * spades gambit
			 */
			// do I have the queen?
			// are there higher spades than the queen out there?
			//   how many?... (ToDO: Maybe... Can I draw them out?)
			// how many backers out there?
			if (hand.find(queenOfSpades)) {	// double check...
				int higherspades = batteryBrain.higherCardsOutThere(queenOfSpades);
				int myspades = hand.getspades().size();
				int lowerCardsOutThere = 13 - (higherspades + myspades);
				if (higherspades > 0 && lowerCardsOutThere <= 0) {
					brainDump("Bravely leading queen...");
					presumptiveLead = queenOfSpades;
				}				
			}
			/*
			 * heart holes? how big?
			 */
			
			/*
			 * Ok, so if I have heartholes and the queen
			 * bleed off high cards... Highest card lowest leadable suit
			 */

			return presumptiveLead;
		}

		/*
		 * countHigherCards - count the number of cards higher than this that haven't
		 * been played that aren't in your hand
		 */
		int countHigherCards(Card c) {
			int n = 0;
			Suit suit = c.suit;
			for (Rank rank : Rank.values()) {
				Card c2 = new Card(rank, suit);
				if (CardGame.isHigher(c2, c) && !hand.find(c2) && !batteryBrain.isPlayed(c2))
					n++;
			}
			return n;
		}

		/*
		 * duckLead - card in hand with most cards above it not in hand or null if there
		 * aren't any...
		 */
		Card duckLead() {
			// best chance to lose lead is the card with the most
			// outstanding cards higher than yours that you don't have
			Card candidate = null;
			int highestSoFar = 0;
			for (int i = Suit.first().ordinal(); i <= Suit.last().ordinal(); i++) {
				Card lc = hand.lowest(Suit.value(i));
				if (lc == null)
					continue;
				if (lc.suit == Suit.HEARTS && !heartsBroken())
					continue;
				int countHigherCardsInSuit = countHigherCards(lc);
				if (countHigherCardsInSuit <= 0)
					continue;
				if (candidate == null) {
					highestSoFar = countHigherCardsInSuit;
					candidate = lc;
				} else if (countHigherCardsInSuit > highestSoFar) {
					highestSoFar = countHigherCardsInSuit;
					candidate = lc;
				}
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
				if (c.equals(card) || CardGame.isHigher(c, card)) // i.e. 2c,Ac -> F So AS,QS continue...
					;
				else if (bestSoFar == null)
					bestSoFar = c;
				else if (CardGame.isHigher(c, bestSoFar))
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
		 * bestSlough for non-first trick
		 */
		Card bestSlough() {
			Card card = null;
			if (size() == 1)
				return peek();

			if (shootMoonOrDumpPoints()) {
				card = biggestLoser(Suit.HEARTS);
				if (bThinkingOutLoud) {	// going for it...
					System.out.println("Robot(seat" + mySeat + "): Thinking moon...");
				}
				if (card != null && bThinkingOutLoud) {	// going for it...
					System.out.println("Brain: Best slough for moon shooter:"+ card.encode() +"?!");
					brainDump(true);
				}
				return card;
			} else
				card = highestCardShortestSuitNon(Suit.HEARTS, Suit.SPADES);

			if (card != null && bThinkingOutLoud) {	// going for it...
				System.out.println("Brain: Best slough no moon dreams:"+ card.encode() +"?!");
				brainDump(true);
			}
			if (card != null)
				return card;
			// play something low, and avoid breaking hearts
			// not moonshooting...
			card = toxicCard(bMoonPolice);
			if (bThinkingOutLoud) {	// going for it...
				if (card == null)
					System.out.println("Best slough:(null/random)?");
				else
					System.out.println("Best slough:" + card.encode() + "?");
				brainDump(true);
			}
			if (card == null)
				card = randomCard();
			return card;
		}

		/*
		 * TODO: get the 1-per-lifetime cases of 13 Hearts, or QS+12 Hearts
		 */
		Card bestSlough(int trick) {
			// this is just the best first-trick special case...
			if (trick != 0)
				return bestSlough();
			/*
			 * -- NOTE --- slough for JUST the FIRST TRICK...
			 */
			//
			// If you have the QS don't slough a spade
			// i.e. any spade is a backer...
			//
			boolean saveSpades = hand.find(queenOfSpades);
			if (!saveSpades && hand.find(kingOfSpades))
				return kingOfSpades;
			if (!saveSpades && hand.find(aceOfSpades))
				return aceOfSpades;
			// Ok, can't or shouldn't slough the AS or KS.
			// if moonshooting play something low
			//
			// if moonshooting, play lowest card that isn't a top card in suit
			// if !moonshooting, play highest card in shortest suit
			Card card = null;
			if (shootMoonOrDumpPoints()) {
				card = lowestCardNotTop(Suit.HEARTS, Suit.SPADES); // moonshooting
				if (card == null)
					card = lowestCardInHand(Suit.HEARTS, Suit.SPADES);
			} else
				card = highestCardShortestSuitNon(Suit.HEARTS, Suit.SPADES);
			if (card != null)
				return card;
			// if we have made it this far, we couldn't find
			// a legal non-spade, non-point card to slough.
			// So we only have Spades and point cards...
			// (Still first trick...)
			// Ok... I have only spades and hearts?
			// Crikey! how many spades do I have?
			System.out.println("Crikey! Take a picture. First Trick:");
			brainDump(true);

			int nspades = hand.getspades().size();
			if (nspades == 1) {
				bMoonShooting = true;
				return hand.getspades().peek();
			} else if (nspades > 1) {
				// Rats. I have to get rid of a useful backer
				// and possibly the only card that could prevent
				// me from shooting the moon...
				Card highest = hand.highestNon(queenOfSpades);
				return highest;
			} else { // All hearts and the queenofspades
				// make sure
				int ptcards = 0;
				Card nonPtCard = null;
				int i = 0;
				for (Suit st = Suit.first(); i < Suit.size(); st = st.next(), i++) {
					if (st == Suit.HEARTS)
						ptcards++;
					else if (st == Suit.SPADES) {
						Subdeck spades = hand.getspades();
						for (Card c : spades.subdeck)
							if (c.equals(queenOfSpades))
								ptcards++;
							else {
								nonPtCard = c;
							}
					} else {
						for (Card c : hand.getSuit(st)) {
							ptcards++;
							nonPtCard = c;
						}
					}
				}
				if (nonPtCard != null) {
					System.out.println("Can't happen? But ok... Ptcards=" + ptcards);
					return nonPtCard;
				}
				if (nonPtCard == null) {
					System.out.println("Can't happen: first-card search problem.");
					card = randomNonPointCard();
					if (card != null)
						return card;
				}
			}
			return hand.lowest(Suit.HEARTS); // moon...
		}

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

		Card randomNonPointCard() {
			if (size() == 0)
				return null;
			if (size() == 1)
				return peek();

			Card c = randomCard();
			for (int i = 0; i < 50; c = randomCard(), i++) {
				if (c.suit == Suit.HEARTS)
					continue;
				if (c.equals(queenOfSpades))
					continue;
				break;
			}
			return c;
		}

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
		 * true if queen is out and I don't have it
		 */
		boolean someoneElseHasQueen() {
			if (hand.find(queenOfSpades))
				return false; // someone has it and it's me...
			if (!batteryBrain.isPlayed(queenOfSpades))
				return true; // hasn't been played, so...
			return false;
		}

		// TODO: Move this out from Hand.
		// Hand methods should be generic for all routines, not Hearts Specific...
		Card bestLead(Card presumptiveSpade) {
			// leading spades if possible is best idea I have right now...
			// So I won't play the presumptive spade if
			// .. I have the queen
			// ... unless I'm running everyone out of spades
			Card candidate = null;
			if (shootMoonOrDumpPoints()) { // shoot moon!
				// don't think about shooting the moon till the queen is out...
				if (someoneElseHasQueen() && presumptiveSpade != null)
					return presumptiveSpade;
				if (someoneElseHasQueen())
					candidate = duckLead();
				else {
					candidate = commandLead();
					if (candidate != null)
						brainDump(candidate, "Thinking moon...");
				}
			} else if (hand.find(queenOfSpades)) {
				candidate = commandLead();
			}
			if (candidate == null) {
				do {
					candidate = hand.randomCard();
				} while (candidate.suit == Suit.HEARTS && !heartsBroken());
			}
			return candidate;
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
		 * canLoseTrick if there is a card that I could play that could be taken by
		 * someone else
		 * 
		 * return the lead loser that I'm longest in... i.e. fewest number of cards I
		 * have to lose to shoot moon...
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

		final boolean bShootMoon = true;
		final boolean bDumpPoints = false;

		/*
		 * true if I should shoot the moon false if I should dump points
		 */
		void setMoonPolice() {
			bMoonPolice = true;
		}

		void setMoonPolice(boolean bOnOff) {
			bMoonPolice = bOnOff;
		}

		boolean shootMoonOrDumpPoints() {
			if (!moonPossibleForSomeone()) {
				setMoonPolice(false);
				return bDumpPoints;
			} else if (!moonPossibleForMe())
				// moon possible for someone, just not me...
				setMoonPolice();

			// Soo... I could shoot the moon.
			// should I try?
			if (badHearts())
				return bDumpPoints;

			// Ok, is there a likely path to the whitehouse?
			// Is there a suit I will run?
			Card ll = leadLoser();
			if (ll == null)
				return bShootMoon; // I can't lose the lead; done;
			// Can I protect ll?
			// Are there cards in that suit I can win with?
			Card highestCardInSuit = hand.highest(ll.suit);
			// if there is a card that can take my highest, then punt
			// otherwise try to draw out cards.
			if (batteryBrain.higherCardOutThere(highestCardInSuit))
				return bDumpPoints;

			return bShootMoon;
		}

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
