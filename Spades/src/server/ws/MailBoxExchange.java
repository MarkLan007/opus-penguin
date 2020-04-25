package server.ws;

public class MailBoxExchange {
	public enum PassType {
		PassHold, PassLeft, PassRight, PassAcross;
	}
	static PassType[] pts = PassType.values();
	static public PassType getNextPass(PassType e)
	{
	  int index = e.ordinal();
	  int next = (index + 1) % pts.length;
	  return pts[next];
	}
	static public PassType getPrevPass(PassType e)
	{
	  int index = e.ordinal();
	  if (index == 0)
		  index = pts.length;
	  return pts[--index];
	}
	/*
	 * for now make this a hold hand first...
	 * TODO: should start with a PassLeft
	 */
	static public PassType first() { return PassType.PassLeft; }
	//
	
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
	MailBoxExchange(PassType pt, int nplayers, int msize) {
		exchangeSize = nplayers;
		exchange = new MailBox[nplayers];
		int i;
		for (i=0; i<nplayers; i++) {
			exchange[i] = new MailBox();
			exchange[i].to = i;
		}
		setRouting(pt);
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
				mb.to = i;
				mb.from = right;
				right = (right + 1) % exchangeSize;
			}
			break;
		case PassRight:
			int left;
			for (i=0,left=exchangeSize-1; i<exchangeSize; i++) {
				mb = exchange[i];
				mb.to = i;
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
	
	int getRecipient(int from) {
		int i;
		for (i=0; i<exchangeSize; i++) 
			if (exchange[i].from == from)
				return i;
		// Uh oh.
		System.out.println("Can't route for:" + from + ". recipient not found");
		return 0;
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


/* Slight Improvement to the cars example:
 * Sometimes you need the enums outside of the class
 * for example, as parameters. So How about this:
 * 
 * 1. Better if it's a class variable: 
 * i.e. static 
 * Then you can call it from outside the class like this 
 * 	Cars.nextCar(e);
 * 2. And also you don't have to create the array and 
 * initialize it with every call
 * 3. One line shorter...
*/
/*
static Cars[] cars = Cars.values();
static public Cars getNextCar(Cars e)
{
  int index = e.ordinal();
  int next = (index + 1) % cars.length;
  return cars[next];
}
*/
/*
 * And don't forget about prev
 * Here's a snappy version
 */
/*
static public PassType getPrevCar(PassType e)
{
  int index = e.ordinal();
  if (index == 0)
	  index = cars.length;
  return cars[--index];
}
*/