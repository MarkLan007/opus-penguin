/**
 * Moving SocketUtils and NewCardUtils into one file
 * to avoid the module import/export bugs
 */
/** 
 * CardUtils - card data structures and utility functions
 */

'use strict'; 
const minorVersion ="1b" ;
const versionString1="OneUtils.js version 0." + 
	minorVersion +
	"(prerelease) [do not use .mjs experimental version.]";
//console.warn(VersionString);	// Moved to GameConsole.html
console.warn(versionString1);
console.log("Loading OneUtils version 1.0" + minorVersion + "...");

const Rank = {
		ACE: 	1,
		DEUCE: 	2,
		THREE: 	3,
		FOUR: 	4,
		FIVE: 	5,
		SIX:	6,
		SEVEN:	7,
		EIGHT:	8,
		NINE:	9,
		TEN:	10,
		JACK:	11,
		QUEEN:	12,
		KING:	13		
};

const CLUBS="CLUBS";
const DIAMONDS="DIAMONDS";
const HEARTS="HEARTS";
const SPADES="SPADES";

const Suit = {
		CLUBS:		1,
		DIAMONDS:	2,
		HEARTS:		3,
		SPADES:		4
};

//
// table positions at cardtable
const Orientation4 = {
		NORTH:	0,
		EAST:	1,
		SOUTH:	2,
		WEST:	3,
}

const Orientation6 = {
		NOON: 0,
		TENMINUTES: 1,
		TWENTYMINUTES: 2,
		THIRTYMINUTES: 3,
		FORTYMINUTES: 4,
		FIFTYMINUTES: 5
}

class Card {
	//var cardindex;
	//var rank;
	//var suit;
	//var suitimage
	// var xoffset, yoffset
	// var width, height
	constructor(r, s, cardindex) {
		this.rank = r;
		this.suit = s;
		// this should be computed something like this, but I'm too dumb to make this work right now
		//this.cardIndex = (s.value - 1) * 4 + (r.value - 1);
		this.cardIndex = cardindex;
		this.suitImage = null; // put suitimage in when actually displayed
	};
	
}

var theDeck = new Array();

function initializeTheDeck() {
	var cardindex=0;
	for (var suit in Suit) {
		for (var rank in Rank) {
			var card;
			//console.log("creating:"+rank+suit);
			card = new Card(rank, suit, cardindex);
			theDeck.push(card);
			cardindex ++;
		}
	}
	// Add card images for each card
	// ... not here. In constructor
	var verboseInit=false;
	for (var i=0; i<theDeck.length; i++) {
		if (verboseInit)
			console.log("created: " + theDeck[i].rank + theDeck[i].Suit.name);
	}
}

// From the HTML page

// show the players hand
var handWindow=null;
function wsShowHand() {
	handWindow=open("about:blank", "example", "width=300,height=300");
	handWindow.document.title="Player's Hand";
	handWindow.focus();
	//alert(handWindow.location.href);
	handWindow.onload = 
		function() { let 
		html='<dev style="font-size:30px">Welcome!</div>';
		handWindow.document.body.insertAdjacentHTML('afterbegin', html);
		};
	
}
// show the game felt board
var feltWindow=null;
var feltCanvas=null;
var suitcardImages=null;
var clubsCardImages=null;
var diamondsCardImages=null;
var heartsCardImages=null;
var spadesCardImages=null;
var feltContext=null;

//
// constants pertaining to the particular .jpg files loaded
const cardwidth=180;		// shared constants...
const cardheight=250;
const xmarginwidth=23;
const ymarginwidth=16;

