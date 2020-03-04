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
		this.handButton = null;
		this.shortName = "";
	};
	
}

var theDeck = null;
var deckInitialized=false;
function initializeTheDeck() {
	if (theDeck != null)
		return;
	theDeck = new Array();
	deckInitialized = true;
	var cardindex=0;
	for (var suit in Suit) {
		for (var rank in Rank) {
			var card;
			//console.log("creating:"+rank+suit);
			card = new Card(rank, suit, cardindex);
			card.shortName = "" + rank + " " + suit;
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

/*
 * decodeCard - determine a card in the deck by rank and string
 *  i.e. this is used when reading server protocol messages and 
 *  placing cards in the hand, deleting them, etc.
 *  returns the card it finds or null
 */
var cardRanks={"A":0, 
			"1": 0,
			"2": 1,
			"3": 2,
			"4": 3,
			"5": 4,
			"6": 5,
			"7": 6,
			"8": 7,
			"9": 8,
			"0": 9,
			"T": 9,
			"J":10, 
			"Q":11,
			"K":12,
			};
var cardSuits={
		"C": 0,
		"D": 1,
		"H": 2,
		"S": 3,
		};

function decodeCard(sRank, sSuit) {
	// decode rank
	var rank=0;
	var suit=0;
	if (sRank in cardRanks)
		rank=cardRanks[sRank];
	else 
		return null;
	if (sSuit in cardSuits) 
		suit=cardSuits[sSuit];
	else
		return null;
	var cardIndex=suit*13+rank;
	return theDeck[cardIndex];
}

// From the HTML page

// show the players hand
var handWindow=null;
const maxCardsInHand=52;
function cardSelected(event) {
	var t = event.target;
	var uniqueId = t.type + t.id;
	var special = t.textContent;
	console.log("Whoa!" + uniqueId+ "->" + t.textContent);
	alert("Whoa Nellie! in called with" + t.id + special);
}

function cardSelected2(event) {
	var t = event.target;
	var uniqueId = t.type + t.id;
	console.log("Double Whoa!" + uniqueid);
	alert("Whoa Nellie! a doubleclick was seen in:" + t.id);
}

/*
 * Array code... Temporary. If it works, fold into creating theDeck
 * 
 */

/*
 * wsHandInit - (was wsShowHand) initialize hand details including
 *  creating buttons for each card to be added to a hand
 */
function wsHandInit() {
	var i=0;
	//alert(handWindow.location.href);
		var controlDiv=document.getElementById("CardsInHandDiv");
		// now in main window...
		//var controlDiv=document.getElementById("CardsInHandDiv");
		var cardBtn;
		initializeTheDeck(); // if not done already
		for (i=0; i<maxCardsInHand; i++) {
			// only way to do cardBtn = new Button();
			cardBtn = document.createElement("Button");
			if (cardBtn == null) {
				alert("ButtonCreate failed on attempt=" + i);
				console.log("ButtonCreate failed on attempt=" + i);
			}
			/*
			 * foreach card in the deck place a handButton to make visible later
			 */
		    //theDeck[i].handButton = cardBtn;
		    var card = theDeck[i];
		    card.handButton = cardBtn;

	       //Set the attributes on the button
	       cardBtn.setAttribute("id", "CardButton" + i);
	       cardBtn.setAttribute("type","button");
	       cardBtn.setAttribute("value","Search");
	       cardBtn.innerText = card.shortName;
	       cardBtn.setAttribute("name","label" + i);
	       // failed tries...
	       // cardBtn.setAttribute("data-arg1", "foobar");
	       // cardBtn.setAttribute("textContent", "AceClubs");

	       cardBtn.style.height = "0";
	       cardBtn.style.width = "0";
	       cardBtn.style.visibility = "hidden";
	       /*
	        * Bug: For no apparent reason adding the event listeners with setAttribute doesn't work.
	        * I have no idea why. But addEventListener does work.
	        */
	       cardBtn.addEventListener("click", cardSelected);
	       cardBtn.addEventListener("dblclick", cardSelected2);
	       cardBtn.setAttribute("data-arg1", "User-Button"+ i);
	       //cardBtn.style.marginLeft = "20px";
	       //cardBtn.style.marginTop = "20px";
	       
	       //Add the button to the div holding cards	       
	       controlDiv.appendChild(cardBtn);
		}
	
}
/*
 * Old code... when show hand brought up its own window
 */
function wsShowHand2() {
	var i=0;
	handWindow=document.window;
	handWindow=open("HandCanvas.html", "example", "alwaysRaised=on,width=500,height=400");
	handWindow.document.title="Player's Hand";
	
	//handWindow.focus();
	//alert(handWindow.location.href);
	handWindow.onload = 
		function() { 
		/* let 
		html='<dev style="font-size:30px">Welcome!</div>';
		handWindow.document.body.insertAdjacentHTML('afterbegin', html);
		*/
	       //Create the search button
		//var controlDiv=handWindow.document.getElementById("CardsInHandDiv");
		// now in main window...
		//var controlDiv=document.getElementById("CardsInHandDiv");
		var cardBtn;
		initializeTheDeck(); // if not done already
		for (i=0; i<maxCardsInHand; i++) {
			// only way to do cardBtn = new Button();
			cardBtn = handWindow.document.createElement("Button");
			if (cardBtn == null) {
				alert("ButtonCreate failed on attempt=" + i);
				console.log("ButtonCreate failed on attempt=" + i);
			}
			// buttonList.push(cardBtn);
			/*
			 * foreach card in the deck place a handButton to make visible later
			 */
		    //theDeck[i].handButton = cardBtn;
		    var card = theDeck[i];
		    card.handButton = cardBtn;

	       //Set the attributes on the button
	       cardBtn.setAttribute("id", "CardButton" + i);
	       cardBtn.setAttribute("type","button");
	       cardBtn.setAttribute("value","Search");
	       cardBtn.innerText = "A1"; // for now
	       cardBtn.innerText = card.shortName;
	       cardBtn.setAttribute("name","label" + i);
	       // failed tries...
	       // cardBtn.setAttribute("data-arg1", "foobar");
	       // cardBtn.setAttribute("textContent", "AceClubs");

	       cardBtn.style.height = "0";
	       cardBtn.style.width = "0";
	       cardBtn.style.visibility = "hidden";
	       /*
	        * Bug: For no apparent reason adding the event listeners with setAttribute doesn't work.
	        * I have no idea why. But addEventListener does work.
	        */
	       cardBtn.addEventListener("click", cardSelected);
	       cardBtn.addEventListener("dblclick", cardSelected2);
	       cardBtn.setAttribute("data-arg1", "User-Button"+ i);
	       //cardBtn.style.marginLeft = "20px";
	       //cardBtn.style.marginTop = "20px";
	       
	       //Add the button to the div holding cards	       
	       controlDiv.appendChild(cardBtn);
		}
		/* stop doing this now that grab works...
		 * for (i=10; i<maxCardsInHand; i++) {
			//cardBtn = handWindow.document.getElementById("CardButton" + i);
			cardBtn = buttonList[i];
		    cardBtn.style.visibility = "visible";
		} */
		}; // lambda function
	
}

/*
 * make card (by cardindex) visible in hand
 */
function addCardToHand(cardindex) {
	//var cardBtn = buttonList[cardindex];
	//cardBtn.style.visibility = "visible";
	var card=theDeck[cardindex];
	if (card == null) {
		console.log("Error: Somehow can't find cardindex=" + cradindex);
		return false;
	}
	if (card.handButton == null) {
		console.log("Error: Somehow can't find handbutton, cardindex=" + cardindex);
		return false;
	}
	card.handButton.style.visibility = "visible";
    card.handButton.style.height = "75px";
    card.handButton.style.width = "54px";
	return true;
	//card.cardBtn.style.visibility = "visible";
}
/*
 * deleteCardFromHand - just set to to be not visible
 */
function deleteCardFromHand(cardindex) {
	//var cardBtn = buttonList[cardindex];
	//cardBtn.style.visibility = "visible";
	var card=theDeck[cardindex];
	if (card == null) {
		console.log("Error: Somehow can't find cardindex=" + cradindex);
		return false;
	}
	if (card.handButton == null) {
		console.log("Error: Somehow can't find handbutton, cardindex=" + cardindex);
		return false;
	}
	card.handButton.style.visibility = "hidden";
    //card.handButton.style.height = "75px";
    //card.handButton.style.width = "54px";
	return true;
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

/*
 * wsInitFelt - initialize the canvas in the game window
 */
var canvasWidth=500;
var canvasHeight=500;
function wsFeltInit() {
	initializeTheDeck();

	/*
	feltWindow.onload = 
		function() {
		*/  
		// var html='<dev style="font-size:30px">Welcome!</div>';
		// feltWindow.document.body.insertAdjacentHTML('afterbegin', html);
		feltCanvas = document.getElementById("Canvas1");
		if (feltCanvas && feltCanvas.getContext) {
			feltCanvas.width = canvasWidth;
			feltCanvas.height = canvasHeight;
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
				//...suitcardImages = document.getElementById("ClubsImage");
				//...clubsCardImages = suitcardImages; // first one... clean this up...
				clubsCardImages = document.getElementById("ClubsImage");
				diamondsCardImages = document.getElementById("DiamondsImage");
				heartsCardImages = document.getElementById("HeartsImage");
				spadesCardImages = document.getElementById("SpadesImage");
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
		/* }; */
	
}

function wsShowFelt() {
	feltWindow=open("FeltCanvas.html", "example", "width=600,height=800");
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

function getNewCardImage() {
	var img = new Image();
	var card = theDeck[1]; // 2 of clubs for now...
	// use clubs... clubsCardImage
	var idata = clubsCardImage;
	idata = clubsCardImage.getImageData
}
/*
 * turnover 1 card game-state machine (for debugging) and
 * routines for table4 and table6 placement (and clear, eventually)
 */

var serializeDeck=true;
var fakeRandom=0;
function jrandom(between0andXminus1) {
	if (serializeDeck) {
		var t=fakeRandom;
		fakeRandom ++;
		if (fakeRandom >= between0andXminus1)
			fakeRandom = 0;
		return t;
	}
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

var pi=Math.PI;

function getCardImageFile(card) {
	var x=card.suit;
	switch (card.suit) {
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
 * rotate through positions; state is currentSeatToPlay
 */
/*
 * game state machine 
 */
var currentSeatToPlay=0; //badly named; should be cardSeat or something like that
var indexCheck=true;
var nTableSize=4;

function setTableSize(size) {
	switch(size) {
	case 4:
	case 6:
		nTableSize=size;
		return true;
	default:
		console.log("Can't set table size to ", size);
	}
	return false;
}

/*
 * clearCardTable - clear card table and reset trick to zero...
 */
function clearCardTable(bResetTrick) {
	var feltContext = feltCanvas.getContext("2d");
	feltContext.clearRect(0, 0, feltCanvas.width, feltCanvas.height);
	feltCanvas = document.getElementById("Canvas1");
	if (feltCanvas && feltCanvas.getContext) {
		feltCanvas.width = canvasWidth;
		feltCanvas.height = canvasHeight;
		feltContext = feltCanvas.getContext("2d");
		if (feltContext) {
			feltContext.fillStyle = "ForestGreen";
			feltContext.strokeStyle = "blue";
			feltContext.lineWidth = 5;
			var cardWidth=200, cardHeight=500;
			// what the hell is this crap?
			for (let i=0; i<52; i++) {
				feltContext.fillRect(i*cardWidth,0,cardWidth,cardHeight);
				feltContext.strokeRect(i*cardWidth,0,cardWidth, cardHeight);	
				
			}
		}

	if (bResetTrick)
		currentSeatToPlay = 0;
	}
}

function turnover1card() { //Bug: currently ignores random card and retries it in 1-4 and 1-6...
	var randomcard=jrandom(52);	// pick a card, any card.
	var card=theDeck[randomcard];
	if (indexCheck == true)
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
	console.log("Card:" + card.cardIndex);
	// Next, switch on the table size...
	switch (nTableSize) {
	case 4:
		turnover1card4(card, currentSeatToPlay);
		break;
	case 6:
		console.log("Recently implemented 6...");
		turnover1card6(card, currentSeatToPlay);
		break;
	default:
		alert("Unknown table size"+nTableSize);
		return;	// do not pass go, or change currentSeatToPlay;
	}

	currentSeatToPlay = (currentSeatToPlay + 1) % nTableSize;
}

function turnover1card4(card, position) { // New rotation
	// var randomcard=jrandom(52);	// pick a card, any card.
	// var card=theDeck[randomcard];
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

	console.log("currentSeatToPlay Seat=" + position);
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
	var rotation=(90/180) * pi;	// rotation in radians for 90 degrees
	switch (position) {
	case 0:	// North-center
		feltContext.drawImage(card.cardImage, 
			firstx, firsty, cardwidth, cardheight, // source rectangle
			halfwidth-halfcardwidth, 0 , cardwidth, cardheight		// destination rectangle
			);
		break;
	case 1: // East half cardwith shifted left, half screen down
		// genius use of 90-degree rotation
		var upperLeftX=halfwidth+halfcardwidth+xmarginwidth;
		var upperLeftY=topy;
		feltContext.translate(halfwidth, halfheight);
		feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				-cardwidth, -cardheight , // not 0 after rotation...
					cardwidth, cardheight		// destination rectangle
				);
		// Undo rotation and translation for next seat
		feltContext.rotate(-rotation);
		feltContext.translate(-halfwidth, -halfheight);
		break;
	case 2:	// South rotated 180 upside down.
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth-halfcardwidth, halfheight, cardwidth, cardheight		// destination rectangle
				);
		break;
	case 3:	// West
		// genius use of 90-degree rotation
		var upperLeftX=halfwidth+halfcardwidth+xmarginwidth;
		var upperLeftY=topy;
		feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth-halfcardwidth, -cardheight , // not 0 after rotation...
					cardwidth, cardheight		// destination rectangle
				);
		feltContext.rotate(-rotation);
		break;
		default:
			console.log("Switch: can't happen.");
	}
	
}

function turnover1card6(card, position) { // New rotation
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

	console.log("currentSeatToPlay Seat=" + position);
	console.log("xy-source["+firstx+", "+firsty+"]");
	console.log("width-height["+cardwidth+", " + cardheight + "]");

	if (card.cardImage == null) {
		card.cardImage = getCardImageFile(card);
	}
	waitForImageLoad();
	//
	// 6handed now
	var halfwidth=Math.floor(feltCanvas.width/2);
	var halfheight=Math.floor(feltCanvas.height/2); 
	var halfxmarginwidth=Math.floor(xmarginwidth/2);
	var halfcardwidth=Math.floor(cardwidth/2);
	
	// calculation for position should place position 4 at the top, so...
	position = (position + 3) % 6;
	// rotate board 60-degrees times the position, splat card, rotate back
	var rotation=((position*60)/180) * pi;	// rotation in radians for 90 degrees
		feltContext.translate(halfwidth, cardheight);
		feltContext.rotate(rotation);
	
	feltContext.drawImage(card.cardImage, 
			firstx, firsty, cardwidth, cardheight, // source rectangle
			0, 0, // not 0 after rotation...
				cardwidth, cardheight		// destination rectangle
			);
	// Undo rotation and translation for next seat
		feltContext.rotate(-rotation);
		feltContext.translate(-(halfwidth), -cardheight);
}

// backup - turnover1card6 do not modify...
function bktv1c6trnover1crd6(card, position) { // New rotation
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

	console.log("currentSeatToPlay Seat=" + position);
	console.log("xy-source["+firstx+", "+firsty+"]");
	console.log("width-height["+cardwidth+", " + cardheight + "]");

	if (card.cardImage == null) {
		card.cardImage = getCardImageFile(card);
	}
	waitForImageLoad();
	//
	// 6handed now
	var halfwidth=Math.floor(feltCanvas.width/2);
	var halfheight=Math.floor(feltCanvas.height/2); 
	var halfxmarginwidth=Math.floor(xmarginwidth/2);
	var halfcardwidth=Math.floor(cardwidth/2);
	
	// rotate board 60-degrees times the position, splat card, rotate back
	var rotation=((position*60)/180) * pi;	// rotation in radians for 90 degrees
		feltContext.translate(halfwidth-halfcardwidth, cardheight);
		feltContext.rotate(rotation);
	
	feltContext.drawImage(card.cardImage, 
			firstx, firsty, cardwidth, cardheight, // source rectangle
			0, 0, // not 0 after rotation...
				cardwidth, cardheight		// destination rectangle
			);
	// Undo rotation and translation for next seat
		feltContext.rotate(-rotation);
		feltContext.translate(-(halfwidth-halfcardwidth), -cardheight);

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

	console.log("currentSeatToPlay Seat=" + currentSeatToPlay);
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
	switch (currentSeatToPlay) {
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
	
	currentSeatToPlay = (currentSeatToPlay + 1) % nTableSize;
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

/*
 * put a message right above the user's cards in his had
 */
function gamestatusUpdate(sMsg) {
	document.getElementById("gamestatusArea").textContent 
	= sMsg ;
	
}

function writeToTextArea(msg) {
	appendTextToTextArea("call deprecated:" + msg);
	/*
	var echoText = document.getElementById("echoText");
	echoText += msg;
	*/
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
	setReconnectDisabled(true);
	xstatusUpdate("Connected...");
}
function wsCloseConnection(){
	webSocket.close();
}
function wsReconnect() {
	openWebSocket();
}
/*
 * wsGetMessage - the on message received code
 */
function wsGetMessage(message){
	var s="" + message.data + "";

	echoText.value += "server>" + s + "\n";
	if (isProtocol(s)) {
		// hand off to protocol manager
		processCardString(s);	
	}
	return s;
}
// from protocalMessage.java
/*
 * Messages are of the form
 	[CommandChar][PlayerIDorX][Zero or more RS pairs representing Cards]%[MessageString]
 Player->G	 	
 	= Play Card
 	~ Pass Card(S)
 	Q Query
 	S Text (TBD)
 G->Player
 	+ Card(s)
 	- Card(s)
 	? Your Turn? [Cards in current trick] <change to ?>
 	& Trick Update. Cards(s) in the current trick [Player gets one of these every time someone plays]. 
 		// [Cards]%trick flags [Hwwxxyy[.;] [Hh] ww=winner;xx=trickid yy=lead or taker] [,.]
 	! Trick Cleared 
 	B Broken Suit (i.e. hearts, spades) [Could probably just put this in the trick update...]
 	$ %Player.score;Player.score;
 	% Player error %Message that can be given to human user i.e.
 	[11 core key messages]
 	
 	ERROR Text messages that should be supported
 	%!%Not your turn!
 	%2%Must play 2C
 	%N%Hearts/Spades are not broken
 */
//todo: implement the full set...
var protocolMessageTypes={
	'+': true,	// add cards
	'-': true,	// delete cards
	'?': true,	// your move
	'&': true,	// trick update
	'!': true,	// trick cleared
	'B': true,	// Broken suit
	'$': true,	// player scores
	'%': true,	// player error
};

/*
 * Messages of the form
 * +Cards
 * -Card
 */
function isProtocol(msg) {
	if (msg.charAt(0) in protocolMessageTypes) {
		return true;	
	}
	return false;
}
/*
 * pcs processCardString -- bizarre bug...
 */
function processCardString(cardString) {
	var card=null;
	var bDelete=false;
	var cUser;
	var i=0;
	var c0=cardString.charAt(0);
	switch(c0) {
	case "-":
		bDelete=true;
	case '+':
		// char 1 is the user id. Ignore for now
		// var cardString = s;
		for (i=2; i<cardString.length; i+=2) {
			card = decodeCard(cardString.charAt(i),
					cardString.charAt(i+1));
			if (card != null) {// yikes; null check if something bad happened
				console.log("Adding:" + card.cardIndex);
				if (bDelete)
					deleteCardFromHand(card.cardIndex);
				else
					addCardToHand(card.cardIndex);
			}
		}
		break;
	case '?':
		// tell user: your move xxx
		cUser = cardString.charAt(1);
		console.log("? Not yet implementeded");
		gamestatusUpdate("Your move! seat<"+cUser+">");
		break;
	case '&':	// 
		console.log("Trickupdate under construction");
		i=2;
		card = decodeCard(cardString.charAt(i),
				cardString.charAt(i+1));
		var user=parseInt(cardString.charAt(1), 10);
		switch (nTableSize) {
		case 4:
			turnover1card4(card, user);
			break;
		case 6:
			console.log("Recently implemented 6...");
			turnover1card6(card, user);
			break;
		default:
			alert("Unknown table size"+nTableSize);
			return;	// do not pass go, or change currentSeatToPlay;
		}

		break;
	case '%':
		console.log("%error:" + cardString);
		gamestatusUpdate("error:" + cUser);

		// put on screen above cards... xxx
		break;
	default:
		console.log("unimplemented protocol msg:" 
				+ c0 
				+ " not implemented");
			break;
	}
	
}

/*
 * setReconnectDisabled - disable the reconnect button
 */
function setReconnectDisabled(bDisabled) {
	var button = document.getElementById("reconnectButton");
	//button.style.visibility = "visible";
	button.disabled = bDisabled;
}

/* never tested...
function setReconnectInactive() {
	var button = document.getElementById("reconnectButton");
	//button.style.visibility = "hidden";
	button.disabled = true;
}
*/

function wsClose(message){
	//echoText.value += "Disconnect ... \n";
	setReconnectDisabled(false);
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
	/*
	 * Head off local commands
	 */
	if (processLocalCommand(line)) {	// i.e. a .table=, .clear, etc.
		;
	} else {	// otherwise send to the server
		console.log("full-line:" + line);
		// add to the scroll box...
		appendTextToTextArea("local:" + line + '\n'); // temporary...
		//
		if (serverWrite(line) == false)
			return;		// i.e. exit before clearing text box
	}
	//
	// clear text box And set the focus to the text area... 
	document.getElementById("msgText").value = "";
	document.getElementById("msgText").focus();
}

/*
 * processLocalCommand - process a typed-in line locally 
 */
function processLocalCommand(line) {
	// var commandStatus=false;	// assume failure...
	
	 // pick off lines beginning with .
	if (line.startsWith(".")) // as soon as you know .Something you will eat and process the line
		 ;
	else
		return false;

	if (line.includes("table=")) { // .table={4,6}
		// extract number from string
        var matches = line.match(/(\d+)/); // if a number at all...       
        if (matches) { 
        	var text = matches[0];
        	// not yet... xstatusUpdate("Setting table param=" + n);
        	// parseInt(text,10); not needed??
        	var n=parseInt(text,10);
        	if (setTableSize(n)) {
            	xstatusUpdate("Setting table param=" + n);
        	} else {
        		xstatusUpdate("Invalid table parameter. ignored");   	
        	}
        } 
	} else if (line.includes("grab=")) {
        var matches = line.match(/(\d+)/); // if a number at all...       
        if (matches) { 
        	var text = matches[0];
        	// parseInt(text,10); not needed??
        	var n=parseInt(text,10);
        	if (n >= 0 && n < 52 && addCardToHand(n)) {
            	xstatusUpdate("grabbing card cardindex=" + n);
        	} else {
        		xstatusUpdate("Invalid grab parameter. ignored");   	
        	}
        }
	} else if (line.includes("status=")) {
		// from the 2nd char after the = to the end
		gamestatusUpdate(line.slice(8)); 
		/*
        var matches = line.match(/(\d+)/); // if a number at all...       
        if (matches) { 
        	var text = matches[0];
        	// parseInt(text,10); not needed??
        	var n=parseInt(text,10);
        	if (n >= 0 && n < 52 && addCardToHand(n)) {
            	xstatusUpdate("grabbing card cardindex=" + n);
        	} else {
        		xstatusUpdate("Invalid grab parameter. ignored");   	
        	}
        }
        */
	} else if (line.includes("clear")) {
    	clearCardTable(true);
    	xstatusUpdate("Table Cleared and Reset.");
    } else {
		xstatusUpdate("Unrecognized command. ignored");   	
	} 
	
	return true;	// if I've made it this far, it's at least .something
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
