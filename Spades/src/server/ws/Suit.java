package server.ws;

//import server.ws.MailBoxExchange.PassType;

//package cardGame;

public enum Suit {
	CLUBS, DIAMONDS, HEARTS, SPADES;			// Bridge order; Ascending alphabetical...

	static Suit[] suits = Suit.values();
	static public Suit first() { return CLUBS; }
	static public Suit last() { return SPADES; }
	public Suit next() {
		int index = ordinal();
		int next = (index + 1) % suits.length;
		return suits[next];
	}

	public Suit prev() {
		int index = ordinal();
		if (index == 0)
			index = suits.length;
		return suits[--index];
	}
	
	static public int size() { return suits.length; }
	//
	static public Suit value(int index) { 
		return Suit.values()[index%suits.length]; 
		}
}
/*
 * 	public enum PassType {
		PassHold, PassLeft, PassRight, PassAcross;
			static PassType[] pts = PassType.values();

		public PassType next() {
			int index = ordinal();
			int next = (index + 1) % pts.length;
			return pts[next];
		}

		public PassType prev() {
			int index = ordinal();
			if (index == 0)
				index = pts.length;
			return pts[--index];
		}

 */