function wsShowFelt() {
	feltWindow=open("FeltCanvas.html", "example", "width=600,height=900");
	feltWindow.document.title="Game Felt";
	feltWindow.focus();
	//alert(handWindow.location.href);
	
	// Initialize card deck
	initializeTheDeck();

	feltWindow.onload = 
		function() {  
		var html='<dev style="font-size:30px">Welcome!</div>';
		feltWindow.document.body.insertAdjacentHTML('afterbegin', html);
		feltCanvas = feltWindow.document.getElementById("Canvas1");
		if (feltCanvas && feltCanvas.getContext) {
			feltCanvas.width = 600;
			feltCanvas.height = 600;
			feltContext = feltCanvas.getContext("2d");
			if (feltContext) {
				feltContext.fillStyle = "ForestGreen";
				feltContext.strokeStyle = "blue";
				feltContext.lineWidth = 5;
				var cardWidth=200, cardHeight=500;
				for (let i=0; i<52; i++) {
					feltContext.fillRect(i*cardWidth,0,cardWidth,cardHeight);
					feltContext.strokeRect(i*cardWidth,0,cardWidth, cardHeight);	
					
				}
				// Cardwidth=200 cardheight=500

				// Now get the card images loaded on the html page...
				// xxx
				// "ClubsImage" 
				// first put the image on the canvas...
				// then see if you can take the individual cards out of the image
				//  and save them in the card...
				//
				// There doesn't seem to be a way to create subimages.
				// Maybe I should use the suitcard image and pass coordinates for the
				// particular card being accessed. So pass offsets...
				//
				// Ok, so should use deferred processing for this.
				// store cardimage as null.
				// when needed put in a reference to the actual image
				suitcardImages = feltWindow.document.getElementById("ClubsImage");
				clubsCardImages = suitcardImages; // first one... clean this up...
				clubsCardImages = feltWindow.document.getElementById("ClubsImage");
				diamondsCardImages = feltWindow.document.getElementById("DiamondsImage");
				heartsCardImages = feltWindow.document.getElementById("HeartsImage");
				spadesCardImages = feltWindow.document.getElementById("SpadesImage");
				suitcardImages = diamondsCardImages;	// temp hack for testing...
				if (suitcardImages == null) 
					alert("Failed to obtain card image file");
				else {
					// just draw the first card for now...
					/*
					var width=suitcardImages.width;
					var height=suitcardImages.height;
					console.log("width:" + suitcardImages.width + "->" + cardwidth);					
					console.log("height:" + suitcardImages.height + "->" + cardheight);
					
					feltContext.drawImage(suitcardImages, 
							0, 0, cardwidth, cardheight, // source rectangle
							0, 0, cardwidth, cardheight	// destination rectangle
							);
						*/	
					}
				
			}
		}
		};
	
}

// turnover1card
var fakeRandom=0;
function jrandom(between0andXminus1) {
	//return fakeRandom++;
	var j = Math.random() * between0andXminus1;
	//if (j < 1) return 1;
	return Math.floor(j);
}

// cards are in a 5x3 grid in the .jpg file; result is zero indexed
//return x-coordinate of serial number (integer) card in pixels
function cardXoffset(card) {
	// determine the column card is in
	var cardInCol=Math.floor(card%5);
	// now determine the number of pixels + border to offset in x direction
	var pixels=cardInCol * cardwidth + cardInCol * xmarginwidth;
	return pixels;
}

//return y-coordinate of serial number (integer) card in pixels
function cardYoffset(card) {
	var cardInRow=Math.floor(card / 5);
	var pixels=(cardInRow * cardheight) + (cardInRow * ymarginwidth);
	return pixels;
}

/*
 * turnover1card -- backup version; works for one card
 */
