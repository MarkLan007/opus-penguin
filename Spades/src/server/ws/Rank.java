package server.ws;

//package cardGame;

public enum Rank {
	ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;
	
	static Rank[] ranks = Rank.values();
	static public Rank first() { return ACE; }
	static public Rank last() { return KING; }
	public Rank next() {
		int index = ordinal();
		int next = (index + 1) % ranks.length;
		return ranks[next];
	}

	public Rank prev() {
		int index = ordinal();
		if (index == 0)
			index = ranks.length;
		return ranks[--index];
	}
	
	static public int size() { return ranks.length; }
	//
	static public Suit value(int index) { 
		return Suit.values()[index%ranks.length]; 
		}

}
