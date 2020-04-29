package server.ws;

//package cardGame;

//import RobotBrain.Hand;

public class RobotBrain {
	int trickCount = 0;
	int pid;
	Hand hand = new Hand();

	void setPID(int n) {
		pid = n;
	}

	boolean bThinkingOutLoud=false;
	void thinkOutLoud(boolean b) {
		bThinkingOutLoud = b;
	}
	void brainDump() {
		bThinkingOutLoud = true;
	}
	void brainDump(boolean bNow) {
		if (!bNow)
			brainDump();	// just make this robot chatty...
		System.out.println("Bad Subdecks?:" + this.currentTrickId() 
		+ "c:{"+ hand.getclubs()
		+ "}d:{" + hand.getdiamonds()
		+ "}h:{"+ hand.gethearts()
		+ "}s:{" + hand.getspades()
		);
		System.out.println("Trick:" + this.currentTrickId() 
		+ "c:{"+ hand.getclubs().encode()
		+ "}d:{" + hand.getdiamonds().encode()
		+ "}h:{"+ hand.gethearts().encode()
		+ "}s:{" + hand.getspades().encode() + "}"
		);
	}
	Card playAnything() {
		Card c = null;
		c = new Card(Rank.ACE, Suit.SPADES);
		return c;
	}
	
	Card playCard() {
		Card c = null;
		if (trickCount == 0) {
			// Make sure I have 13 cards in my hand
			//  or else declare a misdeal (or at least whine)
			if (hand.size() != 13)
				System.out.println("Robot(?) cards=" + hand.size());
		}
		if (cardLead == null) {
			/*
			 * I have the lead...
			 */
			if (bThinkingOutLoud) {
				System.out.println("Robot: I have the lead...");
			}
			if (trickCount == 0) {
				// unusual... Void in clubs...
				System.out.println("Can't happen: void in clubs and I have the lead on the first trick.");
				c = new Card(Rank.ACE, Suit.SPADES);
				return c;
			}
			// Play a spade if I have one
			if (hand.getspades().size() > 0) {
				c = hand.getspades().peek(); // get lowest...
			} else {
				c = hand.bestLead(); // get the best card to lead
			}
			if (bThinkingOutLoud) {
				System.out.println("Robot: About to play..." 
						+ c.encode());
			}
			return c;
		}
		/*
		 * must follow rule
		 */
		if (!hand.voidIn(cardLead.suit)) {
			c = hand.highest(cardLead.suit);
		}
		else {
			// void in the lead suit. Slough something
			c = hand.bestSlough(trickCount);
		}
		
		if (c.equals(Rank.TEN, Suit.CLUBS))
			System.out.println("About to play 10c");
		return c;
	}

	/*
	 * RobotBrain::getPass
	 */
	Subdeck getPass(int size) {
		Subdeck pass = new Subdeck();
		//
		// preferentially pass the 2c
		if (hand.find(deuceOfClubs))
			pass.add(deuceOfClubs);
		
		for (int tries = 0; pass.size() < 3 && tries < 50;) {
			Card c = hand.randomCard();
			if (c == null)
				break;
			if (pass.find(c))
				tries++;
			else
				pass.add(c);
		}
		if (pass.size() != 3)
			System.out.println("Cannot find 3 cards to pass...");
		for(Card c : pass.subdeck)
			if (c.rank == Rank.DEUCE &&
				c.suit == Suit.CLUBS)
				System.out.println("Robot("+ pid + ")Passing the 2C... Har, har");
		return pass;
	}

	/*
	 * RobotBrain::addCards and deleteCard; know what's in the hand
	 */
	void addCards(Subdeck sd) {
		hand.populate(sd);
	}

	void deleteCard(Card c) {
		hand.delete(c);
	}

	/*
	 * Essence of RobotBrain -- remember what has been played...
	 * RobotBrain::addCards
	 */
	//
	// TODO: notePlayed et. all: This is a total hack and should be revisited
	Trick currentTrick = null;
	int currentLeader = -1;
	Card cardLead = null;
	// int iCurrentTrick=0;

	int currentTrickId() {
		return trickCount;
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
		if (currentTrick != null && currentTrick.heartsBroken())
			t.breakHearts();
		currentTrick = t;
		trickCount++;
	}

	/*
	 * ToDO: do something with who won the trick... Me for example. compute moon
	 * shooting, etc.
	 */
	void trickCleared(Trick trick) {
		// Show the trick... aaa
		String s = trick.encode();
		System.out.println("Robot" + pid + " clearing:" + s);
		newTrick();
	}

	Card queenOfSpades = new Card(Rank.QUEEN, Suit.SPADES);
	Card deuceOfClubs = new Card(Rank.DEUCE, Suit.CLUBS);

	class Hand {
		//Subdeck clubs = new Subdeck(), diamonds = new Subdeck(), hearts = new Subdeck(), spades = new Subdeck();
		Subdeck[] suits=new Subdeck[4];
		
		Subdeck getSuit(Suit suit) { 
			return suits[suit.ordinal()];
			}
		
		int size() {
			int i=0;
			int ncards=0;
			Suit st;
			for (st=Suit.first(); i < Suit.size(); st=st.next(), i++) {
				ncards += suits[st.ordinal()].size();
				}
			return ncards;
		}
		Hand() {
			int i = 0;
			for (Suit s = Suit.first(); i < Suit.size(); i++) {
				suits[s.ordinal()] = new Subdeck();
				s = s.next();
			}
		}
		