var pi=Math.PI;
var currentCard=0; //badly named; should be cardSeat or something like that
var nHands=4;
function turnover1card() {
	var randomcard=jrandom(52);	// pick a card, any card.
	var card=theDeck[randomcard];
	if (indexCheck == true)
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
	console.log("Card:" + card.cardIndex);
	feltContext = feltCanvas.getContext("2d");
	var lastx, lasty;
	// All cards are packed into their files the same way, so 
	// location is based (symmetrically) on the card's rank.
	// Also note that Arrays are 0-based, and the offsets are 1-based
	var firstx=cardXoffset(Rank[card.rank] - 1);
	var firsty=cardYoffset(Rank[card.rank] - 1);

	console.log("currentCard Seat=" + currentCard);
	console.log("xy-source["+firstx+", "+firsty+"]");
	console.log("width-height["+cardwidth+", " + cardheight + "]");

	if (card.cardImage == null) {
		card.cardImage = getCardImageFile(card);
	}
	waitForImageLoad();
	//
	// Assume 4handed for now
	var halfwidth=Math.floor(feltCanvas.width/2);
	var halfheight=Math.floor(feltCanvas.height/2); 
	var halfxmarginwidth=Math.floor(xmarginwidth/2);
	var halfcardwidth=Math.floor(cardwidth/2)
	//var halfymarginheight=Math.floor(xmarginheight/2);
	var topy=0;
	if (halfheight > cardheight)
		topy = cardheight;
	else
		topy = halfheight;
	var rotation=0;
	switch (currentCard) {
	case 0:	// North-center
		feltContext.drawImage(card.cardImage, 
			firstx, firsty, cardwidth, cardheight, // source rectangle
			halfwidth-halfcardwidth, 0 , cardwidth, cardheight		// destination rectangle
			);
		break;
	case 1: // East half cardwith shifted left, half screen down
		rotation = pi/2;
		//feltContext.translate(halfwidth, halfheight);
		//feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth+halfcardwidth+xmarginwidth, topy, cardwidth, cardheight		// destination rectangle
				);
		//feltContext.rotate(-rotation);
		//feltContext.translate(-halfwidth, -halfheight);
		break;
	case 2:	// South rotated 180 upside down.
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth-halfcardwidth, halfheight, cardwidth, cardheight		// destination rectangle
				);
		break;
	case 3:	// West
		rotation = (45/180)*pi;
		//feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				0, topy, cardwidth, cardheight		// destination rectangle
				);
		//feltContext.rotate(-rotation);
		break;
		default:
			console.log("Switch: can't happen.");
	}
	
	currentCard = (currentCard + 1) % nHands;
}

/*
 * turnover1card - rebuilt turnover1card using cards not just the face
 * xxx
 */
var indexCheck=true;

function getCardImageFile(card) {
	var x=card.suit;
	switch (card.suit) {	// xxx
	case CLUBS:
		if (clubsCardImages == null)
			clubsCardImages = feltWindow.document.getElementById("ClubsImage");
		return clubsCardImages;
	case DIAMONDS:
		if (diamondsCardImages == null)
			diamondsCardImages = feltWindow.document.getElementById("DiamondsImage");
		return diamondsCardImages;
	case HEARTS:
		if (heartsCardImages == null)
			heartsCardImages = feltWindow.document.getElementById("HeartsImage");
		return heartsCardImages;
	case SPADES:
		if (spadesCardImages == null)
			spadesCardImages = feltWindow.document.getElementById("SpadesImage");
		return spadesCardImages;
	default:
		return suitcardImages;
	}
}
/*
 * cardPos is 0, NORTH at top, clockwise to number of players
 * xxx
 */
