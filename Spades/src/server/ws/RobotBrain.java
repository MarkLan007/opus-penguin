package server.ws;

import java.util.Arrays;
//package cardGame;

//import RobotBrain.Hand;

public class RobotBrain {
	int trickCount = 0;
	int pid;
	Hand hand = new Hand();
	BatteryBrain batteryBrain=new BatteryBrain();
	boolean globalHeartsBroken=false;
	boolean bMoonPolice = false;
	final int nPlayers=4;
	int mySeat=-1;	
	
	void setPID(int n) {
		pid = n;
	}
	
	/*
	 * hearts have been player OR I have nothing but hearts left...
	 */
	boolean heartsBroken() {
		if (globalHeartsBroken)
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
		mySeat = pid;	// can this change? If it does I'm screwed
	}

	void reset() {
		// seat should not change...
		trickCount = 0;
		currentTrick = null;
		// otherwise robot screwed up on first trick following
		hand.clear();
		batteryBrain.reset();
		globalHeartsBroken = false;
		bMoonPolice = false;
		//hand = new Hand();
	}

	boolean bThinkingOutLoud = true;

	void thinkOutLoud(boolean b) {
		bThinkingOutLoud = b;
	}

	void brainDump() {
		bThinkingOutLoud = true;
	}

	void brainDump(boolean bNow) {
		if (!bNow)
			brainDump(); // just make this robot chatty...
		
		System.out.println("BrainContents:" + this.currentTrickId() + " c:{" + hand.getclubs().size() + "}d:{"
				+ hand.getdiamonds().size() + "}h:{" + hand.gethearts().size() + "}s:{" + hand.getspades().size()
				+ "}");
		System.out.println("Trick:" + this.currentTrickId() + "c:{" + hand.getclubs().encode() + "}d:{"
				+ hand.getdiamonds().encode() + "}h:{" + hand.gethearts().encode() + "}s:{" + hand.getspades().encode()
				+ "}");
	}

	Card getSomething() {
		Card c = hand.randomCard();
		/*
		Card c = null;
		c = new Card(Rank.ACE, Suit.SPADES);
		*/
		if (c == null)
			c = new Card(Rank.ACE, Suit.SPADES);

		return c;
	}

