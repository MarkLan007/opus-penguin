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

	Card playCard() {
		Card c = null;
		if (cardLead == null) {
			/*
			 * I have the lead...
			 */
			if (trickCount == 0) {
				// unusual... Void in clubs...
				System.out.println("Can't happen: void in clubs and I have the lead.");
				c = new Card(Rank.ACE, Suit.SPADES);
				return c;
			}
			// Play a spade if I have one
			if (hand.spades.size() > 0) {
				c = hand.spades.peek(); // get lowest...
			} else {
				c = hand.bestLead(); // get the best card to lead
			}
			return c;
		}
		/*
		 * must follow
		 */
		switch (cardLead.suit) {
		case CLUBS:
			c = hand.clubs.subdeck.peek();
			break;
		case DIAMONDS:
			c = hand.diamonds.subdeck.peek();
			break;
		case HEARTS:
			c = hand.hearts.subdeck.peek();
			break;
		case SPADES:
			c = hand.spades.subdeck.peek();
			break;
		}
		if (c == null) {
			// void in the lead suit. Slough something
			c = hand.bestSlough();
		}
		return c;
	}

	/*
	 * RobotBrain::getPass
	 */
	Subdeck getPass(int size) {
		Subdeck pass = new Subdeck();
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
				System.out.println("Passing the 2C... Har, har");
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

	class Hand {
		Subdeck clubs = new Subdeck(), diamonds = new Subdeck(), hearts = new Subdeck(), spades = new Subdeck();

		void populate(Subdeck sd) {
			// Card c=null;
			for (Card c : sd.subdeck) {
				switch (c.suit) {
				case CLUBS:
					clubs.add(c);
					break;
				case DIAMONDS:
					diamonds.add(c);
					break;
				case HEARTS:
					hearts.add(c);
					break;
				case SPADES:
					spades.add(c);
					break;
				}
			}
		} // populate

		Card queenOfSpades = new Card(Rank.QUEEN, Suit.SPADES);

		Card bestSlough() {
			if (spades.find(queenOfSpades))
				return queenOfSpades;
			if (hearts.size() > 0)
				return hearts.peek();
			// TODO: Highest card in hand?
			// ... guess
			return hand.toxicCard();
		}

		Card toxicCard() {
			if (hearts.size() > 0)
				return hearts.peek();
			if (clubs.size() > 0)
				return clubs.peek();
			if (diamonds.size() > 0)
				return diamonds.peek();
			if (spades.size() > 0)
				return spades.peek();
			System.out.println("RobotPlayer: Can't happen/Can't find any cards when I need them");
			return queenOfSpades;
		}

		int jRandom(int upperBound) {
			double d = Math.random();
			int i = (int) (d * 100.0);
			return i % upperBound;
		}

		Subdeck randomSuit() {
			int iSuit = jRandom(4);
			switch (iSuit) {
			case 0:
				return clubs;
			case 1:
				return diamonds;
			case 2:
				return hearts;
			case 3:
				return spades;
			default:
				System.out.println("randomSuit: Can't happen:" + iSuit);
				return hearts;
			}
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
			if (spades.subdeck.size() > 0)
				return spades.subdeck.peek();
			if (clubs.subdeck.size() > 0)
				return clubs.subdeck.peek();
			if (diamonds.subdeck.size() > 0)
				return diamonds.subdeck.peek();
			if (hearts.subdeck.size() > 0)
				return hearts.subdeck.peek();
			return null;
		}

		void delete(Card c) {
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
			}
			return;
		} // delete

	} // Hand

} // RobotBrain