function turnover1cardOld() {
	var randomcard=jrandom(52);	// pick a card, any card.
	var card=theDeck[randomcard];
	if (indexCheck == true)
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
	console.log("Card:" + card.cardIndex);
	feltContext = feltCanvas.getContext("2d");
	var lastx, lasty;
	// All cards are packed into their files the same way, so 
	// location is based (symmetrically) on the card's rank.
	// Also note that Arrays are 0-based, and the offsets are 1-based
	var firstx=cardXoffset(Rank[card.rank] - 1);
	var firsty=cardYoffset(Rank[card.rank] - 1);

	console.log("currentCard Seat=" + currentCard);
	console.log("xy-source["+firstx+", "+firsty+"]");
	console.log("width-height["+cardwidth+", " + cardheight + "]");

	if (card.cardImage == null) {
		card.cardImage = getCardImageFile(card);
	}
	waitForImageLoad();
	//
	// Assume 4handed for now
	var halfwidth=Math.floor(feltCanvas.width/2);
	var halfheight=Math.floor(feltCanvas.height/2); 
	var halfxmarginwidth=Math.floor(xmarginwidth/2);
	var halfcardwidth=Math.floor(cardwidth/2)
	//var halfymarginheight=Math.floor(xmarginheight/2);
	var topy=0;
	if (halfheight > cardheight)
		topy = cardheight;
	else
		topy = halfheight;
	var rotation=0;
	switch (currentCard) {
	case 0:	// North-center
		feltContext.drawImage(card.cardImage, 
			firstx, firsty, cardwidth, cardheight, // source rectangle
			halfwidth-halfcardwidth, 0 , cardwidth, cardheight		// destination rectangle
			);
		break;
	case 1: // East half cardwith shifted left, half screen down
		rotation = pi/2;
		//feltContext.translate(halfwidth, halfheight);
		//feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth+halfcardwidth+xmarginwidth, topy, cardwidth, cardheight		// destination rectangle
				);
		//feltContext.rotate(-rotation);
		//feltContext.translate(-halfwidth, -halfheight);
		break;
	case 2:	// South rotated 180 upside down.
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth-halfcardwidth, halfheight, cardwidth, cardheight		// destination rectangle
				);
		break;
	case 3:	// West
		rotation = (45/180)*pi;
		//feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				0, topy, cardwidth, cardheight		// destination rectangle
				);
		//feltContext.rotate(-rotation);
		break;
		default:
			console.log("Switch: can't happen.");
	}
	
	currentCard = (currentCard + 1) % nHands;
}

var imageFilesLoaded=0;	// number of image files loaded
function logCardImage() {
	//alert("Image loaded?");
	imageFilesLoaded++;
	console.log("image files loaded=" + imageFilesLoaded);	
}

function waitForImageLoad() {
	var sleeps=10;
	var i;
	for (i=0; i<sleeps; i++) {
		if (imageFilesLoaded <4)
			return;
		sleep(100);
		}
}


/**
 * 
 */
function initSocketUtils() {
	alert("initSocket: stub");
	console.log("initSocke: stub");
}

const sServiceName = "Spades";
var webSocket = null;

var debugXStatus=false;
function xstatusUpdate(sMsg) { 
	if (debugXStatus)
		alert(sMsg);
	// Note: bizarre soul-sucking bug if you try to user InnerHTMl at least on UNIX browsers...
	document.getElementById("statusArea").textContent 
		= sMsg ; // this is the only of these that works reliably
		// = '<var>' + sMsg + '</var>' ;
		// insertAdjacentText("afterbegin", sMsg);
		// innterHTML =  sMsg ;
	return true;
}

function writeToTextArea(msg) {
	var echoText = document.getElementById("echoText");
	echoText += msg;
}

/*
 * filter keys to look for <enter>
 */
var verboseGUIIO=false;
function keyFilter(event) {
	if (verboseGUIIO == true) {
		console.log('<key=' + event.keyCode + '>');
	}
	if (event.keyCode == 13 ) {
		console.log("return seen. Process!");
		var line=getMsgText();
		xstatusUpdate("[return]Sending{" + line + "} to server...");
		processSubmitAndClearMsgText();
	}
}

function selectButtonPress(event) {
	if (verboseGUIIO == true) {
		console.log('yea!js:Button Pressed');
	}
	//event.preventDefault();
	console.warn("js:Button Pressed");
	if (event == null) return;
	//alert("Button Pressed...");
	var line=getMsgText();
	xstatusUpdate("[submit]Sending{" + line + "} to server...");
	processSubmitAndClearMsgText();
	//var s = document.getElementById("msgText").value;
	//console.warn("js: textvalue =" + s);
	//appendTextToTextArea(s);
	//xstatusUpdate("[ButtonPress]Sending to server...");
}
function wsOpen(message){
	//echoText.value += "Connected ... \n";
	xstatusUpdate("Connected...");
}
function wsCloseConnection(){
	webSocket.close();
}
function wsGetMessage(message){
	echoText.value += "server>" + message.data + "\n";
}
function wsClose(message){
	//echoText.value += "Disconnect ... \n";
	xstatusUpdate("Disconnected..."); 
}
// obsolete: wserror -- superceded by xstatusUpdate
function wserror(message){
	echoText.value += "Error ... \n";
}

