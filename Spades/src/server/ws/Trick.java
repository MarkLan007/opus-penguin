package server.ws;

//package cardGame;

public class Trick {
	/*
	 * Trick Modifiers are game-specific flags on a trick that represent game specific things for the trick
	 *  the cards are part of the protocol message
	 */
	int trickid=0;
	int winner=-1;
	int leader=0; // 0-nPlayers who could have lead the trick or winner if closed
	boolean bHeartsBroken=false;
	boolean bClosed=false; // Is this trick the final one (closed) or just a table update for the user
	Subdeck subdeck=new Subdeck();
	
	Trick(int t) {
		trickid = t;
		}
	
	void breakHearts() {
		bHeartsBroken = true;
		}
	
	boolean heartsBroken() {
		return bHeartsBroken;		
		}
	
	/*
	 * true if card has been played on this trick false otherwise
	 *  -- note -- doesn't consider whether it's sloughed as opposed 
	 *  to played and viing for the lead.
	 */
	boolean cardPlayed(Card card) {
		for (Card c : subdeck.subdeck)
			if (c.equals(card))
				return true;
		return false;
	}
	
	String encode(boolean tVerbose) {
		if (!tVerbose)
			return encode();
		
			String s="";
			String hb="";
			// ! nn L W B [subdeck] for trick-id Leader Winner H.Broken [Cards+]
			String id="" + trickid;
			if (id.length() < 2)	id = '0' + id;
			if (bHeartsBroken) hb = "T"; else hb = "F";
			s = "Trick:" + id + " L:" + leader + " W:" + winner +  " HBroken:" + hb + "[" + subdeck.encode() + "]";
			return s;
			
		}
	String encode() {
		String s="";
		String hb="";
		// ! nn L W B [subdeck] for trick-id Leader Winner H.Broken [Cards+]
		String id="" + trickid;
		if (id.length() < 2)	id = '0' + id;
		if (bHeartsBroken) hb = "T"; else hb = "F";
		s = id + leader + winner +  hb + "[" + subdeck.encode() + "]";
		return s;
		}
	}

