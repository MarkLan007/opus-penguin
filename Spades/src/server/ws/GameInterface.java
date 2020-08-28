package server.ws;

//package cardGame;

/*
 * These are the game callbacks that players (included RobotPlayer and HumanPlayer) call back to the game with
 *  -- RobotPlayer is synchronous
 *  -- HumanPlayer is asynchronous putting data through a socket
 *  
 *  TODO: why can't I set gameName???
 */
public interface GameInterface {
	String gameName="";			// Why can't I set this in CardGame?? A mystery...
	/*
	 * Once I get sockets implemented I can know who the player is from the socket it was received from (but what about robots?)
	 *  (How do robots know who they are if you don't tell them...)
	 */
	/*
	 * New interface prototypes for Spades
	 * ++
	 */
	void process(ProtocolMessage m);
	boolean start();
	String uniqueName(String fname);
	void setName(int pid, String sname);
	public String getGameType();
	void handOver();
	String nameOfTheGame();
	// generalize breaking suits...
	Suit getSuitThatMustBeBroken();
	//
	boolean isHigher(Card cfirst, Card csecond);
	// --
	void playCard(int fromPlayer, Card card);	// server tells client to delete card if successful; sends errormsg if not
	void passCards(int fromPlayer, Subdeck cards);
	void bidTricks(int fromPlayer, Subdeck subdeck);
	void query(int fromPlayer, String msg);
	void superuser(int fromPlayer, String msg);
	void disconnect(int pid);	// disconnect from game;
	int getCurrentTrickId();
	void declareMisdeal(int pid, String string);
	//
	// Spades additions
	int getBid(int nseat);
	void setBid(int n, int nseat);
	public int getTricks(int nseat);
	
}