function openWebSocket() {
	webSocket = new WebSocket("ws://localhost:8080/" +
				sServiceName +
				"/websocketendpoint");
	// todo: something about an error...
	var message = document.getElementById("message");
	webSocket.onopen = function(message){ wsOpen(message);};
	webSocket.onmessage = function(message){ wsGetMessage(message);};
	webSocket.onclose = function(message){ wsClose(message);};
	webSocket.onerror = function(message){ wsError(message);};
}

// sleep for parameter ms milliseconds. 
function sleep(ms) {  
	  return new Promise(resolve => setTimeout(resolve, ms));
	}

//returns true on successful write; false on error
function serverWrite(msg){
	if (webSocket == null) {
		openWebSocket();
		sleep(10);
	}
	if (webSocket.readystate == 0) {
		console.log("Still connecting. Try again in a second.");
		return false;
	}
	else if (webSocket.readyState == 1) {	// Ready. Connection established
		webSocket.send(msg);
		return true;
	} else if (webSocket.readyState == 2 || webSocket.readyState == 3) {
		xstatusUpdate("Connection to server has been closed. Plese Reconnect.")
		return false;
	} else {
		console.log("Unknown status(" + webSocket.readystate + ") unable to write:" + msg);
		xstatusUpdate("unable to write:{"+msg+"} Network temporarily unavailable. Please try again.")
		return false;
	}
	return true;
}

//
// getMsgText - return the text in the text box line
function getMsgText() {
	var line = document.getElementById("msgText").value;
	return line;
}

//get text string, append, and clear box
//Handle <return> and submit button the same way
function processSubmitAndClearMsgText() {
	var line = getMsgText();
	if (line == null) {
		console.log("null msg box - ignored.");
		return;
		}
	console.log("full-line:" + line);
	// add to the scroll box...
	appendTextToTextArea("local:" + line + '\n'); // temporary...
	//
	if (serverWrite(line) == false)
		return;
	document.getElementById("msgText").value = "";
	//
	// And set the focus to the text area... only necessary when button is pressed...
	document.getElementById("msgText").focus();
}

function appendTextToTextArea(newtext) {
	var old = document.getElementById("statusArea").value;
	document.getElementById("statusArea").value = old + newtext;
}
		
/*
 * export/import at this time are failed features.
 * reverting to text/javascript and assuming macro-style importing
 *
export {openWebSocket, selectButtonPress, keyFilter };
// I don't know why import doesn't have a semicolon but export does. Go figure.
*/

// The functions called directly from HTML... Last...

function wsSendMessage1(){
	var line=getMsgText();
	xstatusUpdate("[submit-button]Sending{" + line + "} to server...");
	processSubmitAndClearMsgText();
	// console.log("Sending:" + message.value);
	//webSocket.send(message.value);
	//serverWrite(message.value);
	//echoText.value += "Message sent to the server : " + message.value + "\n";
	//message.value = "";
}

/*
 * menuDropdown
 */
function menuDropDown() {
	console.log("menuDropDown...");
	document.getElementById("item1-menu").classList.toggle("show");
}
function mouseExitDropDownMenu(event) {
	console.log("exit mouseExitDropDownMenu.../Do nothing right now...");
	console.log("arg=" + event);
	menuDropDown();	// toggle show value...
}
/* 
 * dropDown - When the user clicks on the button, 
 * toggle between hiding and showing the dropdown content 
 */
function dropDown() {
	console.log("myDropdown...");
	document.getElementById("myDropdown").classList.toggle("show");
}

function mouseExitMenu(event) {
	console.log("exit myDropdown.../Do nothing right now...");
	console.log("arg=" + event);
	dropDown();	// toggle show value...

}
console.log("OneUtils loaded [done].");