		Subdeck getclubs() { return suits[Suit.CLUBS.ordinal()]; }
		Subdeck getdiamonds() { return suits[Suit.DIAMONDS.ordinal()]; }
		Subdeck gethearts() { return suits[Suit.HEARTS.ordinal()]; }
		Subdeck getspades() { return suits[Suit.SPADES.ordinal()]; }

		boolean voidIn(Suit st) {
			return suits[st.ordinal()].size() == 0;
		}
		
		/*
		 * highest -- highest card in suit
		 */
		Card highest(Suit st) {
			Subdeck sd=suits[st.ordinal()];
			Card highestSoFar=null;
			for (Card c : sd)
				if (highestSoFar == null)
					highestSoFar = c;
				else 
					highestSoFar = highestSoFar.higherCard(c);
				
			return highestSoFar;
		}
		
		void populate(Subdeck sd) {
			for (Card c : sd.subdeck) {
				suits[c.suit.ordinal()].add(c);
			}
		} // populate
		
		Card bestSlough() {
			if (getspades().find(queenOfSpades))
				return queenOfSpades;
			if (gethearts().size() > 0)
				return gethearts().peek();
			// TODO: Highest card in hand?
			// ... guess
			return hand.toxicCard();
		}
		
		/*
		 * TODO: get the 1-per-lifetime cases of 13 Hearts, or QS+12 Hearts
		 */
		Card bestSlough(int trick) {
			if (trick != 0)
				return bestSlough();
			Suit s;
			for (s=Suit.first(); s != Suit.last(); s=s.next())
				if (!voidIn(s))
					return highest(s);
			return randomCard();
		}
		
		/*
		 * find - find a card in the hand
		 */
		boolean find(Card c) {
			Subdeck suit=suits[c.suit.ordinal()];
			return suit.find(c);
			/*
			Subdeck sSuit=null;
			
			switch (c.suit) {
			case CLUBS:
				sSuit = clubs;
				break;
			case DIAMONDS:
				sSuit = clubs;
				break;
			case HEARTS:
				sSuit = clubs;
				break;
			case SPADES:
				sSuit = clubs;
				break;
			}
			return sSuit.find(c);
*/
		}

		Card toxicCard() {
			if (find(queenOfSpades))
				return queenOfSpades;
			if (gethearts().size() > 0)
				return gethearts().peek();
			if (getclubs().size() > 0)
				return getclubs().peek();
			if (getdiamonds().size() > 0)
				return getdiamonds().peek();
			if (getspades().size() > 0)
				return getspades().peek();
			System.out.println("RobotPlayer: Can't happen/Can't find any cards when I need them");
			return null;
		}

		int jRandom(int upperBound) {
			double d = Math.random();
			int i = (int) (d * 100.0);
			return i % upperBound;
		}

		Subdeck randomSuit() {
			int iSuit = jRandom(4);
			Suit suit=Suit.value(iSuit);
			Subdeck sd=null;
			// don't return a suit with no cards in it, if we can help it.
			for (int i=0; i<4; i++) {
				sd = suits[suit.ordinal()];
				if (sd.size() == 0)
					suit = suit.next();
				else
					break;
			}
			return sd;
			/*
			switch (iSuit) {
			case 0:
				sd = clubs;
				break;
			case 1:
				sd = diamonds;
				break;
			case 2:
				sd = hearts;
				break;
			case 3:
				sd = spades;
				break;
			default:
				System.out.println("randomSuit: Can't happen:" + iSuit);
				sd = hearts;
			}
			*/
			
		}

		boolean coinFlip() {
			int coin = jRandom(2);
			return coin == 1 ? true : false;
		}

		Card randomCard() {
			int iSuit = jRandom(4);
			Subdeck rs = randomSuit();
			for (int i = 0; rs.size() == 0; i++) {
				rs = randomSuit();
				if (i > 150) {
					System.out.println("randomCard: Can't happen:" + iSuit);
					break;
				}
			}
			// just returns the first card in the suit..
			if (coinFlip())
				return rs.peekLast();
			else
				return rs.peek();
		}

		Card bestLead() {
			// leading spades if possible is best idea I have right now...
			if (getspades().size() > 0)
				return getspades().subdeck.peek();
			/*
			 * lead lowest card in shortest suit, maybe?
			 *  until I can do some analysis...
			 */
			if (getclubs().subdeck.size() > 0)
				return getclubs().subdeck.peek();
			if (getdiamonds().subdeck.size() > 0)
				return getdiamonds().subdeck.peek();
			if (gethearts().subdeck.size() > 0)
				return gethearts().subdeck.peek();
			return null;
		}

		void delete(Card c) {
			Subdeck sd=suits[c.suit.ordinal()];
			sd.delete(c);
			/*
			switch (c.suit) {
			case CLUBS:
				hand.clubs.delete(c);
				break;
			case DIAMONDS:
				hand.diamonds.delete(c);
				break;
			case HEARTS:
				hand.hearts.delete(c);
				break;
			case SPADES:
				hand.spades.delete(c);
				break;
			}*/
			
			return;
		} // delete

	} // Hand

} // RobotBrain
