package server.ws;

//package cardGame;

//=~S+-?&!B$%>

public enum ProtocolMessageTypes {
	//
	// Client Messages i.e. messages sent from Player/Client to Game Server
	PLAY_CARD, // CARD
	PASS_CARD, // CARD*
	GAME_QUERY, // Q 
	SUPER_USER, // S Text
	//
	// Server Messages i.e. messages sent from Server/Player-client
	ADD_CARDS, // CARD +
	DELETE_CARDS, // CARD -
	YOUR_TURN, // !CARD* [cards in trick already played]
	CURRENT_TRICK, // &CARD [Really it's CURRENT_CARD. Only takes one card. The only message that encodes a trick is _CLEARED]
	TRICK_CLEARED,	// ! nn L W B [subdeck] for trick-id Leader Winner H.Broken [Cards+]
	BROKEN_SUIT, // B hearts/spades are broken
	PLAYER_SCORES, // $ Player-score, Player-score...
	PLAYER_ERROR, // %MSG Text Display immediately; {Please play 2c, Follow Suit, Hearts/Spades not broken, not-your-turn, don't-have-that-card, user-error}
	PLAYER_INFO,		// %INF Text Display later; player-info to be displayed when asked
	PLAYER_WELCOME;
	static public ProtocolMessageTypes first() { return PLAY_CARD; }
	static public ProtocolMessageTypes last() { return PLAYER_WELCOME; }
	}
/* Eleven Messages at the moment */


//T[trickid][lead][won][CARD]
//C[trickid][player][CARD]
//?[cardsPlayedInTrickUpToYou] -- if no cards, you are leading.