	Card determineBestCard(int actualTrickId) {
		Card c = null;
		trickCount = actualTrickId;
		if (trickCount == 0) {
			// Make sure I have 13 cards in my hand
			// or else declare a misdeal (or at least whine)
			if (hand.size() != 13)
				System.out.println("*** Robot(?) cards=" + hand.size() + "***");
		}
		if (hand.find(deuceOfClubs)) {
			System.out.println("Robot: I have the 2...");
			return deuceOfClubs;
			}
		else if (trickCount == 0 && cardLead == null) {
			// can't happen: first trick without any clubs...
			System.out.println("Can't happen: Lead on first trick without the 2...");
			// but the show must go on...
		}
		Card presumptiveSpade=hand.highestCardUnder(queenOfSpades);
		if (cardLead == null) {	// I have the lead
			if (bThinkingOutLoud) {
				System.out.println("Robot(seat" + mySeat + "): I have the lead...");
			}
			if (trickCount == 0) {
				// can't happen.
				// I have the lead on the first trick without the 2c
				System.out.println("Can't happen: void in clubs and I have the lead on the first trick.");
				c = hand.randomCard();
				if (c == null) {
					c = getSomething();
					System.out.println("Can't play anything... <AS> returned");
				}
				return c;
			}
			// Draw out the queen of spades, unless I have it
			// Play highest spade below queen if I have one
			//  ... except if I have the queen
			if (batteryBrain.isPlayed(queenOfSpades))
				presumptiveSpade = null;
			if (!hand.find(queenOfSpades) && presumptiveSpade != null) {
				c = presumptiveSpade;
				// c = hand.getspades().peek(); // get lowest...
				// no... get a spade below the queen...
				// so if QS is still out, be carefull..
				if (bThinkingOutLoud) {
					System.out.println("Robot: Attempt to draw the queen out:" + c.encode());
				}
			} 
			if (c == null ){
				if (bThinkingOutLoud)
					System.out.println("Spades not a good choice. Look for a good lead.");
				c = hand.bestLead(presumptiveSpade); // get the best card to lead
			}
			if (c == null) {
				if (bThinkingOutLoud)
					System.out.println("Bestlead returned nothing; desparate.");
				c = getSomething();
				if (bThinkingOutLoud)
					System.out.println("Robot: Uh oh. desperate/no good choices. About to play..." + c.encode());
			}
			if (bThinkingOutLoud)
				System.out.println("Robot: About to play..." + c.encode());
			return c;
		}
		/*
		 * apply the must-follow rule
		 */
		if (bThinkingOutLoud) {
			System.out.println("Robot: must follow " 
					+ cardLead.suit 
					+ " if possible...");
		}
		if (trickCount == 0) {	// first trick; highest club or slough
			if (hand.getclubs().size() > 0)
				return hand.highest(Suit.CLUBS);
			// follow through to slough something
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
			//if (alreadyPlayed(queenOfSpades))
			if (cardLead.suit == Suit.SPADES)
				c = hand.duckUnderSpade(currentTrick);
			else
				c = hand.duckUnder(currentTrick);
			if (bThinkingOutLoud)
				System.out.println("s" + mySeat + " ducking under w/:" + c.encode());
			// if so, duck the lead (unless moonshooting...)
			// c = hand.highest(cardLead.suit);
			// c = hand.highestNon(cardLead.suit, queenOfSpades);
		} else { // Slough!
			if (bThinkingOutLoud) {
				System.out.println("Robot: can slough!");
			}
			// void in the lead suit. Slough something
			c = hand.bestSlough(trickCount);
			if (bThinkingOutLoud)
				System.out.println("s" + mySeat + "Sloughing w/:" + c.encode());
		}
		if (bThinkingOutLoud && c == null)
			System.out.println("s" + mySeat + "Dithering. No result:" + c.encode());
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

		// ideas:
		// jetison or accumulate hearts
		// don't pass spades generally
		// pass queen if backers > 3
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
		for (Card c : pass.subdeck)
			if (c.rank == Rank.DEUCE && c.suit == Suit.CLUBS)
				System.out.println("Robot(" + pid + ")Passing the 2C... Har, har");
		return pass;
	}

	/*
	 * RobotBrain::addCards and deleteCard; know what's in the hand
	 */
	void addCards(Subdeck sd) {
		hand.populate(sd);
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
		//Boolean[] memory = new Boolean[52];	// sorry... initialized to false
		Boolean[][] mem = new Boolean[Suit.suits.length][Rank.ranks.length];
		int pointTotals[]=new int[4];
		int count=0;	// count of cards played

		BatteryBrain() {
			reset();
			// todo: if being pulled into life in the middle of the game
			// find out what's been played already somehow...
		}
		void reset() {
			//Arrays.fill(mem, false);
			for (Boolean[] row: mem)
			    Arrays.fill(row, false);
			Arrays.fill(pointTotals, 0);
			count = 0;
		}
		void played(Card c) {
			mem[c.suit.ordinal()][c.rank.ordinal()] = true;
			count ++;
		}
		/*
		 * isPlayed -- if I don't really know return false
		 *  -- this is really I remember that the card has been played
		 */
		boolean isPlayed(Card c) {
			return mem[c.suit.ordinal()][c.rank.ordinal()];
		}
		int countSuitPlayed(Suit st) {
			int count = 0;
			int i = st.ordinal();
			for (int j=Rank.first().ordinal(); j<=Rank.last().ordinal(); j++)
				if (mem[i][j])
					count ++;
			return count;
		}
		// not quite right... the higher card out there
		// might be in your hand...
		// see ducklead
		boolean higherCardOutThere(Card card) {
			int suit=card.suit.ordinal();
			Rank rank=card.rank;
			int last=Rank.last().ordinal();
			for (int r=rank.ordinal(); r<=last; r++)
				if (!mem[suit][r] &&
						!hand.find(card))
					return true;
			return false;			
		}
	}
	
