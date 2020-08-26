package server.ws;

//package cardGame;
/*
 * new development starting now... Post RC1 fixes to stuff before
 * forking hearts game to hearts + spades, and mobile updates.
 */
class Card {
	int cardindex;
	Rank rank=Rank.ACE;		// Default Card AS
	Suit suit=Suit.SPADES;

	static final Card deuceOfClubs = new Card(Rank.DEUCE, Suit.CLUBS);
	//Card deuceOfClubs = new Card(Rank.DEUCE, Suit.CLUBS);
	static final Card aceOfSpades = new Card(Rank.ACE, Suit.SPADES);
	static final Card kingOfSpades = new Card(Rank.KING, Suit.SPADES);
	static final Card queenOfSpades = new Card(Rank.QUEEN, Suit.SPADES);
	static final Card deuceOfHearts = new Card(Rank.DEUCE, Suit.HEARTS);
	static final Card threeOfHearts = new Card(Rank.THREE, Suit.HEARTS);

	public Card(int iOrdinal) {
		int iCard = iOrdinal % 13;
		int iSuit = iOrdinal / 13;
		cardindex = iOrdinal;

		rank = Rank.values()[iCard];
		suit = Suit.values()[iSuit];
		}
	
	static Card cardBack() {
		Card c=new Card(0);
		c.cardindex = 52;
		return c;
		}
	
	public Card higherCard(Card c) {
		if (c.rank == Rank.ACE)
			return c;
		else if (rank == Rank.ACE)
			return this;
		if (c.rank.ordinal() > rank.ordinal())
			return c;
		return this;
	}

	// new... 6/8/20
	public Card lowerCard(Card c) {
		if (c.rank == Rank.ACE)
			return this;
		else if (rank == Rank.ACE)
			return c;
		if (c.rank.ordinal() < rank.ordinal())
			return c;
		return this;
	}

	private void computeCardIndex(Rank rk, Suit st) {
		int base = st.ordinal();
		int i = rk.ordinal();
		cardindex = (base * 13) + i;
		
		}
	
	public Card(Rank rk, Suit st) {
		rank = rk;
		suit = st;
		computeCardIndex(rk, st);
		}

	public boolean equals(Rank r, Suit st) {
		if (rank == r && suit == st)
			return true;
		return false;
		}

	public boolean equals(Card c) {
		if (c.rank == rank && c.suit == suit)
			return true;
		return false;
		}

	public int getindex() {
		return cardindex; 
		}
	
	/*
	 * These are the only places that the string values are referenced to encode and decode protocol messages
	 */
	static final String sRanks="A23456789TJQK";
	static final String sSuits="CDHS";
	
	/*
	 * turn a 2-character string like AS into Card with Rank Ace and Suit Spades
	 */
	public Card(String sEncoding) {
		int i;
		char cRank=sEncoding.charAt(0);
		char cSuit=sEncoding.charAt(1);
		cRank = Character.toUpperCase(cRank);
		cSuit = Character.toUpperCase(cSuit);
		int iRanksLen = sRanks.length();
		for (i=0; i<iRanksLen; i++)
			if (cRank == sRanks.charAt(i)) {
				rank = Rank.values()[i];
				break;
				}
		int iSuitsLen = sSuits.length();
		for (i=0; i<iSuitsLen; i++)
			if (cSuit == sSuits.charAt(i)) {
				suit = Suit.values()[i];
				break;
				}
		// Otherwise default card is AS, as initialized
		// TODO: throw hissy fit.
		computeCardIndex(rank, suit);
		}
	
	/*
	 * Encode a card into 2 characters for [Rank][Suit]
	 */
	String encode() {

		/*
		 * Get the ordinal and use as an index into sRanks and sSuits.
		 */
		int i=suit.ordinal();
		String sSuit=sSuits.substring(i, i+1);
		i = rank.ordinal();
		String sRank=sRanks.substring(i, i+1);
		return sRank + sSuit;
		/*
		 * Let him who is without sin, cast the first stone...
		 * how about suit.ordinal() as an index into SSuits...
		 */
		/*
		String s1="", s2="";
		switch (suit) {
		default:
		case CLUBS:
			//Suit.valueOf(suit);   //valueOf(arg0)
			//Suit.values()[suit];
			//sSuits.substring(, endIndex)
			s1 = "C";
			break;
		case DIAMONDS:
			s1 = "D";
			break;
		case HEARTS:
			s1="H";
			break;
		case SPADES:
			s1="S";
			break;
			}
		switch (rank) {
		default:
		case ACE:
			s2 = "A"; break;
		case DEUCE:
			s2 = "2"; break;
		case THREE:
			s2 = "3"; break;
		case FOUR:
			s2 = "4"; break;
		case FIVE:
			s2 = "5"; break;
		case SIX:
			s2 = "6"; break;
		case SEVEN:
			s2 = "7"; break;
		case EIGHT:
			s2 = "8"; break;
		case NINE:
			s2 = "9"; break;
		case TEN:
			s2 = "T"; break;
		case JACK:
			s2 = "J"; break;
		case QUEEN:
			s2 = "Q"; break;
		case KING:
			s2 = "K"; break;
			}
			
		return s2 + s1;
		*/
		}
	}
