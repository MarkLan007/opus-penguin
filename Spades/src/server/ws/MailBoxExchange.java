package server.ws;

public class MailBoxExchange {
	public enum PassType {
		PassHold, PassLeft, PassRight, PassAcross;
	}
	public class MailBox {
		int from, to;	// to is redundant; stored by place
		int messagesize=0;
		boolean empty=true;
		Subdeck contents=null;
	}
	//
	// maintained by route
	boolean full=false;	
	boolean isfull() { return full; }
	int exchangeSize;
	MailBox exchange[]=null;
	
	/*
	 * MailBoxExchange - create an exchange with nplayers mailboxes
	 *  msize is for error checking the size of the subeck at runtime
	 */
	MailBoxExchange(int nplayers, int msize) {
		exchangeSize = nplayers;
		exchange = new MailBox[nplayers];
		int i;
		for (i=0; i<nplayers; i++) {
			exchange[i] = new MailBox();
			exchange[i].to = i;
		}
	}
	void setRouting(PassType passType) {
		int i;
		MailBox mb;
		switch (passType) {
		case PassHold:
			System.out.println("MailBox:setRouting: Routine shouldn't be set for a hold-hand.");			
			break;
		case PassLeft:
			int right;
			for (i=0, right=exchangeSize-1; i<exchangeSize; i++) {
				mb = exchange[i];
				mb.from = right;
				right = (right + 1) % exchangeSize;
			}
			break;
		case PassRight:
			int left;
			for (i=0,left=exchangeSize-1; i<exchangeSize; i++) {
				mb = exchange[i];
				mb.from = left;
				left = (left + 1) % exchangeSize;
			}
			break;
		case PassAcross:
			int across;
			for (i=0, across=exchangeSize/2; i <exchangeSize; i++) {
				mb = exchange[i];
				mb.from = across;
				across = (across + 1) % exchangeSize;				
			}
			break;
		default:	// Can't happen
			System.out.println("MailBox:setRouting: Can't Happen.");			
		}
		
	}

	void route(int to, Subdeck contents) {
		MailBox mb;
		mb = exchange[to];
		mb.contents = contents;
		mb.empty = false;
	}
	
	Subdeck retrieve(int to) {
		if (to >= exchangeSize || to < 0) {
			System.out.println("MailBox:retrieve: Range error:Can't Happen.");
			return null;
		}
		return exchange[to].contents;
	}
}