	boolean moonPossibleForSomeone() {
		int playersWithPoints=0;
		for (int i=0; i<nPlayers; i++)
			if (batteryBrain.pointTotals[i] > 0)
				playersWithPoints ++;
		return playersWithPoints <= 1;
	}
	
	boolean moonPossibleForMe() {
		//int myPoints=0;
		int othersWithPoints=0;
		for (int i=0; i<nPlayers; i++)
			if (i == mySeat)
				; //myPoints = batteryBrain.pointTotals[i];
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
		if (currentTrick != null && currentTrick.heartsBroken()) {
			t.breakHearts();
			globalHeartsBroken = true;
		}
		currentTrick = t;
		trickCount++;
	}

	// true if card is in trick, false otherwise
	boolean cardInTrick(Card card) {
		boolean found=false;
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
		System.out.println("Robot" + pid + " clearing:" + s);
		//
		// Keep real time score to for moon shooting / moon policing
		int winner=trick.winner;
		for (Card c: trick.subdeck)
			if (c.suit == Suit.HEARTS)
				batteryBrain.pointTotals[winner] ++;
			else if (c.equals(queenOfSpades))
				batteryBrain.pointTotals[winner] += 13;
		
		newTrick();
	}

	Card aceOfSpades = new Card(Rank.ACE, Suit.SPADES);
	Card kingOfSpades = new Card(Rank.KING, Suit.SPADES);
	Card queenOfSpades = new Card(Rank.QUEEN, Suit.SPADES);
	Card deuceOfClubs = new Card(Rank.DEUCE, Suit.CLUBS);
	Card deuceOfHearts = new Card(Rank.DEUCE, Suit.HEARTS);
	Card threeOfHearts= new Card(Rank.THREE, Suit.HEARTS);

	class Hand {
		// Subdeck clubs = new Subdeck(), diamonds = new Subdeck(), hearts = new
		// Subdeck(), spades = new Subdeck();
		Subdeck[] suits = new Subdeck[4];

		Subdeck getSuit(Suit suit) {
			return suits[suit.ordinal()];
		}

		int size() {
			int ncards = 0;
			//Suit st;
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

		/*
		 * currentLeader - card that is the current leader of a trick
		 */
		Card highestCardInTrick(Trick trick) {
			int i = 0;
			Suit st=null;
			//final int nPlayers = 4;
			Card leadingCard = null;
			/*
			 * enumerate the trick, and get highest
			 * TODO: integrate with code in CardGame
			 *   this should only get done once...
			 */
			for (Card c : trick.subdeck) {
				// the actual player who won the trick is n players away from the leader or the
				// leader
				if (i == 0) {
					leadingCard = c;
					st = c.suit;
				} else if (c.suit == st &&
						CardGame.isHigher(c, leadingCard)) { // true if c is higher than leadingcard
					//if (bDebugCardCf)
						//gameErrorLog("->T");
					leadingCard = c;
					//currentTrick.winner = (i + currentTrick.leader) % nPlayers;
				} else {
					//if (bDebugCardCf)
						//gameErrorLog("->F");
					// card was sloughed
				}
				i++;
			}	
			return leadingCard;
		}
		
		/*
		 * duckUnderSpade - different because I don't care if I take a spade trick
		 *  (usually). 
		 *  And if I have the queen I want to avoid playing it unless someone has
		 *  a higher card on the trick already.
		 *  (or if the only cards left to be played are the AK
		 *  ...
		 *  ... already determined if I can slough...
			// the whole point of this is to avoid the queen
			// so this algorithm will return the queen unless
			// it's pulled from my myCards and only return it
			// as a last resort
		 */
		Card duckUnderSpade(Trick trick) {
			Card cardToDuck=highestCardInTrick(trick);
			Suit st=cardToDuck.suit;
			Subdeck myCards=hand.getSuit(st);
			// if I have the queen and it's my only spade,
			// just admit defeat.
			boolean iHaveTheQueen = myCards.find(queenOfSpades);
			if (iHaveTheQueen) {
				if (trick.cardPlayed(aceOfSpades) ||
						trick.cardPlayed(kingOfSpades))
					return queenOfSpades;
				if (hand.getspades().size() == 1) {
					// snark: Oh crap. Can't be helped.
					return queenOfSpades;
				}
			} else if (trick.cardPlayed(queenOfSpades)) {
				return duckUnder(trick);				
			}
			// TODO: might do something else if I'm moonshooting
			//  or want to be the moonpolice
			return duckUnder(trick);				

			/*
			// Great find another spade.
			// can I get under?
			for (Card c : myCards)
				// get highest card below lead
				if (CardGame.isHigher(c, cardToDuck))// i.e. 2c,Ac -> F 
					if (candidate == null)
						candidate = c;
					else 
						candidate = candidate.higherCard(c);
			
			if (candidate == null) { // couldn't find one
				candidate = hand.lowest(st);
				if (ct.subdeck.size() == 3) // if not, am I last?
					candidate = hand.highest(st);
			}
			// otherwise take with highest (unless moonshooting)
			if (candidate == null)
				candidate = lastResort;
			return candidate;
			*/
		}

		/*
		 * duckUnder(Trick trick) - do everthing possible to duck the trick
		 *  if can't be ducked either be lowest or if inevitable take with highest
		 *  return null only if no cards of the suit in hand 
		 *  
		 *  duckunder(trick) - avoid taking this trick if possible
		 *   if not possible take with the highest card in the suit.
		 *   special case for spades/queen of spades handled separately
		 */		
		Card duckUnder(Trick ct) {
			Card cardToDuck=highestCardInTrick(ct);
			Card candidate=duckUnder(cardToDuck);
			if (candidate == null) { // couldn't find one
				candidate = hand.lowest(cardToDuck.suit);
				// otherwise take with highest (unless moonshooting)
				if (ct.subdeck.size() == 3) // if not, am I last?
					candidate = hand.highest(cardToDuck.suit);
			}
			return candidate;
		}

		/*
		 * duckUnder - if possible, return a card under the one given
		 *  null if there aren't any.
		 */
		Card duckUnder(Card cardToDuck) {
			//Card cardToDuck=highestCardInTrick(ct);
			Suit st=cardToDuck.suit;
			Subdeck myCards=hand.getSuit(st);
			Card candidate=null;

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
		
		Card commandLead() {
			return null;
		}
		
		/*
		 * countHigherCards - count the number of cards higher than this that
		 *  haven't been played that aren't in your hand
		 */
		int countHigherCards(Card c) {
			int n=0;
			Suit suit=c.suit;
			for (Rank rank : Rank.values()) {
				Card c2=new Card(rank, suit);
				if (CardGame.isHigher(c2,c) &&
						!hand.find(c2) &&
						!batteryBrain.isPlayed(c2))
					n++;
			}
			return n;
		}
		
		/*
		 * duckLead - card in hand with most cards above it not in  hand
		 *  or null if there aren't any...
		 */
		Card duckLead() {
			// best chance to lose lead is the card with the most
			// outstanding cards higher than yours that you don't have
			Card candidate=null;
			int highestSoFar=0;
			for (int i=Suit.first().ordinal(); i <= Suit.last().ordinal(); i++) {
				Card lc=hand.lowest(Suit.value(i));
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
		 * highestCardUnder - return highest card in hand below card in game's rank order
		 *   return null if no such card
		 *    isHigher(c1,c2) t if c1 > c2
		 *   code suspect... Not fully tested..
		 */
		Card highestCardUnder(Card card) {
			Subdeck sd=getSuit(card.suit);
			Card bestSoFar=null;
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
		 * lowest -- lowest card in suit
		 * ... new
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
		/*
		 * return the highest non suit in the hand,
		 * if possible...
		 */
		Card highestNon(Suit st, Card cToAvoid) {
			Subdeck sd = suits[st.ordinal()];
			Card highestSoFar = null;
			for (Card c : sd)
				if (highestSoFar == null) {
					if (c.rank != cToAvoid.rank)
						highestSoFar = c;
				}
				else
					if (c.rank != cToAvoid.rank)
						highestSoFar = highestSoFar.higherCard(c);

			if (highestSoFar == null && sd.find(cToAvoid))
				return cToAvoid;	// can't be helped...
			return highestSoFar;
		}

		void populate(Subdeck sd) {
			for (Card c : sd.subdeck) {
				suits[c.suit.ordinal()].add(c);
			}
			String type;
			if (sd.size() == 13)
				type = "dealt";
			else if (sd.size() == 3)
				type = "passed";
			else
				type = "huh?";
			System.out.println("Brain:" + type + "[" + sd.encode() +"]");
		} // populate

		Card bestSlough() {
			if (getspades().find(queenOfSpades))
				return queenOfSpades;
			if (hand.find(kingOfSpades))
				return kingOfSpades;
			if (hand.find(aceOfSpades))
				return aceOfSpades;
			if (gethearts().size() > 0)
				return gethearts().peek();
			// TODO: Highest card in hand?
			// ... guess
			return toxicCard();
		}

		/*
		 * TODO: get the 1-per-lifetime cases of 13 Hearts, or QS+12 Hearts
		 */
		Card bestSlough(int trick) {
			if (trick != 0)
				return bestSlough();
			//
			// determine the best slough on the
			// first trick...
			// TODO: if you have the QS don't slough a spade
			// if you don't, slough the A or K if you have them
			// routine: bestNonQSSlough();
			//
			// Ok, this is sloughing on the first trick
			// Can/Should I slough AK or S? Only if I don't have the Q
			boolean saveSpades = hand.find(queenOfSpades);
			if (!saveSpades && hand.find(kingOfSpades))
				return kingOfSpades;
			if (!saveSpades && hand.find(aceOfSpades))
				return aceOfSpades;
			for (int i = Suit.first().ordinal(); i<Suit.size(); i++) {
				Suit s = Suit.value(i);
				if (s == Suit.SPADES || s == Suit.HEARTS)
					continue;
				if (!voidIn(s)) {
					Card c = highest(s);	// Moonshooting?
				}
			}
			// if we have made it this far we only have Spades
			// and point cards... (Still first trick...)
			// Ok... I have only spades and hearts?
			// Crikey! how many spades do I have?
			System.out.println("Crikey! Take a picture:");
			brainDump(true);

			if (hand.getspades().size() >= 5)
				return hand.lowest(Suit.SPADES);	// de facto MoonShoot
			else if (hand.getspades().size() > 1){
				// Rats. I have to get rid of a useful backer
				Card highest=hand.highestNon(Suit.SPADES, queenOfSpades);
				return highest;
			} else { // All hearts and the queenofspades
				// make sure
				int ptcards = 0;
				Card nonPtCard=null;
				int i=0;
				for (Suit st = Suit.first(); i < Suit.size(); st = st.next(), i++) {
					if (st == Suit.HEARTS)
						ptcards++;
					else if (st == Suit.SPADES) {
						Subdeck spades=hand.getspades();
						for (Card c: spades)
							if (c.equals(queenOfSpades))
								ptcards++;
							else {
								nonPtCard = c;
							}
					} else {
						for (Card c: hand.getSuit(st)) {
							ptcards ++;
							nonPtCard = c;
							}
					}
				}
				if (ptcards != 13 || nonPtCard == null) {
					System.out.println("Can't happen: first-card search problem.");
					return randomNonPointCard();
				} else if (nonPtCard != null) {
					return nonPtCard;
				} else { 
					return hand.lowest(Suit.HEARTS); // moon...
				}
			}
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

		Card toxicCard() {
			if (find(queenOfSpades))
				return queenOfSpades;
			// TODO:
			// return AC or KC only if don't have QC
			// hmmm on the lead only...
			if (getspades().size() > 0) {
				if (hand.find(kingOfSpades))
					return kingOfSpades;
				if (hand.find(aceOfSpades))
					return aceOfSpades;
			}
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
			Suit suit = Suit.value(iSuit);
			Subdeck sd = null;
			// don't return a suit with no cards in it, if we can help it.
			for (int i = 0; i < 4; i++) {
				sd = suits[suit.ordinal()];
				if (sd.size() == 0)
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
			Card c = randomCard();
			for (int i=0; i<50; c = randomCard(),i++) {
				if (c.suit == Suit.HEARTS)
					continue;
				if (c.equals(queenOfSpades))
					continue;
				break;
			}
			return c;
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
		// Hand should be generic for all routines, not Heart Specific...
		Card bestLead(Card presumptiveSpade) {
			// leading spades if possible is best idea I have right now...
			// So I won't play the presumptive spade if
			//  .. I have the queen
			//  ... unless I'm running everyone out of spades
			Card candidate=null;
			if (shootMoonOrDumpPoints()) {	// shoot moon!
				// don't think about shooting the moon till the queen is out...
				if (someoneElseHasQueen() && presumptiveSpade != null)
					return presumptiveSpade;
				if (someoneElseHasQueen())
					candidate = duckLead();
				else
					candidate = commandLead();
			}
			if (candidate == null) {
				do {
					candidate = hand.randomCard();
				} while (candidate.suit == Suit.HEARTS && 
						!heartsBroken()) ;
			}
			return candidate;
		}

		/*
			if (presumptiveSpade != null && !hand.find(queenOfSpades))
				return presumptiveSpade;	// simple; draw out the queen
			if (presumptiveSpade != null && hand.find(queenOfSpades)) {
				// ok, not so simple
				// do I have the A & K? Otherwise able to shoot moon?
				// are most of the spades out??
				// Can I run everyone else out of spades?
				// What's the best suit-candidate to slough
				// Am I void in anything now?
				int spadesOutThere=remainingSpades();
				int spadesInHand=hand.count(queenOfSpades.suit);
				if (spadesOutThere == 0) {
					// got it!
					// lead something low
					;
					
				} else if (spadesInHand > spadesOutThere) {
					// if there is a spade lower than the queen play it
					// otherwise play the K or A
				}
			}
			if (getspades().size() > 0)
				return getspades().subdeck.peek();
				
			if (globalHeartsBroken && remainingCardsIn(Suit.HEARTS) > 0) {
				//
				// don't play a card no-one else has
				if (hand.find(threeOfHearts))	// if you have both play 3 first...
					return threeOfHearts;
				if (hand.find(deuceOfHearts))
					return deuceOfHearts;
			}
			/*
			 * lead lowest card in shortest suit, maybe? until I can do some analysis...
			 */
			// should I claim the rest?
			//}

			// May as well claim the rest
			// snark guess I have the rest...
			//  return hand.randomCard();
			/*
			if (getclubs().subdeck.size() > 0)
				return getclubs().subdeck.peek();
			if (getdiamonds().subdeck.size() > 0)
				return getdiamonds().subdeck.peek();
			/*
			 * TODO:
			 * leading the 2H could be good, if hearts broken
			 *
			if (gethearts().subdeck.size() > 0)
				return gethearts().subdeck.peek();
			return null;
			*/
		//}
		
		// maybe...
		// not used in this form...
	/*
	 *
	 // strategy functions...
		boolean willRunTable() {
			boolean bRunPossible = false;
			//Suit suit=null;
			for (Suit st : Suit.suits) 
				if (hand.getSuit(st).size() > 0 && 
						remainingCardsIn(st) > 0) { 
					bRunPossible = true;	// i.e. you have all of suit st
					//suit = st;
					}
			return false;
		} */
		
		/*
		 * canLoseTrick if there is a card that I could play that 
		 *  could be taken by someone else
		 *  
		 *  return the lead loser that I'm longest in...
		 *   i.e. fewest number of cards I have to lose to shoot moon...
		 */
		Card leadLoser() {
			for (Suit st : Suit.suits) {
				Subdeck cards=hand.getSuit(st);
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
		 * true if I should shoot the moon
		 *  false if I should dump points
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

			// this should be where moonPolice mode starts
			// maybe set a moonPolice mode?
			/*
			 * if (moonPossibleForSomeone()) return bDumpPoints; else return bDumpPoints;
			 */

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
		}	// add
		
		void deleteCard(Card c) {
			Subdeck sd = hand.getSuit(c.suit);

			//Subdeck sd = suits[c.suit.ordinal()];
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
