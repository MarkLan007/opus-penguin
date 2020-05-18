/**
 * CardUtils - card data structures and utility functions
 */
'use strict';
/*
 * xyzzy... Naming: wsXXX - routines that involve web socket sending or
 * receiving messages ...DialogWindow -- dialog done the window way (TODO: move
 * to retired code) ...DialogDiv -- dialog done with divs (better.)
 */
const minorVersion = "1b";
const versionString1 = "OneUtils.js version 0." +
	minorVersion +
	"(prerelease) [do not use .mjs experimental version.]";
// console.warn(VersionString); // Moved to GameConsole.html
console.warn(versionString1);
console.log("Loading OneUtils version 1.0" + minorVersion + "...");

var bIsMobile = false; // assume desktop-like connection
var scrWidth = window.screen.width;
var scrHeight = window.screen.height;
var scrInnerWidth = window.innerWidth;
var clientWidth = 0;	// window-width without scrollbars, etc.
var clientHeight = 0;

/*
 * Mozilla: Note that not all of the width given by this property may be
 * available to the window itself. When other widgets occupy space that cannot
 * be used by the window object, there is a difference in window.screen.width
 * and window.screen.availWidth. See also screen.height.
 */
function detectConfig() {
	var bIsMobile = false; // initiate as false
	// device detection
	if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|ipad|iris|kindle|Android|Silk|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(navigator.userAgent) 
	    || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent.substr(0,4))) { 
		bIsMobile = true;
	}
	scrInnerWidth = window.innerWidth;

	return bIsMobile;
}

function setWide() {
	var buttonGridArray = document.getElementsByClassName("buttonGrid");
	for (var i=0; i<buttonGridArray.length; i++) {
		// ordinarily... grid-template-columns: repeat(5, 1fr);
		var bg=buttonGridArray[i];
		bg.style.gridTemplateColumns = "repeat(5, 1fr)";
	}
	//buttonGrid.style.gridTemplateColumns = "repeat(4, 1fr)";
}

function setNarrow() {
	var buttonGridArray = document.getElementsByClassName("buttonGrid");
	for (var i=0; i<buttonGridArray.length; i++) {
		// ordinarily... grid-template-columns: repeat(5, 1fr);
		var bg=buttonGridArray[i];
		bg.style.gridTemplateColumns = "repeat(4, 1fr)";
	}
	//buttonGrid.style.gridTemplateColumns = "repeat(4, 1fr)";
}

function narrowConfig() {
	if (scrWidth < 400) {
		setNarrow();
		return true;
	}
	return false;
}

// ++
function displayWindowSize(){
    // Get width and height of the window excluding scrollbars
    var w = document.documentElement.clientWidth;
    var h = document.documentElement.clientHeight;
    clientWidth = w;
    clientHeight = h;
	xstatusUpdate("config=" + "clientWidth:" + w +  ":" + scrInnerWidth  + " height:" + h);

	// keep handarea on the right...
	resizeHandArea(clientWidth, scrInnerWidth);
}
 
// Attaching the event listener function to window's resize event
window.addEventListener("resize", displayWindowSize);

// Calling the function for the first time
displayWindowSize();

// --
const Rank = {
	ACE: 1,
	DEUCE: 2,
	THREE: 3,
	FOUR: 4,
	FIVE: 5,
	SIX: 6,
	SEVEN: 7,
	EIGHT: 8,
	NINE: 9,
	TEN: 10,
	JACK: 11,
	QUEEN: 12,
	KING: 13
};

const CLUBS = "CLUBS";
const DIAMONDS = "DIAMONDS";
const HEARTS = "HEARTS";
const SPADES = "SPADES";

const Suit = {
	CLUBS: 1,
	DIAMONDS: 2,
	HEARTS: 3,
	SPADES: 4
};

//
// table positions at cardtable
const Orientation4 = {
	NORTH: 0,
	EAST: 1,
	SOUTH: 2,
	WEST: 3,
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
	// var cardindex;
	// var rank;
	// var suit;
	// var suitimage
	// var xoffset, yoffset
	// var width, height
	constructor(r, s, cardindex) {
		this.rank = r;
		this.suit = s;
		// this should be computed something like this, but I'm too dumb to make
		// this work right now
		// this.cardIndex = (s.value - 1) * 4 + (r.value - 1);
		this.cardIndex = cardindex;
		this.suitImage = null; // put suitimage in when actually displayed
		this.handButton = null;
		this.friendlyName = "";
		this.shortName = "";
	};

}

/*
 * These are the only places that the string values are referenced to encode and
 * decode protocol messages -- identical code with the server
 */
var sRanks = "A23456789TJQK";
var sSuits = "CDHS";

var theDeck = null;
var deckInitialized = false;
function initializeTheDeck() {
	if (theDeck != null)
		return;
	theDeck = new Array();
	deckInitialized = true;
	var cardindex = 0;
	var r = 0, s = 0;
	for (var suit in Suit) {
		r = 0;
		for (var rank in Rank) {
			var card;
			var sRank = "" + rank, sSuit = "" + suit;
			// console.log("creating:"+rank+suit);
			card = new Card(rank, suit, cardindex);
			card.friendlyName = sRank + sSuit;
			card.shortName = sRanks.charAt(r) + sSuits.charAt(s);
			theDeck.push(card);
			cardindex++;
			r++;
		}
		s++;
	}

	var verboseInit = false;
	if (verboseInit) {
		for (var i = 0; i < theDeck.length; i++) {
			console.log("created: " + theDeck[i].rank + theDeck[i].Suit.name);
		}
	}
}

/*
 * decodeCard - determine a card in the deck by rank and string i.e. this is
 * used when reading server protocol messages and placing cards in the hand,
 * deleting them, etc. returns the card it finds or null
 */
var cardRanks = {
	"A": 0,
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
	"J": 10,
	"Q": 11,
	"K": 12,
};
var cardSuits = {
	"C": 0,
	"D": 1,
	"H": 2,
	"S": 3,
};

function decodeCard(sRank, sSuit) {
	// decode rank
	var rank = 0;
	var suit = 0;
	if (sRank in cardRanks)
		rank = cardRanks[sRank];
	else
		return null;
	if (sSuit in cardSuits)
		suit = cardSuits[sSuit];
	else
		return null;
	var cardIndex = suit * 13 + rank;
	return theDeck[cardIndex];
}

// From the HTML page
function cardIndexFromButtonName(sname) {
	var digitString = sname.replace(/\D/g, "");
	var cardIndex = parseInt(digitString);
	return cardIndex;
}

// show the players hand
var handWindow = null;
const maxCardsInHand = 52;
function cardSelected(event) {
	var t = event.target;
	/*
	 * Mark the button as selected for the user by setting the opacity
	 *  .. only do this if in a pass.
	 *  .. the delete comes back quickly and unsetting this in case
	 *  of an error is too complicated right now...
	 */
	if (bPassingCardsInProgress)
		t.style.opacity = 0.5;
	var buttonName = event.target.id;
	//var uniqueId = t.type + t.id;
	//var special = t.textContent;
	// console.log("Whoa!" + uniqueId+ "->" + t.textContent);
	// alert("Whoa Nellie! in called with" + t.id + special);
	// var digitString = buttonName.replace(/\D/g, "");
	// var cardIndex = parseInt(digitString);
	var cardIndex=cardIndexFromButtonName(buttonName);
	//
	// if the pass dialog is up, pass it.
	// otherwise play it
	if (bPassingCardsInProgress)
		// addCardToPassDialogWindow(cardIndex);
		addCardToPassDialogDiv(cardIndex);
	else {
		sendCardFromButtonPress(cardIndex);
		contractHandArea();
	}
}

function cardSelected2(event) {
	var t = event.target;
	var uniqueId = t.type + t.id;
	console.log("Double Whoa!" + uniqueId);
	alert("Whoa Nellie! a doubleclick was seen in:" + t.id);
	// sendCardFromButtonPress(parseInt(t.id, 10));
}

/*
 * wsHandInit - (was wsShowHand) initialize hand details including creating
 * buttons for each card to be added to a hand
 */
function wsHandInit() {
	var i = 0;
	// alert(handWindow.location.href);
	// var controlDiv = document.getElementById("CardsInHandDiv");
	// now in main window...
	// var controlDiv=document.getElementById("CardsInHandDiv");
	var cardBtn;
	initializeTheDeck(); // if not done already
	for (i = 0; i < maxCardsInHand; i++) {
		// only way to do cardBtn = new Button();
		cardBtn = document.createElement("Button");
		if (cardBtn == null) {
			alert("ButtonCreate failed on attempt=" + i);
			console.log("ButtonCreate failed on attempt=" + i);
		}
		/*
		 * foreach card in the deck place a handButton to make visible later
		 */
		// theDeck[i].handButton = cardBtn;
		var card = theDeck[i];
		card.handButton = cardBtn;

		// Set the attributes on the button
		cardBtn.setAttribute("id", "CardButton" + i);
		cardBtn.setAttribute("type", "button");
		cardBtn.setAttribute("value", "Search");
		// with images, don't set innterText
		// cardBtn.innerText = card.friendlyName;
		cardBtn.setAttribute("name", "label" + i);
		// failed tries...
		// cardBtn.setAttribute("data-arg1", "foobar");
		// cardBtn.setAttribute("textContent", "AceClubs");

		cardBtn.style.height = "0";
		cardBtn.style.width = "0";
		cardBtn.style.visibility = "hidden";
		//
		// Button alignment in the hand?
		/*
		 * images on buttons
		 */
		cardBtn.style.alignItems = "left";
		/*
		 * construct name from shortname + _thumb.png
		 */
		var fname = '"thumblib/' + card.shortName + '_thumb.png"';
		// fname = '"buttonfacetest.png"';

		cardBtn.style.backgroundImage = 'url(' + fname + ')';
		cardBtn.style.backgroundRepeat = "no-repeat";
		/*
		 * Bug: For no apparent reason adding the event listeners with
		 * setAttribute doesn't work. I have no idea why. But addEventListener
		 * does work.
		 */
		cardBtn.addEventListener("click", cardSelected);
		cardBtn.addEventListener("dblclick", cardSelected2);
		cardBtn.setAttribute("data-arg1", "User-Button" + i);
		// cardBtn.style.marginLeft = "20px";
		// cardBtn.style.marginTop = "20px";

		// Add the button to the div holding cards
		// see below:
		// controlDiv.appendChild(cardBtn);
	}
	/*
	 * Place cards in suit-related div
	 */
	arrangeCardsInDivs();
	/*
	 * var controlDiv = document.getElementById("ClubsInHandDiv"); for (i = 0; i <
	 * 52; i++) { card = theDeck[i]; cardBtn = card.handButton; switch (i) {
	 * case 13: controlDiv = document.getElementById("DiamondsInHandDiv");
	 * break; case 26: controlDiv = document.getElementById("SpadesInHandDiv");
	 * break; case 39: controlDiv = document.getElementById("HeartsInHandDiv");
	 * break; } controlDiv.appendChild(cardBtn); }
	 */

}

/*
 * make card (by cardindex) visible in hand -- just sets the card to be visible
 * in the hand
 */
function addCardToHand(cardindex) {
	var card = theDeck[cardindex];
	if (card == null) {
		console.log("Error: Somehow can't find cardindex=" + cradindex);
		return false;
	}
	if (card.handButton == null) {
		console.log("Error: Somehow can't find handbutton, cardindex=" + cardindex);
		return false;
	}
	// ++
	// yea!
	card.handButton.className = "hoverBtn";
	// --
	card.handButton.style.opacity = 1.0; 
	card.handButton.style.visibility = "visible";
	if (narrowConfig()) {
		card.handButton.style.height = "75px";	// was 75px
		card.handButton.style.width = "54px";	// was 54px xxx
	}
	else {
		card.handButton.style.height = "150px";	// was 75px
		card.handButton.style.width = "100px";	// was 54px xxx
	}
	//
	// get the right div, and insert it.
	//
	var div = getSuitDiv(card);
	div.appendChild(card.handButton);

	return true;
	// card.cardBtn.style.visibility = "visible";
}

/*
 * deleteCardFromHand - just set to to be not visible
 */
function deleteCardFromHand(cardindex) {
	var card = theDeck[cardindex];
	if (card == null) {
		console.log("Error: Somehow can't find cardindex=" + cradindex);
		return false;
	}
	if (card.handButton == null) {
		console.log("Error: Somehow can't find handbutton, cardindex=" + cardindex);
		return false;
	}
	card.handButton.style.visibility = "hidden";
	// card.handButton.style.height = "75px";
	// card.handButton.style.width = "54px";
	return true;
}

function deleteAllCardsFromHand() {
	var i;
	for (i=0; i<theDeck.length; i++)
		deleteCardFromHand(i);
}

// show the game felt board
var feltWindow = null;
var feltCanvas = null;
var suitcardImages = null;
var clubsCardImages = null;
var diamondsCardImages = null;
var heartsCardImages = null;
var spadesCardImages = null;
var feltContext = null;

//
// constants pertaining to the particular .jpg files loaded
/*
 * Was: const cardwidth=180; // shared constants... const cardheight=250; const
 * xmarginwidth=23; const ymarginwidth=16;
 */
const cardwidth = 180.4;	// (180) 1pixel fix 4-16-20
const cardheight = 250.3;	// (250)
const xmarginwidth = 22.75;	// (23)
const ymarginwidth = 16.5;	// (16) 1pixel fix 4-16-20

/*
 * wsInitFelt - initialize the canvas in the game window
 */
var canvasWidth = 500;
var canvasHeight = 500;
function wsFeltInit() {
	initializeTheDeck();

	/*
	 * feltWindow.onload = function() {
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
			feltContext.fillRect(0, 0, canvasWidth, canvasHeight);

			var cardWidth = 200, cardHeight = 500;
			/*
			 * for (let i=0; i<52; i++) {
			 * feltContext.fillRect(i*cardWidth,0,cardWidth,cardHeight);
			 * feltContext.strokeRect(i*cardWidth,0,cardWidth, cardHeight); }
			 */
			// Cardwidth=200 cardheight=500

			// Now get the card images loaded on the html page...
			// "ClubsImage"
			// first put the image on the canvas...
			// then see if you can take the individual cards out of the
			// image
			// and save them in the card...
			//
			// There doesn't seem to be a way to create subimages.
			// Maybe I should use the suitcard image and pass coordinates
			// for the
			// particular card being accessed. So pass offsets...
			//
			// Ok, so should use deferred processing for this.
			// store cardimage as null.
			// when needed put in a reference to the actual image
			// ...suitcardImages = document.getElementById("ClubsImage");
			// ...clubsCardImages = suitcardImages; // first one... clean
			// this up...
			clubsCardImages = document.getElementById("ClubsImage");
			diamondsCardImages = document.getElementById("DiamondsImage");
			heartsCardImages = document.getElementById("HeartsImage");
			spadesCardImages = document.getElementById("SpadesImage");
			suitcardImages = diamondsCardImages;	// temp hack for
			// testing...
			if (suitcardImages == null)
				alert("Failed to obtain card image file");
			else {
				// just draw the first card for now...
				/*
				 * var width=suitcardImages.width; var
				 * height=suitcardImages.height; console.log("width:" +
				 * suitcardImages.width + "->" + cardwidth);
				 * console.log("height:" + suitcardImages.height + "->" +
				 * cardheight);
				 * 
				 * feltContext.drawImage(suitcardImages, 0, 0, cardwidth,
				 * cardheight, // source rectangle 0, 0, cardwidth, cardheight //
				 * destination rectangle );
				 */
			}

		}
	}
	/* }; */

}


function wsShowFelt() {
	feltWindow = open("FeltCanvas.html", "example", "width=600,height=800");
	feltWindow.document.title = "Game Felt";
	feltWindow.focus();
	// alert(handWindow.location.href);

	// Initialize card deck
	initializeTheDeck();

	feltWindow.onload =
		function() {
			var html = '<dev style="font-size:30px">Welcome!</div>';
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
					// splat the playing felt
					feltContext.fillRect(0, 0, canvasWidth, canvasHeight);
					var cardWidth = 200, cardHeight = 500;
					/*
					 * for (let i=0; i<52; i++) {
					 * feltContext.fillRect(i*cardWidth,0,cardWidth,cardHeight);
					 * feltContext.strokeRect(i*cardWidth,0,cardWidth,
					 * cardHeight); }
					 */
					// Cardwidth=200 cardheight=500

					// Now get the card images loaded on the html page...
					// "ClubsImage"
					// first put the image on the canvas...
					// then see if you can take the individual cards out of the
					// image
					// and save them in the card...
					//
					// There doesn't seem to be a way to create subimages.
					// Maybe I should use the suitcard image and pass
					// coordinates
					// for the
					// particular card being accessed. So pass offsets...
					//
					// Ok, so should use deferred processing for this.
					// store cardimage as null.
					// when needed put in a reference to the actual image
					suitcardImages = feltWindow.document.getElementById("ClubsImage");
					clubsCardImages = suitcardImages; // first one... clean
														// this
					// up...
					clubsCardImages = feltWindow.document.getElementById("ClubsImage");
					diamondsCardImages = feltWindow.document.getElementById("DiamondsImage");
					heartsCardImages = feltWindow.document.getElementById("HeartsImage");
					spadesCardImages = feltWindow.document.getElementById("SpadesImage");
					suitcardImages = diamondsCardImages;	// temp hack for
					// testing...
					if (suitcardImages == null)
						alert("Failed to obtain card image file");
					else {
						// just draw the first card for now...
						/*
						 * var width=suitcardImages.width; var
						 * height=suitcardImages.height; console.log("width:" +
						 * suitcardImages.width + "->" + cardwidth);
						 * console.log("height:" + suitcardImages.height + "->" +
						 * cardheight);
						 * 
						 * feltContext.drawImage(suitcardImages, 0, 0,
						 * cardwidth, cardheight, // source rectangle 0, 0,
						 * cardwidth, cardheight // destination rectangle );
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
 * turnover 1 card game-state machine (for debugging) and routines for table4
 * and table6 placement (and clear, eventually)
 */

var serializeDeck = true;
var fakeRandom = 0;
function jrandom(between0andXminus1) {
	if (serializeDeck) {
		var t = fakeRandom;
		fakeRandom++;
		if (fakeRandom >= between0andXminus1)
			fakeRandom = 0;
		return t;
	}
	// return fakeRandom++;
	var j = Math.random() * between0andXminus1;
	// if (j < 1) return 1;
	return Math.floor(j);
}

// cards are in a 5x3 grid in the .jpg file; result is zero indexed
// return x-coordinate of serial number (integer) card in pixels
function cardXoffset(card) {
	// determine the column card is in
	var cardInCol = Math.floor(card % 5);
	// now determine the number of pixels + border to offset in x direction
	var pixels = cardInCol * cardwidth + cardInCol * xmarginwidth;
	return pixels;
}

// return y-coordinate of serial number (integer) card in pixels
function cardYoffset(card) {
	var cardInRow = Math.floor(card / 5);
	var pixels = (cardInRow * cardheight) + (cardInRow * ymarginwidth);
	return pixels;
}

var pi = Math.PI;

function getCardImageFile(card) {
	var x = card.suit;
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
 * cardPos is 0, NORTH at top, clockwise to number of players rotate through
 * positions; state is currentSeatToPlay
 */
/*
 * game state machine
 */
var currentSeatToPlay = 0; // badly named; should be cardSeat or something like
// that
var indexCheck = false;	// debugging variable for console writes
var nTableSize = 4;

function setTableSize(size) {
	switch (size) {
		case 4:
		case 6:
			nTableSize = size;
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
			feltContext.fillRect(0, 0, canvasWidth, canvasHeight);
			var cardWidth = 200, cardHeight = 500;
			// what the hell is this crap?
			for (let i = 0; i < 52; i++) {
				feltContext.fillRect(i * cardWidth, 0, cardWidth, cardHeight);
				// feltContext.strokeRect(i*cardWidth,0,cardWidth, cardHeight);
			}
		}

		if (bResetTrick)
			currentSeatToPlay = 0;
	}
}

/*
 * testing functions attached to buttons for debug
 */
// generates consecutive cards to test transitions
function turnover1card() { 
	// in 1-4 and 1-6...
	var randomcard = jrandom(52);	// pick a card, any card.
	var card = theDeck[randomcard];
	if (indexCheck == true) {
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
		console.log("Card:" + card.cardIndex);
	}
	// Next, switch on the table size...
	switch (nTableSize) {
		case 4:
			turnover1card4(card, currentSeatToPlay);
			// break;
// alert("turnover1card whoa!");
			for (var i=0; i<nTableSize-1; i++) {
				randomcard = jrandom(52);
				card = theDeck[randomcard];
				currentSeatToPlay = (currentSeatToPlay + 1) % nTableSize;
				turnover1card4(card, currentSeatToPlay);
				}
			animator();
			break;
		case 6:
			// console.log("Recently implemented 6...");
			turnover1card6(card, currentSeatToPlay);
			break;
		default:
			alert("Unknown table size" + nTableSize);
			return;	// do not pass go, or change currentSeatToPlay;
	}

	currentSeatToPlay = (currentSeatToPlay + 1) % nTableSize;
}

// enqueue the currentTrick (trim to tablesize?)
// and start animator
function showClear() {
	if (currentTrick)
		qEnqueue(currentTrick);
	else
		xstatusUpdate("clear: no trick to clear");
	// clear the table
	// extract the winning card, and enqueue it
	// then place the card back on top of it
	var se=new SpecialEffect();
	se.setType(SpecialEffectType.CLEAR);

	qEnqueue(se);
	if (currentTrick) {
		var w=currentTrick.winningCard();
		qEnqueue(new CardAnimation(w));
	}
	/*
	 * to creat a cardback card, turnover1c4(null,position)
	 */
	var winner=currentTrick.getWinner();
	// turnover1card4(null, winner);
	turnover1card4(null, winner);
	animateMaybe();
	/*
	 * if (!qEmpty()) animator();
	 */
}

var isBusy = false;

function resolve(p1) { }
function reject(p1) { }
/*
 * Use this to try with promise...
 */
function drawFadeInImage(ctx,
	rotation, trX, trY,
	img,
	x, y, width, height,
	tgtx, tgty, tgtwidth, tgtheight) {

	// var img, // / current image to fade in
	var opacity = 0; // / current globalAlpha of canvas

	// / if we're in a fade exit until done

	// / fade in
	let promise = new Promise(function(resolve, reject) {
		var opacity = 0; // / current globalAlpha of canvas

		(function fadeIn() {
			// / set alpha
			ctx.globalAlpha = opacity;

			/*
			 * feltContext.translate(halfwidth, halfheight);
			 * feltContext.rotate(rotation);
			 */

			ctx.save();
			if (trX != 0 || trY != 0)
				ctx.translate(trX, trY);
			if (rotation != 0)
				ctx.rotate(rotation);
			// / draw image with current alpha
			ctx.drawImage(img, x, y, width, height,
				tgtx, tgty, tgtwidth, tgtheight);
			ctx.restore();
			// / increase alpha to 1, then exit resetting isBusy flag
			opacity += 0.02;	// was .02
			if (opacity < 1.0)
				requestAnimationFrame(fadeIn);
			else
				isBusy = false;
		})();
	});
}

/*
 * Ok. So old code should be 'gestault' new code will be scene... passed an
 * animationGestault
 */
function resolveWithClear() {
	clearCardTable(false); 
}
var fIncrement = .01;
function fadeOutTrick(gestault) {

	if (gestault == null)
		return;

	let promise = new Promise(function(resolveWithClear, reject) {
		var opacity = 0.0; // / current globalAlpha of canvas
		let ctx = feltContext;
		//
		// changes the opacity and calls paint in a loop
		(function fadeOut() {
			// set alpha
			// ctx.globalAlpha = opacity;
			gestault.paint(true, ctx, opacity);
			if (opacity <= 1.0) {
				opacity += fIncrement;	// was .02
				requestAnimationFrame(fadeOut);
			} else {
				isBusy = false;
				// gestault.paint(ctx, 0.0);
				// clearCardTable(false);
			}
			opacity += fIncrement;	// was .02
		})();
	});
}

// repaint...
function repaint(gestault) {

	// clearCardTable(true);
	if (gestault == null)
		return;

	let promise = new Promise(function(resolve, reject) {
		var opacity = 0.0; // / current globalAlpha of canvas
		let ctx = feltContext;
		//
		// fade in the entire trick so far...
		(function rp() {
			gestault.paint(false, ctx, opacity);
			if (opacity <= 1.0) {
				opacity += fIncrement;	// was .02
				requestAnimationFrame(rp);
			} else {
				isBusy = false;
				// gestault.paint(ctx, 0.0);
				// clearCardTable(false);
			}
			opacity += fIncrement;	// was .02
		})();
	});
}

// ok, so
/*
 * aaa turnover1card4 show card in position; if the card is null... turn over a
 * cardback in the position compute geometry, create a new animatable card and
 * enqueue it.
 */
var currentGestault = null;
var saveGestault = null;
var currentTrick=null;
var previousTrick=null;
/*
 * turnover1card4 - create a card animation, enqueue it; and add the
 * cardanimation to the trick for use when the trick clears
 */
function turnover1card4(card, position) { // New rotation
	
	if (currentTrick == null)
		currentTrick = new TrickAnimation();
	if (currentTrick.isClosed()) {
		previousTrick = currentTrick;
		currentTrick = new TrickAnimation();
	}
	
	// if there is no current gestault, create one;
	if (currentGestault == null)
		currentGestault = new AnimationGestault();
	if (indexCheck == true)
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
	// console.log("Card:" + card.cardIndex);
	feltContext = feltCanvas.getContext("2d");
	// All cards are packed into their files the same way, so
	// location is based (symmetrically) on the card's rank.
	// Also note that Arrays are 0-based, and the offsets are 1-based

	var firstx = 0;
	var firsty = 0;
	var imagefile = null;
	if (card == null) {	// (cardback) i.e. display a card back
		firstx = cardXoffset(14);
		firsty = cardYoffset(14);
		imagefile = suitcardImages;
		// use suitcardImages
	} else {
		firstx = cardXoffset(Rank[card.rank] - 1);
		firsty = cardYoffset(Rank[card.rank] - 1);
	}

	if (imagefile != null)
		;
	else if (card.cardImage == null) {
		card.cardImage = getCardImageFile(card);
		imagefile = card.cardImage;
	} else {
		imagefile = card.cardImage;
	}
	waitForImageLoad();
	//
	// 4handed geometry
	var halfwidth = Math.floor(feltCanvas.width / 2);
	var halfheight = Math.floor(feltCanvas.height / 2);
	var halfxmarginwidth = Math.floor(xmarginwidth / 2);
	var halfcardwidth = Math.floor(cardwidth / 2)
	var topy = 0;
	if (halfheight > cardheight)
		topy = cardheight;
	else
		topy = halfheight;
	var rotation = (90 / 180) * pi;	// rotation in radians for 90 degrees
	var pc = null;	// AnimatableCard to save in gestault
	switch (position) {
		case 0:	// North-center
			// bug: halwdith=halfcardwidth? maybe - etc.?
			/*
			 * drawFadeInImage(feltContext, 0, 0, 0, imagefile, firstx, firsty,
			 * cardwidth, cardheight, halfwidth = halfcardwidth, 0, cardwidth,
			 * cardheight);
			 */
			pc = new AnimatableCard(imagefile, 0, 0, 0, firstx, firsty, cardwidth, cardheight,
				halfwidth = halfcardwidth, 0, cardwidth, cardheight);
			// pc.friendlyName = card.friendlyName;
			/*
			 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
			 * cardheight, // source rectangle halfwidth-halfcardwidth, 0 ,
			 * cardwidth, cardheight // destination rectangle );
			 */
			break;
		case 1: // East half cardwidth shifted left, half screen down
			// genius use of 90-degree rotation
			var upperLeftX = halfwidth + halfcardwidth + xmarginwidth;
			var upperLeftY = topy;
			/*
			 * feltContext.translate(halfwidth, halfheight);
			 * feltContext.rotate(rotation); drawFadeInImage(feltContext,
			 * rotation, halfwidth, halfheight, imagefile, firstx, firsty,
			 * cardwidth, cardheight, // source rectangle -cardwidth,
			 * -cardheight, // not 0 after rotation... cardwidth, cardheight //
			 * destination rectangle );
			 */
			pc = new AnimatableCard(imagefile, rotation, halfwidth, halfheight,
				firstx, firsty, cardwidth, cardheight,
				-cardwidth, -cardheight, cardwidth, cardheight);
			// pc.friendlyName = card.friendlyName;

			/*
			 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
			 * cardheight, // source rectangle -cardwidth, -cardheight , // not
			 * 0 after rotation... cardwidth, cardheight // destination
			 * rectangle );
			 */
			// Undo rotation and translation for next seat
			/*
			 * feltContext.rotate(-rotation); feltContext.translate(-halfwidth,
			 * -halfheight);
			 */
			break;
		case 2:	// South rotated 180 upside down.
			/*
			 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
			 * cardheight, // source rectangle halfwidth-halfcardwidth,
			 * halfheight, cardwidth, cardheight // destination rectangle );
			 */
			/*
			 * drawFadeInImage(feltContext, 0, 0, 0, imagefile, firstx, firsty,
			 * cardwidth, cardheight, // source rectangle halfwidth -
			 * halfcardwidth, halfheight, cardwidth, cardheight // destination //
			 * rectangle );
			 */
			pc = new AnimatableCard(imagefile, 0, 0, 0, firstx, firsty, cardwidth, cardheight,
				halfwidth - halfcardwidth, halfheight, cardwidth, cardheight);
			// pc.friendlyName = card.friendlyName;
			break;
		case 3:	// West
			// genius use of 90-degree rotation
			var upperLeftX = halfwidth + halfcardwidth + xmarginwidth;
			var upperLeftY = topy;
			/*
			 * feltContext.rotate(rotation);
			 */
			/*
			 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
			 * cardheight, // source rectangle halfwidth-halfcardwidth,
			 * -cardheight , // not 0 after rotation... cardwidth, cardheight //
			 * destination rectangle );
			 */
			/*
			 * drawFadeInImage(feltContext, rotation, 0, 0, imagefile, firstx,
			 * firsty, cardwidth, cardheight, // source rectangle halfwidth -
			 * halfcardwidth, -cardheight, // not 0 after // rotation...
			 * cardwidth, cardheight // destination rectangle );
			 */
			pc = new AnimatableCard(imagefile, rotation, 0, 0, firstx, firsty, cardwidth, cardheight,
				halfwidth - halfcardwidth, -cardheight, cardwidth, cardheight);
			// pc.friendlyName = card.friendlyName;

			/*
			 * feltContext.rotate(-rotation);
			 */
			break;
		default:
			console.log("Switch: can't happen.");
	}
	// currentGestault.add(pc);
	// enqueue(pc);
	/*
	 * aaa use CardAnimation and the queue
	 */
	var qItem = new CardAnimation(pc);	// takes an animated card...
	currentTrick.add(pc);
	// debugging
	// this fixes the "first card not getting animated" bug...
	// it shouldn't, but it does.
	// TODO:
	// figure this out...
	if (qEmpty())	// put in twice if empty...
		qEnqueue(qItem);
	qEnqueue(qItem);
	// animator();
	// aaa
	// check isbusy and start animator, if not?
	animateMaybe();
	}


function turnover1card6(card, position) { // New rotation
	if (indexCheck == true)
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
	// console.log("Card:" + card.cardIndex);
	feltContext = feltCanvas.getContext("2d");
	// All cards are packed into their files the same way, so
	// location is based (symmetrically) on the card's rank.
	// Also note that Arrays are 0-based, and the offsets are 1-based
	var firstx = cardXoffset(Rank[card.rank] - 1);
	var firsty = cardYoffset(Rank[card.rank] - 1);

	// console.log("currentSeatToPlay Seat=" + position);
	// console.log("xy-source["+firstx+", "+firsty+"]");
	// console.log("width-height["+cardwidth+", " + cardheight + "]");

	if (card.cardImage == null) {
		card.cardImage = getCardImageFile(card);
	}
	waitForImageLoad();
	//
	// 6handed geometry
	var halfwidth = Math.floor(feltCanvas.width / 2);
	var halfheight = Math.floor(feltCanvas.height / 2);
	var halfxmarginwidth = Math.floor(xmarginwidth / 2);
	var halfcardwidth = Math.floor(cardwidth / 2);

	// calculation for position should place position 4 at the top, so...
	position = (position + 3) % 6;
	// rotate board 60-degrees times the position, splat card, rotate back
	var rotation = ((position * 60) / 180) * pi;	// rotation in radians for
													// 90
	// degrees
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

var imageFilesLoaded = 0;	// number of image files loaded
function logCardImage() {
	// alert("Image loaded?");
	imageFilesLoaded++;
	// console.log("image files loaded=" + imageFilesLoaded);
}

function waitForImageLoad() {
	var sleeps = 10;
	var i;
	for (i = 0; i < sleeps; i++) {
		if (imageFilesLoaded < 4)
			return;
		sleep(100);
	}
}

var webSocket = null;

var debugXStatus = false;
function xstatusUpdate(sMsg) {
	if (debugXStatus)
		alert(sMsg);
	// Note: bizarre soul-sucking bug if you try to user InnerHTMl at least on
	// UNIX browsers...
	document.getElementById("statusArea").textContent
		= sMsg; // this is the only of these that works reliably
	// = '<var>' + sMsg + '</var>' ;
	// insertAdjacentText("afterbegin", sMsg);
	// innterHTML = sMsg ;
	return true;
}

/*
 * put a message right above the user's cards in his had
 */
function gamestatusUpdate(sMsg) {
	document.getElementById("gamestatusArea").textContent
		= sMsg;
}

/*
 * The format of the trick update msg is !000LWB !0 seatid 00LWB[subdeck]
 * 00=trick number L=lead W=Winner B=hearts broken
 */

// old AnimationGestault and basic unit AnimatableCard
var SerialCard=0;
class AnimatableCard {
	// compute the geometry of where the card goes
	constructor(imagefile, rotation, trX, trY, x, y, w, h,
		tgtx, tgty, tgtwidth, tgtheight) {
		this.imagefile = imagefile;
		this.rotation = rotation;
		this.trX = trX;
		this.trY = trY;
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.tgtx = tgtx;
		this.tgty = tgty;
		this.tgtwidth = tgtwidth;
		this.tgtheight = tgtheight;
		this.friendlyName = "debugMe" + SerialCard++;
	}

	// pc = new AnimatableCard(imagefile, 0, 0, 0, firstx, firsty, cardwidth,
	// cardheight,
	// halfwidth=halfcardwidth, 0, cardwidth, cardheight);

	// add paint(); and paint(ctx) and variations for shrinking...
	/*
	 * paint a single frame given opacity
	 */
	paint(opacity) {
		// / set alpha
		var ctx = feltContext; 
		ctx.globalAlpha = opacity;
		ctx.save();
		if (this.trX != 0 || this.trY != 0)
			ctx.translate(this.trX, this.trY);
		if (this.rotation != 0)
			ctx.rotate(this.rotation);
		// / draw image with current alpha
		ctx.drawImage(this.imagefile, 
			this.x, this.y, 
			this.width, this.height,
			this.tgtx, this.tgty, 
			this.tgtwidth, this.tgtheight);
		ctx.restore();
	}
}

/*
 * old... Anything with gestault in the name is the old way scene is the new
 * way... frame is part of the actual system. (don't create routines with frame
 * in the name) AnimationGestault is the old way
 * 
 * new: put frames in the queue, single animation routine enqueue() - endque
 * animation and start server if idle constructor(ArrayOfCards)
 * constructor(gestault
 */
class AnimationGestault {
	constructor() {
		this.cards = new Array();	// list of AnimatableCards
	}
	add(pc) {
		this.cards.push(pc);
	}
	// takes a graphic context and opacity
	// and paint a single frame (called 60 times per second by caller
	paint(bBackground, ctx, opacity) {
		var i, c;
		ctx.globalAlpha = opacity;
		for (i = 0; i < this.cards.length; i++) {
			ctx.save();
			c = this.cards[i];
			if (c.trX != 0 || c.trY != 0)
				ctx.translate(c.trX, c.trY);
			if (c.rotation != 0)
				ctx.rotate(c.rotation);
			if (bBackground) {
				ctx.fillStyle = "ForestGreen";
				ctx.fillRect(c.tgtx, c.tgty, c.tgtwidth, c.tgtheight);
			}
			else {
				ctx.drawImage(c.imagefile,
					c.x, c.y,
					c.width, c.height,
					c.tgtx, c.tgty,
					c.tgtwidth, c.tgtheight);
			}
			ctx.restore();
		}
	}
};

/*
 * Animation queue routines holds an AnimationScene (i.e. TrickAnimation of
 * CardAnimation) which contain one AnimatableCard or multiple. aaa The goal is
 * one animation queue that gets both play and clear events and marches through
 * them. (No hurry-ups) So there is only one routine that diligents paints
 * animation frames of one or more cards, fading in or out, marking bDone = true
 * when done. (bDone initialized as false)
 * 
 * I think enqueue should kick the animator if idle
 */
// queue functions
var q_first = 0, q_last = 0;
const BIGENOUGH = 25;
var theQueue = new Array(BIGENOUGH);

// private function...
function qNextIndex(index) {
	index++;
	if (index >= BIGENOUGH)
		index = 0;
	return index;
}

// public entry points for the animator
function qEmpty() {
	return q_first == q_last;
}

// public entry points for the animator
function qEnqueue(a) {
	theQueue[q_last] = a;
	q_last = qNextIndex(q_last);
	// for the moment, don't start animator...
	// animator();
}

// public entry points for the animator
function qDequeue() {
	var head = null;
	if (qEmpty())
		;
	else {
		head = theQueue[q_first];
		q_first = qNextIndex(q_first);
	}
	return head;
}

// Scene functions aaa
/*
 * AnimationScene (baseclass) then subclasses TrickAnimation and CardAnimation
 * the basic currency is an AnimatableCard if size is 1, then do a fade-in, p2
 * is one card if size > 1, do a fade-out, p2 is an array of cards
 */
class AnimationScene {	// baseclass
	
	constructor(ac) {
		this.animatableCard = ac;
	}
	// draw one frame, one time
	// increment opacity
	// purely abstract class; expects to be overiden in subclass
	init() {
		
	}
	paint() {	
		
	}
	// return whether the animation is done
	done() {
		return true;
	}
}

class CardAnimation extends AnimationScene {
	// A single card to animate; fade-in
	constructor(ac) {
		super();
		this.animatableCard = ac;
		this.opacity = 0.0;
	}
	// set up instance variables to increment/decrement
	// set initial opacity and targets
	init() {
		this.opacity = 0.0;
	}
	next() {
		this.opacity += .02;
	}
	// draw one frame, one time
	// increment opacity
	// return whether the animation is done
	paint() {
		// call card.paint
		this.animatableCard.paint(this.opacity);
	}
	done() {
		return this.opacity >= 1.0;
	}
}

class TrickAnimation extends AnimationScene {
	// an array of cards to animate; fade-out
	// and finally shrink and pile-up
	// subclass variables: list and size
	constructor() {
		super();
		this.animatableCard = null;
		this.opacity = 1.0;
		//
		this.list = new Array();
		this.leader = 0;	// should always be a valid seat
		this.winner = 0;
		this.bIsClosed = false;
		this.friendlyName = "trick"
	}
	size() {
		return this.list.length;
	}
	// draw one frame, one time
	// from each of the AnimatableCard-s in the scene
	// decrement opacity
	paint() {
		// draw all the cards in the trick
		var i;
		// need to clear since writing over the
		// prevous trick fainter doesn't really work
		// aaa
		// this needs to walk from leader to the length
		var cp = 0; // No: trick is in order played... this.leader; // current
					// player
		for (i=0; i<this.list.length; i++, cp=(cp + 1) % nTableSize) {
			var pc=this.list[cp];
			pc.paint(this.opacity);
		}
	}
	init() {
		clearCardTable(false);
		this.opacity = 1.0;
	}
	next() {
		this.opacity -= .02;
		if (this.opacity < 0.0)
			this.opacity = 0.0;
	}
	// return whether the animation is done
	done() {
		return this.opacity <= 0.0;		
	}
	// subclass methods/not in superclass
	// add an animatable card to the list
	add(ac) {
		this.list.push(ac);
	}
	close(leader, winner) {
		this.bIsClosed = true;
		this.leader = leader;
		this.winner = winner;
	}
	isClosed() {
		return this.bIsClosed;
	}
	winningCard() {
		var index=0, card=null;
		if (this.isClosed())
			index = this.winner;
		if (index >= 0 && index < this.size())
			card = this.list[index];
		return card;
	}
	getWinner() {
		return this.winner;
	}
	getLeader() {
		return this.leader;
	}
}

const SpecialEffectType = { UNIMPLEMENTED:-1, CLEAR:5, };
class SpecialEffect extends AnimationScene {
	constructor() {
		super();
		this.iteration = 0;
		this.effectType = SpecialEffectType.CLEAR;
	}
	/*
	 * paint something one time; i.e. clear the table
	 */
	init() {
		this.iteration = 0;
	}
	paint() {	
		clearCardTable(false); // Boom!
	}
	// return whether the animation is done
	done() {
		return this.iteration >= 1;
	}
	next() {
		this.iteration ++;
	}
	setType(t) {
		this.effectType = t;
	}
}
/*
 * animator - do the actual work dequeue items successively set bAnimatorIdle
 * when done and idle
 */
// only one instance can be running at a time
var bAnimatorIdle = true;
// must be global for visibility to lambda function...
var bDontStartAnimatorYet = false;

var animationItemX;	// animation item
var gStart = 0;
var gLast = 0;
function animateMaybe() {
	if (!bAnimatorIdle)
		return;
	if (qEmpty())
		return;
	if (bDontStartAnimatorYet)
		return;
	animator();
}
/*
 * animator - animate items using the theQueue and RAF
 * sets idle when queue is empty.
 */
function animator() {
	
	if (!bAnimatorIdle) {
		// uh oh.. already running; get out!
		return;
	}
	bAnimatorIdle = false; // set any time q is accessed...
	animationItemX = qDequeue();
	if (animationItemX == null) {
		bAnimatorIdle = true; // set any time q is accessed...
		return;
	}
	console.log("Animating:" + animationItemX.friendlyName);
		animationItemX.init();

		/*
		 * keep drawing frames (and requesting a new animation frame) as long as
		 * there are items in the queue
		 */
		(function paintAnimationFrames(timestamp) {
			// timestamp not really used
			if (gStart == 0)
				gStart = timestamp;
			else
				gLast = timestamp;
			if (animationItemX == null)
				return;
			animationItemX.paint();
			animationItemX.next();
			if (animationItemX.done()) {
				animationItemX = qDequeue();
				if (animationItemX != null)
					animationItemX.init();
				}
			if (animationItemX != null)
				requestAnimationFrame(paintAnimationFrames);
			else
				bAnimatorIdle = true;
		})();

}

/*
 * (new) addCardToTrick using the new animation routines. stillborn.
 */
/*
 * function newAddCardToTrick(card, user, n) { // Create an animation frame, put
 * in the queue // and kick the animation server if necessary
 * console.log("Adding{t=" + n + "}:" + card.friendlyName + "@" + user); var
 * frame=new AnimationFrame(); // qEnqueue(frame); frame.enqueue(); }
 */

/*
 * aaa clearTrick - close and enqueue currentTrick; start next trick
 */
function clearTrick(sMsg) {
	document.getElementById("trickArea").textContent
		= sMsg;
	var i;
	// figure out who took the trick
	// and display the appropriate animation
	// ! 00 L W bBroken
	var leader = parseInt(sMsg.charAt(4));
	var winner = parseInt(sMsg.charAt(5));
	console.log("Winner=" + winner);
	// clearCardTable(false);
	// draw a card back at the winner's place...
	switch (nTableSize) {
		case 4:
			// animate the trick by closing it out
			// and placing it in the animation queue
			currentTrick.close(leader, winner);
			qEnqueue(currentTrick);
			previousTrick = currentTrick;
			var se=new SpecialEffect();
			se.setType(SpecialEffectType.CLEAR);
			qEnqueue(se);
			/*
			 * to creat a cardback card, turnover1c4(null,position)
			 */
			turnover1card4(null, winner);
			animator();
			break;
		case 6:
			break;
		default:
			console.log("Snark Magic 4:Uh oh...");

	}
}


function writeToTextArea(msg) {
	appendTextToTextArea("call deprecated:" + msg);
	/*
	 * var echoText = document.getElementById("echoText"); echoText += msg;
	 */
}

/*
 * filter keys to look for <enter>
 */
var verboseGUIIO = false;
function keyFilter(event) {
	if (verboseGUIIO == true) {
		console.log('<key=' + event.keyCode + '>');
	}
	if (event.keyCode == 13) {
		// console.log("return seen. Process!");
		var line = getMsgText();
		xstatusUpdate("[return]Sending{" + line + "} to server...");
		processSubmitAndClearMsgText();
	}
}

function selectButtonPress(event) {
	if (verboseGUIIO == true) {
		console.log('yea!js:Button Pressed');
	}
	// event.preventDefault();
	// console.warn("js:Button Pressed");
	if (event == null) return;
	// alert("Button Pressed...");
	var line = getMsgText();
	xstatusUpdate("[submit]Sending{" + line + "} to server...");
	processSubmitAndClearMsgText();
	// var s = document.getElementById("msgText").value;
	// console.warn("js: textvalue =" + s);
	// appendTextToTextArea(s);
	// xstatusUpdate("[ButtonPress]Sending to server...");
}
function wsOpen(message) {
	// echoText.value += "Connected ... \n";
	setReconnectDisabled(true);
	// don't write this here... write it when the write succeeds maybe?
	// xstatusUpdate("Connected...");
}
function wsCloseConnection() {
	webSocket.close();
}
function wsReconnect() {
	openWebSocket();
}
/*
 * wsGetMessage - the on message received code
 */
function wsGetMessage(message) {
	var s = "" + message.data + "";

	// echoText.value += "server>" + s + "\n";
	// prepend new message to the top of the text box, not bottom
	echoText.value = "server>" + s + "\n" + echoText.value;

	if (isProtocol(s)) {
		// hand off to protocol manager
		processCardString(s);
	}
	return s;
}
// from protocalMessage.java
/*
 * Messages are of the form [CommandChar][PlayerIDorX][Zero or more RS pairs
 * representing Cards]%[MessageString] Player->G = Play Card ~ Pass Card(S) Q
 * Query S Text (TBD) G->Player + Card(s) - Card(s) ? Your Turn? [Cards in
 * current trick] <change to ?> & Trick Update. Cards(s) in the current trick
 * [Player gets one of these every time someone plays]. // [Cards]%trick flags
 * [Hwwxxyy[.;] [Hh] ww=winner;xx=trickid yy=lead or taker] [,.] ! Trick Cleared
 * B Broken Suit (i.e. hearts, spades) [Could probably just put this in the
 * trick update...] $ %Player.score;Player.score; % Player error %Message that
 * can be given to human user i.e. [11 core key messages]
 * 
 * ERROR Text messages that should be supported %!%Not your turn! %2%Must play
 * 2C %N%Hearts/Spades are not broken
 */
// todo: implement the full set...
var protocolMessageTypes = {
	'+': true,	// add cards
	'-': true,	// delete cards
	'?': true,	// your move
	'&': true,	// trick update
	'!': true,	// trick cleared
	'B': true,	// Broken suit
	'$': true,	// player scores
	'%': true,	// player error
	'>': true,	// player welcome
	'~': true,	// pass cards
	'Q': true,  // result of a query
};

/*
 * Messages of the form +Cards -Card
 */
function isProtocol(msg) {
	if (msg.charAt(0) in protocolMessageTypes) {
		return true;
	}
	return false;
}

var seatId = -1;
function setSeatId(seatid) {
	seatId = seatid + 0;	// make sure it's an int, or can be turned into one
	console.log("Setting seatid:" + seatId)
}
function sendCardFromButtonPress(cardindex) {
	// console.log("Sending Card to server:"+cardindex);
	var card = theDeck[cardindex];
	var shortname = card.shortName;
	// make a protocol message, and send to server
	var msg = "" + "=" + seatId + shortname;
	serverWrite(msg);
}

/*
 * processCardString -- process a card protocol message from server
 */
var bNoWelcome=true;
function processCardString(cardString) {
	var card = null;
	var bDelete = false;
	var cUser;
	var i = 0;
	var c0 = cardString.charAt(0);
	switch (c0) {
		case '>':
			setSeatId(parseInt(cardString.charAt(1)));
			bNoWelcome=false;
			console.log("Welcome:" + cardString);
			break;
		case '-':
			// check for -*
			if (cardString.includes('*')) {
				deleteAllCardsFromHand();
				reorgButtons();
				clearCardTable(false);
				break;
			}
			bDelete = true;
		case '+':
			// char 1 is the user id.
			if (!bDelete && bNoWelcome) {
				// when you are dealt cards set the seat value for future
				// messages
				// only set them if you HAVEN'T seen a welcome msg
				// which is the most trustrworthy way to set it
				setSeatId(parseInt(cardString.charAt(1)));
			}
			for (i = 2; i < cardString.length; i += 2) {
				card = decodeCard(cardString.charAt(i),
					cardString.charAt(i + 1));
				if (card != null) {// yikes; null check if something bad
									// happened
					// console.log("Adding:" + card.cardIndex);
					if (bDelete)
						deleteCardFromHand(card.cardIndex);
					else
						addCardToHand(card.cardIndex);
				}
			}
			// reorg the hand so cards are sorted nicely
			reorgButtons();
			break;
		case '?':
			// tell user: your move
			cUser = cardString.charAt(1);
			animator();
			gamestatusUpdate("Your move! seat<" + cUser + ">");
			break;
		case '&':	// 
			// console.log("Trickupdate under construction");
			i = 2;
			card = decodeCard(cardString.charAt(i),
				cardString.charAt(i + 1));
			var user = parseInt(cardString.charAt(1), 10);
			bDontStartAnimatorYet = false;
			for (var j=0; j<cardString.length; j++)
				if (cardString.charAt(j) == '.')
					bDontStartAnimatorYet = true;
			//bDontStartAnimatorYet = false;

			switch (nTableSize) {
				case 4:
					turnover1card4(card, user);
					break;
				case 6:
					// console.log("Recently implemented 6...");
					turnover1card6(card, user);
					break;
				default:
					alert("Unknown table size" + nTableSize);
					return;	// do not pass go, or change currentSeatToPlay;
			}

			break;
		case '!':
			// alert("Clear trick... pausing");
			// line.slice(8)
			clearTrick(cardString);
			// clearCardTable(true);
			break;
		case '%':
			// console.log("%error:" + cardString);
			// report user error
			gamestatusUpdate("error:" + cardString);
			break;
		case 'Q':
		case 'B':
			gameStatusUpdate("Hearts are Broken!");
			break;
		case '$':
			console.log("Starting scoredialog...");
			wsScoreDialog(cardString);
			break;
		case '~':
			// cardString of the form 'NCards to pass left' where N is the
			// actual number
			// just know 3 for now...
			// console.log("Pass message.. working on it...");
			parseDialogStringMessage(cardString);
			wsPassDialog();
			break;
		default:
			console.log("unimplemented protocol msg:"
				+ c0
				+ " not implemented");
			break;
	}

}

/*
 * Passing cards. if we are passing cards, selected cards from hand are
 * diverted.
 */
var bPassingCardsInProgress = false;
/*
 * PassCards are implemented as buttons, from which we scrape the names off of
 * to pass on. Not ideal, obviously.
 */
var passCards = [null, null, null];
var iCurrentFreeCardInPass = 0;
var iPassSize = 3;

var passWindow = null;
var passDiv = null;
var bPassDialogInit = false;
var bNeedToArrange = true;
function wsInitPassDialogWindow(n, sMsg) {
	// divert selected cards to the dialog
	bPassingCardsInProgress = true;
	iCurrentFreeCardInPass = 0;
	var w = window.open("PassCards.html",
		"Pass Cards",
		"width=600,height=370,status=no,toolbars=no,resizeable=yes,location=no"
		// was 410x325
	);
	passWindow = w;
	bPassDialogInit = true;
	bNeedToArrange = true;
}

var scoreWindow;
var bScoreDialogWindowInit = false;

function dismissScoreDialogWindow(event) {
	console.log("Close-Button seen")
	scoreWindow.close();
}
function enableScoreCloseWindowButton() {

	scoreWindow.document.getElementById("dismissScoresButton").disabled = false;
	var closeBtn = scoreWindow.document.getElementById("dismissScoresButton");
	closeBtn.addEventListener("click", dismissScoreDialogWindow);
	console.log("Clsoe button enabled...");
	}

/*
 * formatScore - parse formatted string and place fields in HTML table
 */
var tempHeaders=["_Player", "_Points", "_Total", "???"];

function formatScore(score) {
	var w=scoreWindow;	
	var name="",row=0;
	var handscore="", gamescore="";
	var c, prefix, elem;
	var i,j;
	// 
	// looking at {$name=x.y$}+
	// scan upto $
	for (i=1;i<score.length;i++)
		if (score.charAt(i) == '$')
			break;
	// i points at $ (at top and bottom of loop; pre-increment)
	for (row=0,i++; i<score.length; i++,row++){
		// j points at first char after the $ (first char of name)
		// scan past the name while accumulating it;
		if (i > 5 && score.charAt(i) == '#') // we are starting into
												// headers...
			break;
		for (name="",j=i; j<score.length; j++) {
			c = score.charAt(j);
			// scan up to = for NAME saving chars
			if (c == '=') 
				break;
			else
				name += c;
		}
		// j points at '='
		// scan past the first digit string
		for (handscore="",j++; j<score.length; j++) {
			c = score.charAt(j);
			if (c == '.')
				break;
			else
				handscore += c;
		}
		// j points at .
		// scan past the second digit string
		for (gamescore="",j++; j<score.length; j++) {
			c = score.charAt(j);
			if (c == '$')
				break;
			else
				gamescore += c;
		}
		// j points at '$'
		i = j;
		//
		// post up result in table
		prefix = "p" + row;
		elem = "player" + prefix;
		document.getElementById(elem).innerText = name;
		elem = prefix + "s0";
		document.getElementById(elem).innerText = handscore;
		elem = prefix + "s1";
		document.getElementById(elem).innerText = gamescore;
		}
	// Headers at the end of the string
	// have a header if i is pointing at #
	// ttt
	if (i < score.length) {
		c = score.charAt(i);
	}
	else {
		console.log("No header!");
	}
	//
	// at the top of the loop, char[i] == '#'
	for (var h=0;i<score.length; h++) {
		header = "";
		c = score.charAt(i);
		if (c != '#')	// not
			break;
		for (j=i+1; j<score.length; j++) {
			c = score.charAt(j);
			if (c == '#')
				break;
			else
				header += c;
		}
		if (header.length > 0)
			tempHeaders[h] = header;
		i = j;
	}
	var table=document.getElementById("formattedScoreTable")
	var headerItems = table.getElementsByTagName("th");
	for (var k=0; k<headerItems.length; k++) {
		var header=headerItems.item(k);
		header.innerText = tempHeaders[k];
	}
	// now, go through rows and make visible if I put data in it
	// invisible otherwise.
	// do this for all the rows in the table (i.e. get element and done when
	// there aren't any more
	// should just do getElementsByTagname
	// ...var table=document.getElementById("formattedScoreTable")
	// can set the headers the same way... i.e. iterate the cells in "th"
	// keep in mind that the header is an element
	var rows = table.getElementsByTagName("tr");
	for (i=0; i<rows.length; i++) {
		row=rows.item(i);
		if (i < nTableSize+1)	// nTableSize is number of players in the game
			row.style.display = '';
		else
			row.style.display = 'none';
	}
}

function wsInitScoreDialogWindow(sMsg) {
	// divert selected cards to the dialog
	var w = window.open("PlayerScores.html",
		"Hand Results",
		"width=600,height=370,status=no,toolbars=no,resizeable=yes,location=no"
		// was 410x325
	);
	// cardBtn.addEventListener("click", dismissScoreDialogWindow);
	// cardBtn.addEventListener("dblclick", dismissScoreDialogWindow);
	scoreWindow = w;
	bScoreDialogWindowInit = true;

	window.addEventListener('load', (event) => {
		  console.log('page is fully loaded');
			scoreWindow.document.getElementById("dismissScoresButton").disabled = false;
			closeBtn = scoreWindow.document.getElementById("dismissScoresButton");
			closeBtn.addEventListener("click", dismissScoreDialogWindow);
		});

	/*
	 * theory: fails here because window not fully initialized...
	 */
	/*
	 * scoreWindow.document.getElementById("dismissScoresButton").disabled =
	 * false; var closeBtn; closeBtn =
	 * scoreWindow.document.getElementById("dismissScoresButton");
	 * closeBtn.addEventListener("click", dismissScoreDialogWindow);
	 */
}

function scoreHandlerInstall() {
	console.log("Dialog init event...")
	var closeBtn=null;
	scoreWindow.document.getElementById("dismissScoresButton").disabled = false;
	closeBtn = scoreWindow.document.getElementById("dismissScoresButton");
	closeBtn.addEventListener("click", dismissScoreDialogWindow);
}

/*
 * experimentalFunction stub for experimenting with new elements invoked from
 * index.html
 */
function showModal() {
	var modal = document.getElementById("myExperimentalModal");
	modal.style.display = "block";
	}

function initModal() {
	var modalDiv = document.getElementById("myExperimentalModal");

	// Get the button that opens the modalDiv
	var btn = document.getElementById("myBtn");

	// Get the <span> element that closes the modalDiv
	var span = document.getElementsByClassName("close")[0];

	// When the user clicks the button, open the modalDiv
	btn.onclick = function() {
	  modalDiv.style.display = "block";
	}

	// When the user clicks on <span> (x), close the modalDiv
	span.onclick = function() {
	  modalDiv.style.display = "none";
	}

	// When the user clicks anywhere outside of the modalDiv, close it
	window.onclick = function(event) {
	  if (event.target == modalDiv) {
	    modalDiv.style.display = "none";
	  }
	}

}

/*
 * passDialogDivBtnCall - exercise the pass cards dialog done with a div called
 * from a button on index page should just delegate and call wsPassDialog(2) cf:
 * wsPassDialog -- the new div version
 */
function passDialogDivBtnCall(n, sMsg) {
	wsPassDialog();
}

function showPassDialogDiv() {
	var modal = document.getElementById("passMsgDiv");
	modal.style.display = "block";
	/* modal.classList.add("semModalLeft:Final"); */
	/*
	 * modal.classList.add("left"); modal.classList.add("slideout");
	 */
	expandHandArea();
	}

function hidePassDialogDiv() {
	var modalDiv = document.getElementById("passMsgDiv");
	  modalDiv.style.display = "none";
	}

var defaultPassMsg="Pass 3 cards to the left/right/across";
var passDialogString = defaultPassMsg;
var nCardsToPass=3;
// msg is of form ~03STring...
// "~03String...
function parseDialogStringMessage(s) {
	nCardsToPass = parseInt(s.charAt(2));
	passDialogString = s.substring(3);
}
function initPassDialogDiv(nCards, sMsg) {
	var modalDiv = document.getElementById("passMsgDiv");

	// Get the button that opens the modalDiv
	var btn = document.getElementById("passCardsButton");

	// Get the <span> element that closes the modalDiv
	// var span = document.getElementsByClassName("closePassDialogDiv")[0];
	var span = document.getElementById("closePassDialogDiv");

	var msgDiv=document.getElementById("passMsgText");
	if (sMsg == "")
		msgDiv.innerText = passDialogString;
	else {
		msgDiv.innerText = sMsg;
	}
	
	// When the user clicks the button, open the modalDiv
	btn.onclick = function() {
	  modalDiv.style.display = "block";
	}

	// When the user clicks on <span> (x), close the modalDiv
	span.onclick = hidePassDialogDiv;

	// When the user clicks anywhere outside of the modalDiv, close it
	window.onclick = function(event) {
	  if (event.target == modalDiv) {
	    modalDiv.style.display = "none";
	  }
	}
	if (bPassingCardsInProgress) {
		// this dialog got put up and the previous cards
		// have not been sent.
		// So don't clear them out...
		return;
	}
	bPassingCardsInProgress = true;
	// ++
	var div=document.getElementById("passDiv");
	var buttonList = getDescendantElements(div);
	// remove the items add them back to the div
	/*
	 * remove any cards in the div,
	 */
	for (var i=0; i<buttonList.length; i++) {
		var cardBtn = buttonList[i];
		div.removeChild(cardBtn);
	}
	iCurrentFreeCardInPass = 0;	
	// --
	bPassDialogInit = true;
}

/*
 * wsScoreDialogDiv - pass the score fully modal score over the play field
 */
function wsScoreDialog(sMsg) {
	wsScoreDialogDiv(sMsg);
	// wsScoreDialogWindow(sMsg);
}

function initScoreDialogDiv() {
	// Get the fullymodal
	var fullymodal = document.getElementById("scoreDialogDiv");

	// Get the <span> element that fcloses the fullymodal
	var span = document.getElementsByClassName("fclose")[0];

	// OH. Bug.
	// fmybtn is the experimental button at the bottom of the page...
	// so this makes that button work...

	/*
	 * // Get the button that opens the fullymodal var btn =
	 * document.getElementById("fmyBtn"); // When the user clicks the button,
	 * open the fullymodal btn.onclick = function() { initScoreDialogDiv();
	 * fullymodal.style.display = "block"; }
	 */

	// When the user clicks on <span> (x), close the fullymodal
	span.onclick = function() {
		initScoreDialogDiv();
		fullymodal.style.display = "none";
	}

	// When the user clicks anywhere outside of the fullymodal, close it
	window.onclick = function(event) {
		initScoreDialogDiv();		
		if (event.target == fullymodal) {
			fullymodal.style.display = "none";
		}
	}

}

function wsScoreDialogDiv(score) {
	initScoreDialogDiv();
	
	var fullymodal = document.getElementById("scoreDialogDiv");
	// both block and inline-block seem to work
	fullymodal.style.display = "inline-block";
	
	var s="fjkasd$buzz=0.0$joe=93.95$laura=3.3$anne=0.26$bob=0.26$patti=0.26$";
	if (score == "")
		score = s;
	formatScore(score);	
}

/*
 * function experimentalFunction(score) { wsScoreDialogDiv(score); }
 */
function experimentalFunction(score) {
	toggleHandArea();
	}

function experimentalFunction1(s) {
	console.log("You never know what you're going to get");
}

/*
 * wsScoreDialogWindow - not used anymore (div dialogs are better)
 */
function wsScoreDialogWindow(sMsg) {
// somehow closing it destroys the window;
// for now, create it every time...
console.log("Parsing:" + sMsg);
bScoreDialogWindowInit = false;
if (!bScoreDialogWindowInit)
	wsInitScoreDialogWindow(sMsg);
formatScore(sMsg);	// harmless in window version... the div is still there...
}


/*
 * wsPassDialog - call either the window or div version of the routines -- note
 * args here are obsolete. See wsPassDialog() function
 */
function wsPassDialog(nCards, sMsg) {
	console.log("Modal div for passing cards" + sMsg);
	initPassDialogDiv(nCards, sMsg);
	showPassDialogDiv();
}
/*
 * this is the current way to do this... call parseDialogStringMessage first
 */
function wsPassDialog() {
	initPassDialogDiv(nCardsToPass, passDialogString);
	showPassDialogDiv();	
}

// get a button card that is the cardback
// Note: ** untested ** probably doesn't work
function getCardBackBtn() {
	var cardBtn;
	cardBtn = document.createElement("Button");
	cardBtn.setAttribute("id", "CardIndex" + 15);
	cardBtn.setAttribute("type", "button");
	cardBtn.setAttribute("value", "Search");
	var fname = '"thumblib/' + "bkD" + '_thumb.png"';
	// fname = '"buttonfacetest.png"';
	cardBtn.style.backgroundImage = 'url(' + fname + ')';
	cardBtn.style.backgroundRepeat = "no-repeat";
	/*
	 * Bug: For no apparent reason adding the event listeners with setAttribute
	 * doesn't work. I have no idea why. But addEventListener does work.
	 */
	cardBtn.addEventListener("click", passCardSelected);
	cardBtn.addEventListener("dblclick", passCardSelected);
	// passWindow.document.getElementById("passDiv").appendChild(cardBtn);
	return cardBtn;
	
}
/*
 * createNewButton - create button to be placed in passed card dialog
 */
function createNewPassCardButton(cardindex) {
	var cardBtn;
	cardBtn = document.createElement("Button");
	cardBtn.setAttribute("id", "CardIndex" + cardindex);
	cardBtn.setAttribute("type", "button");
	cardBtn.setAttribute("value", "Search");
    // with images: don't set innerText
	// cardBtn.setAttribute("name","label" + i);
	//
	// set width and height here??? xxx
	cardBtn.style.height = "0";
	cardBtn.style.width = "0";
	cardBtn.style.alignItems = "center";
	cardBtn.style.marginLeft = "auto";
	cardBtn.style.marginRight = "auto";
	// Monkeyed with style to get the display of card/buttons ok in this dialog
	// style="display:flex;padding:10px;margin-right:auto;margin-left:auto;align-items:center;
	cardBtn.style.visibility = "hidden";
    /*
	 * set background image
	 */
	var card = theDeck[cardindex];
	var fname = '"thumblib/' + card.shortName + '_thumb.png"';
	// fname = '"buttonfacetest.png"';
	cardBtn.style.backgroundImage = 'url(' + fname + ')';
	cardBtn.style.backgroundRepeat = "no-repeat";
	/*
	 * Bug: For no apparent reason adding the event listeners with setAttribute
	 * doesn't work. I have no idea why. But addEventListener does work.
	 */
	cardBtn.addEventListener("click", passCardSelected);
	cardBtn.addEventListener("dblclick", passCardSelected);
	document.getElementById("passDiv").appendChild(cardBtn);
	return cardBtn;
}

/*
 * TODO: use or replace passCardSelected unused! passCardSelected - click on the
 * card in the pass dialog
 * 
 * No just unimplemented. Should return card to hand all local. sends no
 * message. Or is this done somewhere else?
 */
/*
 * passCardSelected -- I'm a button on the pass dialog. I've been clicked,
 * indicating user does NOT want to pass me. So... // pull myself off the
 * passdialog // add button back to hand // reorg hand?
 */
function passCardSelected(event) {
	// return the card to the hand, delete from the dialog
	// actually just make it not visible.
	var div=document.getElementById("passDiv");
	// var buttonList = getDescendantElements(div);
	var cardBtn=event.target;
	div.removeChild(cardBtn);
	// Was never actually removed from hand...
	// if it is removed from hand, add it back here...
	var cardIndex = cardIndexFromButtonName(event.target.id);
	var card = theDeck[cardIndex];
	card.handButton.style.opacity = 1.0; 

	// remove from the pass array
	// decrement cards in the dialog
	var i, j, nfound=0;
	var temp=new Array(iPassSize);	// iPassSize
	for (j=i=0; i<iCurrentFreeCardInPass; i++) {
		// Great. found it. remove it by not copying it;
		if (passCards[i] == cardBtn)
			nfound++;	// multiple copies?
		else 
			temp[j++] = passCards[i];			
		}
	for (i=0; i<j; i++)	// copy back the non-found elements
		passCards[i] = temp[i];
	iCurrentFreeCardInPass -= nfound;
	
	console.log("passCard:Click Seen in dialog. Card (sort of) returned to hand...");
}

function passCardsFromButtonPress(event) {
	// first, stop diverting selected cards
	bPassingCardsInProgress = false;
	console.log("passCardtoServer:Click Seen in dialog. Sending...");

	// alert("Yea! but still need to implement actual pass");

	// pull the cards from the list, construct message
	var cardnames = ""
	for (var i = 0; i < passCards.length; i++) {
		var btn = passCards[i];
		/*
		 * sad but true Determine what card to pass from the name of the
		 * button... Is there a better way to store (hide) a value in a button?
		 */
		var buttonName = btn.name;
		var shortname = buttonName;
		cardnames += shortname;
	}
	// and send to the server
	var msg = "" + "~" + seatId + cardnames;
	serverWrite(msg);

	// hide/dismiss the dialog
	// reset index to passCards
	iCurrentFreeCardInPass = 0;
	//
	// this doesn't work for windows...
	// passWindow.visibility = "hidden";
	// just close it and create a new one...
	// hhh
	hidePassDialogDiv();
	
	xstatusUpdate("Passing:" + msg);
}

/*
 * Todo: Note that clicking on the card in the dialog to return it is not
 * actually implemented
 * 
 * This is the window (not div) version
 * 
 * Todo: expendable when you purge the window-creation versions of pass
 * dialogs...
 */

function addCardToPassDialogWindow(cardindex) {
	var card = theDeck[cardindex];
	// xxx get card from index...
	var i = iCurrentFreeCardInPass;
	// push cardPassWindow to top
	passWindow.focus();
	if (i >= iPassSize) {
		// Ooh. Bad User.
		// trying to select more cards to pass than is legal
		// alert("Warning: Can only pass " + iPassSize + " cards. i=" + i);
		alert("Click on card to return it to hand. Can only pass " + iPassSize + " cards. i=" + i);
		// setPassDialogErrorString("Can only pass " + iPassSize + " cards.")
		return;
	}
	//
	// don't add if already there
	for (var j=0; j<i; j++)
		if (passCards[j].name == card.shortName) {
			// It's a dup!
			return;
		}
	passCards[i] = createNewPassCardButton(cardindex);
	var cardBtn = passCards[i];
    // with images: don't set innerText
	// cardBtn.innerText = card.friendlyName;
	// cardBtn.innerText = card.shortName;
	/*
	 * Experiment with adding an image... ++
	 */
	// spadesCardImages
	console.log("Get ready...");
	cardBtn.style.imagefile = spadesCardImages;
	cardBtn.style.backgroundImage = spadesCardImages;
	cardBtn.style.backgroundImage = "http://localhost:8080/Spades/Spades.jpg";
	/*
	 * --
	 */
	cardBtn.name = card.shortName;
	cardBtn.style.visibility = "visible";
	// was
	// cardBtn.style.height = "250px"; // was "75px" "250px" by "180px" works
	// but
	// cardBtn.style.width = "180px"; // was "54px"
	// try:
	cardBtn.style.height = "125px";	// was "75px" "250px" by "180px" works but
									// is big..
	cardBtn.style.width = "60px";	// was "54px"

	iCurrentFreeCardInPass++;
	// iPassSize cards? enable the send button
	if (i >= iPassSize - 1) {
		var passBtn;
		passWindow.document.getElementById("passCardsButton").disabled = false;
		passBtn = passWindow.document.getElementById("passCardsButton");
		passBtn.addEventListener("click", passCardsFromButtonPress);
	}
}

function resetPassDialogErrorString() {
	var p=document.getElementById("passDialogErrorString");
	p.innerText = "Select Cards from Hand";
}

function setPassDialogErrorString(s) {
	// alert(s);
	var p=document.getElementById("passDialogErrorString");
	p.innerText = s;
}
function addCardToPassDialogDiv(cardindex) {
	var card = theDeck[cardindex];
	var i = iCurrentFreeCardInPass;
	resetPassDialogErrorString();

	// push cardPassWindow to top
	// passWindow.focus();
	// Bad user checks...
	if (i >= iPassSize) {
		// Ooh. Bad User. Can't add one more card.
		// trying to select more cards to pass than is legal
		// alert("Warning: Can only pass " + iPassSize + " cards. i=" + i);
		// alert("Click on card to return it to hand. Can only pass " +
		// iPassSize + " cards. i=" + i);
		setPassDialogErrorString("Can only pass " + iPassSize + " cards.")
		return;
	}
	//
	// Make sure card isn't already there...
	for (var j=0; j< i; j++) {
		// is it a dup?
		btn = passCards[j];
		if (btn.name == card.shortName) {
			// setPassDialogErrorString("Duplicate card selected");
			setPassDialogErrorString("Card must be unique.");
			return;
		}		
	}
	passCards[i] = createNewPassCardButton(cardindex);
	var cardBtn = passCards[i];
    // with images: don't set innerText
	// cardBtn.innerText = card.friendlyName;
	// cardBtn.innerText = card.shortName;
	/*
	 * Experiment with adding an image... ++
	 */
	// spadesCardImages
	// console.log("Get ready...");
	/*
	 * No longer needed, right? cardBtn.style.imagefile = spadesCardImages;
	 * cardBtn.style.backgroundImage = spadesCardImages;
	 * cardBtn.style.backgroundImage =
	 * "http://localhost:8080/Spades/Spades.jpg";
	 */
	/*
	 * --
	 */
	cardBtn.name = card.shortName;
	cardBtn.style.visibility = "visible";
	cardBtn.style.height = "250px";	// was "75px" "250px" by "180px" works but
									// is big..
	cardBtn.style.width = "180px";	// was "54px"

	/*
	 * Allow card to be returned to hand here? No. Already done... See
	 * passCardSelected
	 */
	// cardBtn.addEventListener("click", returnPassCardToHandButtonPress);

	iCurrentFreeCardInPass++;
	// iPassSize cards? enable the send button
	if (i >= iPassSize - 1) {
		var passBtn;
		/*
		 * passWindow.document.getElementById("passCardsButton").disabled =
		 * false; passBtn =
		 * passWindow.document.getElementById("passCardsButton");
		 * passBtn.addEventListener("click", passCardsFromButtonPress);
		 */
		document.getElementById("passCardsButton").disabled = false;
		passBtn = document.getElementById("passCardsButton");
		passBtn.addEventListener("click", passCardsFromButtonPress);
	}
}


/*
 * setReconnectDisabled - disable the reconnect button
 */
function setReconnectDisabled(bDisabled) {
	var button = document.getElementById("reconnectButton");
	// button.style.visibility = "visible";
	button.disabled = bDisabled;
}

/*
 * never tested... function setReconnectInactive() { var button =
 * document.getElementById("reconnectButton"); //button.style.visibility =
 * "hidden"; button.disabled = true; }
 */

function wsClose(message) {
	// echoText.value += "Disconnect ... \n";
	setReconnectDisabled(false);
	xstatusUpdate("Disconnected w/msg:" + message);
}

// obsolete: wserror -- superceded by xstatusUpdate
function wsError(message) {
	echoText.value += "Error ... \n";
}

function whereami() {
	// var s=document.getElementById("scriptdiv").outerHTML;
	var s = location.hostname;
	s = window.location.host;
	console.log("url:" + s);
	return s;
}

// Construct sConnectionString as:
// sWS + sHost + sPort + sRoot + sEndPoint
// so ws:// dragonreef.net :8080 /Spades/ + websocket/ + user
// by convention each component (except host) that needs a leading / adds it7
// (nothing starts with a / except sRoot)
// also: sHost and sPort have no slashes
// the port is tacked on my window.host
var sWS = "ws://";
var sHost = "172.98.72.44:8080";	// determined at runtime or set by user with
									// .server=
// var sPort=":8080"; // can't be set by user, yet
var sRoot = "/Spades";	// was sServiceName
var sEndPoint = "/server/ws";
var sUser = "";
var sConnectionString = "";	// constructed by formatConnectionString

/*
 * formatConnectionString - build the connection string from componenets: called
 * to make use componenets that were changed interactively
 */
var bDetermineHostDynamically = true;
function formatConnectionString() {
	/*
	 * if user has set host explicitly, don't set the host
	 */
	if (bDetermineHostDynamically)
		sHost = whereami();
	sConnectionString = sWS + sHost // + sPort
		+ sRoot + sEndPoint + sUser;
}

// Todo: Review: obsolete?
// only used by Hack* functions
// kill sService and sServiceName
// var sService = "ws://172.98.72.44:8080/";
// var sEndPoint="gameserver";
// var sEndPoint="server/ws";
// Try making the directory structure congruent
// to where the endpoint is to be deployed


// added showhack
// TODO: review:
// showhack should survive the purge of obsolete functions...
// should be showConnectionString or just show???
// Not sure of name yet.
function showHack() {
	formatConnectionString();
	echoText.value = "using:" + sConnectionString + "...\n" + echoText.value;
	console.log("using:" + sConnectionString);
}

function setHost(host) {
	// Since user has explicitly set the host, don't override it
	// when you construct the string
	bDetermineHostDynamically = false;
	sHost = host;
}

function setRoot(root) {
	sRoot = root;
}

function setEndpoint(endpoint) {
	sEndPoint = endpoint;
}

function setUser(s) {
	sUser = s;
}

// var sService = "ws://127.0.0.1:8080/"
function openWebSocket() {
	formatConnectionString();
	console.log("opening:" + sConnectionString + "...");
	// appendTextToTextArea("connecting to " + sConnectionString + "...");
	echoText.value = "connecting to " + sConnectionString + "..." + echoText.value;
	/*
	 * webSocket = new WebSocket("ws://localhost.net:8080/" + sServiceName +
	 * "/websocketendpoint");
	 */
	webSocket = new WebSocket(sConnectionString);

	var message = document.getElementById("message");
	webSocket.onopen = function(message) { wsOpen(message); };
	webSocket.onmessage = function(message) { wsGetMessage(message); };
	webSocket.onclose = function(message) { wsClose(message); };
	webSocket.onerror = function(message) { wsError(message); };
}

// sleep for parameter ms milliseconds.
function sleep(ms) {
	return new Promise(resolve => setTimeout(resolve, ms));
}

// returns true on successful write; false on error
function serverWrite(msg) {
	if (webSocket == null || webSocket.readyState > 1) {
		openWebSocket();
	}
	// wait (but not too long) while connecting...
	for (var i = 0; i < 10; i++) {
		if (webSocket.readyState == 0 ||
			webSocket.readyState == undefined)
			sleep(50);
		else
			break;
	}
	if (webSocket.readyState == 0 || webSocket.readyState == undefined) {
		console.log("Connecting successfully... Netwok slow; Try to write again in a second.");
		xstatusUpdate("Connecting successfully... Netwok slow; Try to write again in a second.")
		return false;
	}
	else if (webSocket.readyState == 1) {	// Ready. Connection established
		webSocket.send(msg);
		return true;
	} else if (webSocket.readyState == 2 || webSocket.readyState == 3) {
		xstatusUpdate("Connection to server has been closed. Please Reconnect.")
		return false;
	} else {
		console.log("Unknown status(" + webSocket.readyState + ") unable to write:" + msg);
		xstatusUpdate("unable to write:{" + msg + "} Network temporarily unavailable. Please try again.")
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

// get text string, append, and clear box
// Handle <return> and submit button the same way
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
		// console.log("full-line:" + line);
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

function rankOrdinal(c) {
	if (parseInt(c))
		return c;
	switch (c) {
	case 'T':
	case 't':
		return 10;
	case 'j':
	case 'J':
		return 11;
	case 'q':
	case 'Q':
		return 12;
	case 'k':
	case 'K':
		return 13;
	case 'a':
	case 'A':
		return 14;
	default:
		return 0;
	}
	return 0;
}
/*
 * true if c1 >= c2
 */
function higherCard(c1CardButton, c2CardButton) {
	var cardIndex1 = c1CardButton.name.match(/\d+/);
	var cardIndex2 = c2CardButton.name.match(/\d+/);
	var card1 = theDeck[cardIndex1];
	var card2 = theDeck[cardIndex2];
	var c1 = rankOrdinal(card1.shortName[0]);
	var c2 = rankOrdinal(card2.shortName[0]);
	return c1 >= c2;
}
function lowerCard(c1CardButton, c2CardButton) {
	var cardIndex1 = c1CardButton.name.match(/\d+/);
	var cardIndex2 = c2CardButton.name.match(/\d+/);
	var card1 = theDeck[cardIndex1];
	var card2 = theDeck[cardIndex2];
	var c1 = rankOrdinal(card1.shortName[0]);
	var c2 = rankOrdinal(card2.shortName[0]);
	return c1 <= c2;
}
function sortByDescendingCardOrder(a) {
	for (var i=0; i<a.length; i++) {
		// highest card is in a[i]
		for (var j=i+1; j<a.length; j++)
			if (higherCard(a[j], a[i])) {
				// swap
				var t=a[i];				
				a[i] = a[j];
				a[j] = t;
			}
	}
	return a;
}
/*
 * sortby .. sort only the first len elements
 */
function sortByAscendingCardOrder(a, len) {
	for (var i=0; i<len; i++) {
		// highest card is in a[i]
		for (var j=i+1; j<len; j++)
			if (lowerCard(a[j], a[i])) {
				// swap
				var t=a[i];				
				a[i] = a[j];
				a[j] = t;
			}
	}
	return a;
}

/*
 * reorgButtonsInDeiv - delete all the button-nodes, saving the visible ones in
 * button list. Sorting them, and putting them back in sdiv.
 * 
 * Bug: sorting them doesn't seem to do anything. No idea why.
 */
function extractCardName(b) {
	var index = b.name.match(/\d+/);
	var card = theDeck[index];
	var cname = card.shortName;
	return cname;
}

function dumpButtonlist(name, buttonList, len) {
	var s = "";
	for (var i=0; i<len; i++)
		s = s + extractCardName(buttonList[i]) + ",";
	console.log(name + "=" + s + ".");
 }

var rowMaxButtons = 8;	// max of 8 cards in a row...
function reorgButtonsInDiv(sdiv) {
	if (bNeedToArrange)	// should work off bCardsAdded
		; // arrangeCardsInDivs();
	var div=document.getElementById(sdiv);
	var buttonList = getDescendantElements(div);
	var i, j, nSkipped=0;
	for (i=j=0; i<buttonList.length; i++) {
		var cardBtn = buttonList[i];
		if (div.removeChild(cardBtn) == null)
			console.log("Can't happen: remove child failed.")
		if (cardBtn.style.visibility == "hidden") {
			nSkipped++;
		} else {
			buttonList[j++] = cardBtn;	// j is first free visible button
			}
	}
	dumpButtonlist(sdiv, buttonList, j);
	sortByAscendingCardOrder(buttonList, j);
	dumpButtonlist(sdiv, buttonList, j);
	// var span=document.createElement("span");
	for (i=0; i<j; i++) {
		var cardBtn=buttonList[i];
		div.prepend(cardBtn);
	}		
	// div.prepend(span);
	div.className = "buttonGrid";
	// div.className = "alignLeft";
	// alignRight is another interesting option...
	// except it doesn't work if there are more than 4 cards
	// div.className = "alignRight";
	console.log("Reorg consolidated " + nSkipped + " cards in" + sdiv);
}

function reorgButtonsInDivXXXSave(sdiv) {
	if (bNeedToArrange)	// should work off bCardsAdded
		arrangeCardsInDivs();
	var buttonList=null; // = new Array();
	var cardBtn=null;
	// start with "ClubsInHandDiv"
	var div=document.getElementById(sdiv);
	// div.style.alignItems = "left"; // Ok...

	buttonList = getDescendantElements(div);
	// buttonList = sortByCardOrder(buttonList);
	// remove the items add them back to the div
	var i, j, nSkipped=0;
	// div.style.left = "50px";
	/*
	 * remove all the cards from the div, and add back the visible ones
	 */
	for (i=0; i<buttonList.length; i++) {
		cardBtn = buttonList[i];
		if (div.removeChild(cardBtn) == null)
			console.log("Can't really happen. removeChile Failed.");
		/*
		 * comment now invalid?... TODO: fix comment? for now squirrel away a
		 * card that is not visible... i.e. take it out of the div
		 */
	}
	// minmargin no longer used? Delete??
	// var minmargin = 0;
	/*
	 * Ok, so count the blank buttons in the array and then pad the buttonlist
	 * from the left So that it right justifies. Can't figure out any other way
	 * to do this...
	 */
	/*
	 * for (i=0; i<buttonList.length; i++) { cardBtn=buttonList[i]; // don't
	 * add in cards that should be hidden if (cardBtn.style.visibility ==
	 * "hidden") { nSkipped++; if (blankBtn == null) blankBtn = cardBtn; } }
	 */
	/*
	 * No don't. Really.
	 * 
	 * //var k=0; // append nSkipped blanks to div for (j=0; j<nSkipped; j++) {
	 * if (blankBtn != null) { div.appendChild(blankBtn); k++; } }
	 */
	// console.log("Prepending " + k + " blanks to " + sdiv)
	/*
	 * now, append sorted (visible) cards to the div
	 */
	for (i=0; i<buttonList.length; i++) {
		cardBtn=buttonList[i];
		// skip hidden cards
		if (cardBtn.style.visibility == "hidden") {
			nSkipped++;
		} else {
			div.appendChild(cardBtn);
			// minmargin += 140;
			}
	}
	div.className = "buttonGrid";
	// alignRight is another interesting option...
	// div.className = "alignRight";
	// TODO: perhaps need to make cards narrower, though.. perhaps float...
	console.log("Reorg consolidated " + nSkipped + " cards in" + sdiv);
}

// Review:
// this is two sort of good ideas that should just be one thing...

var cardDivs=[
	"ClubsInHandDiv",
	"DiamondsInHandDiv",
	"SpadesInHandDiv",
	"HeartsInHandDiv",
];
var stringArrayOfDivNames=new Array(4);
stringArrayOfDivNames[CLUBS] = "ClubsInHandDiv";
stringArrayOfDivNames[DIAMONDS] = "DiamondsInHandDiv";
stringArrayOfDivNames[HEARTS] = "HeartsInHandDiv";
stringArrayOfDivNames[SPADES] = "SpadesInHandDiv";

/*
 * for (var key in stringArrayOfDivNames) { console.log(key + "->" +
 * stringArrayOfDivNames[key]); }
 */

// zzz
function getSuitDiv(card) {
	var sdiv = stringArrayOfDivNames[card.suit];
	var div = document.getElementById(sdiv);
	return div;
}

function getDescendantElements(parent) {
	return [].slice.call(parent.getElementsByTagName('*'));
}

function clearDivs() {
	for (var j=0; j<cardDivs.length; j++) {
		var sdiv=cardDivs[j];
		var div=document.getElementById(sdiv);
		var buttonList = getDescendantElements(div);
		// buttonList = sortByCardOrder(buttonList);
		// remove the items add them back to the div
		// var i, j, nSkipped=0;
		// div.style.left = "50px";
		for (var i=0; i<buttonList.length; i++) {
			var cardBtn = buttonList[i];
			if (div.removeChild(cardBtn) == null)
				console.log("Can this happen? RemoveChild failed...");
		}
	}
}

// xxx
function resizeHandArea(width, innerwidth) {
	expandHandArea();
}

function expandHandArea() {
	var div=document.getElementById("handArea");
	var startx = 160;
	div.x = startx;
	div.width = clientWidth - startx;
	div.style.zIndex = 3;
	// div.css("z-index", 4);
}

// this is really just hide hand area...
function contractHandArea() {
	var div=document.getElementById("handArea");
	div.style.zIndex = 0;
	}

function toggleHandArea() {
	var div=document.getElementById("handArea");

	if (div.style.zIndex > 2)
		div.style.zIndex = 0;
	else
		div.style.zIndex = 3;
}

function arrangeCardsInDivs() {
		console.log("Uh oh. I shouldn't be called. Ever.");
		var always=true;
		if (always)
			return;
		clearDivs();
		var controlDiv = document.getElementById("ClubsInHandDiv");
		for (var i = 0; i < theDeck.length; i++) {
			var card = theDeck[i];
			var cardBtn = card.handButton;
			if (cardBtn.style.visibility == "hidden")
				continue;
			// Hmmm TODO:
			// should put all the divs in an array and just pull them
			// out of the array...
			switch (card.suit) {
			case CLUBS:
				controlDiv = document.getElementById("ClubsInHandDiv");
				break;
			case DIAMONDS:
				controlDiv = document.getElementById("DiamondsInHandDiv");
				break;
			case SPADES:
				controlDiv = document.getElementById("SpadesInHandDiv");
				break;
			case HEARTS:
				controlDiv = document.getElementById("HeartsInHandDiv");
				break;
			}
			controlDiv.appendChild(cardBtn);
		}
	}

function reorgButtons() {
	// put the hand-index over the top of the table-canvas
	// .style.zIndex
	var handDiv = document.getElementById("handArea");
	handDiv.style.zIndex = 2;
	for (var i=0; i<cardDivs.length; i++)
		reorgButtonsInDiv(cardDivs[i]);
}

/*
 * processLocalCommand - process a typed-in line locally
 */
function processLocalCommand(line) {
	// var commandStatus=false; // assume failure...

	// pick off lines beginning with .
	if (line.startsWith(".")) // as soon as you know .Something you will eat
		// and process the line
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
			var n = parseInt(text, 10);
			if (setTableSize(n)) {
				xstatusUpdate("Setting table param=" + n);
			} else {
				xstatusUpdate("Invalid table parameter. ignored");
			}
		}
	} else if (line.includes("config")) {
		var flag = detectConfig();
		if (flag)
			xstatusUpdate("config=" + "mobile width/inner:" + scrWidth +  ":" + scrInnerWidth  + " height:" + scrHeight);
		else
			xstatusUpdate("config=" + "desktop width/inner:" + scrWidth + ":" + scrInnerWidth  +  " height:" + scrHeight);
	} else if (line.includes("narrow")) {
		var flag = detectConfig();
		if (flag)
			xstatusUpdate("config=" + "mobile width/inner:" + scrWidth +  ":" + scrInnerWidth  + " height:" + scrHeight);
		else
			xstatusUpdate("config=" + "desktop width/inner:" + scrWidth + ":" + scrInnerWidth  +  " height:" + scrHeight);
		setNarrow();
	} else if (line.includes("wide")) {
		var flag = detectConfig();
		if (flag)
			xstatusUpdate("config=" + "mobile width/inner:" + scrWidth +  ":" + scrInnerWidth  + " height:" + scrHeight);
		else
			xstatusUpdate("config=" + "desktop width/inner:" + scrWidth + ":" + scrInnerWidth  +  " height:" + scrHeight);
		setWide();
	} else if (line.includes("grab=")) {
		var matches = line.match(/(\d+)/); // if a number at all...
		if (matches) {
			var text = matches[0];
			// parseInt(text,10); not needed??
			var n = parseInt(text, 10);
			if (n >= 0 && n < 52 && addCardToHand(n)) {
				xstatusUpdate("grabbing card cardindex=" + n);
			} else {
				xstatusUpdate("Invalid grab parameter. ignored");
			}
		}
	} else if (line.includes("status=")) {
		// Todo: Review: Not sure this is useful anymore...
		// from the 2nd char after the = to the end
		// ... just tests the status message
		gamestatusUpdate(line.slice(8));
	} else if (line.includes("unhack")) {
		unHack();
	} else if (line.includes("hack=")) {
		// sets the endpoint componenet of the connect string
		// from the 2nd char after the = to the end
		var param = line.slice(6);	// length + .
		// hackWSString(param);
		sstatusUpdate("Obolete: must set individual components: .host= .port= .root= .endpoint= .user= ")
		// xstatusUpdate("string="+param);
	} else if (line.includes("whereami")) {
		// sets the endpoint componenet of the connect string
		// from the 2nd char after the = to the end
		xstatusUpdate("host:" + whereami() + " aka " + window.location.host);
	} else if (line.includes("host=")) {
		// sets the server component
		// from the 2nd char after the = to the end
		var param = line.slice(6);	// length + .
		setHost(param);
		// xstatusUpdate("string="+param);
		xstatusUpdate("host=" + param + " connect=" + sConnectionString);
	} else if (line.includes("root=")) {
		// from the 2nd char after the = to the end
		var param = line.slice(6);
		setRoot(param);
		formatConnectionString();
		xstatusUpdate("root=" + param + " connect=" + sConnectionString);
	} else if (line.includes("endpoint=")) {
		// sets the endpoint componenet of the connect string
		// from the 2nd char after the = to the end
		var param = line.slice(10);	// length + .
		// hackWSString(param);
		// xstatusUpdate("string="+param );
		setEndpoint(param);
		xstatusUpdate("endpoint=" + param + " connect=" + sConnectionString);
	} else if (line.includes("user=")) {
		// from the 2nd char after the = to the end
		var param = line.slice(6);
		setUser(param);
		// xstatusUpdate("user="+param);
		xstatusUpdate("user=" + param + " connect=" + sConnectionString);
	} else if (line.includes("showhack") ||
		line.includes("show")) {
		showHack();
		xstatusUpdate("host=" + sHost +
			" root=" + sRoot +
			" endpoint=" + sEndPoint +
			" user=" + sUser);
		xstatusUpdate("connect=" + sConnectionString);
	} else if (line.includes("parse")) {
		xstatusUpdate("host=" + sHost +
			" root=" + sRoot +
			" endpoint=" + sEndPoint +
			" user=" + sUser);
	} else if (line.includes("clear")) {
		clearCardTable(false);
		fadeOutTrick(currentGestault);
		// clearCardTable(true);
		xstatusUpdate("Table Cleared and Reset.");
	} else if (line.includes("close")) {
		// command obsolete... has to do with window style dialogs (using div
		// version)
		// dismissScoreDialogWindow(null);
		enableScoreCloseWindowButton();
		xstatusUpdate("manually close score dialog window");
	} else if (line.includes("score")) {
		// shouldn't matter if you specify an = or not in this command
		// because slice starts at six and format ignores the first character
		// assuming protocol stuff
		var score = line.slice(6);
		var s="fjkasd$buzz=0.0$joe=93.95$laura=3.3$anne=0.26$bob=0.26$patti=0.26$";
		// s = "#S0gXpX#/default=0.0#$robot1=10.10#$robot2=13.13#$robot3=3.3#";
		if (score == "")
			score = s;
		// experimentalFunction formats the score if sent...
		// formatScore(score);
		// TODO: change this name!!!
		// cf //score which actually gets the score and routes to format
		experimentalFunction(score);
		xstatusUpdate("score["+s+"]");
	} else if (line.includes("reorg")) {
		// Array of div names
		// foreach div name
		// get buttons
		// place into a temp array in reverse card-sorted order
		// add back into div
		reorgButtons();
		bNeedToArrange = false;
		xstatusUpdate("reorg of button divs complete.");
	} else if (line.includes("repaint")) {
		clearCardTable(false);
		if (previousTrick != null) {
			xstatusUpdate("Repainting...");
			qEnqueue(previousTrick);
			}
		else if (currentTrick == null)
			xstatusUpdate("Nothing to repaint.");			
		else {
			xstatusUpdate("Repainting current.");
			qEnqueue(currentTrick);
		}
		xstatusUpdate("Repainting current.");
		/*
		 * if (currentGestault == null) repaint(saveGestault); else
		 * repaint(currentGestault);
		 */
	} else if (line.includes("last")) {
		repaint(saveGestault);
		xstatusUpdate("Last trick...");
	} else {
		xstatusUpdate("*** Unrecognized command. ignored ***");
	}

	return true;	// if I've made it this far, it's at least .something
}

function appendTextToTextArea(newtext) {
	var old = document.getElementById("statusArea").value;
	document.getElementById("statusArea").value = old + newtext;
}

/*
 * export/import at this time are failed features. reverting to text/javascript
 * and assuming macro-style importing
 * 
 * export {openWebSocket, selectButtonPress, keyFilter }; // I don't know why
 * import doesn't have a semicolon but export does. Go figure.
 */

// The functions called directly from HTML... Last...

function wsSendMessage() {
	var line = getMsgText();
	xstatusUpdate("[submit-button]Sending{" + line + "} to server...");
	processSubmitAndClearMsgText();
	// console.log("Sending:" + message.value);
	// webSocket.send(message.value);
	// serverWrite(message.value);
	// echoText.value += "Message sent to the server : " + message.value + "\n";
	// message.value = "";
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
 * dropDown - When the user clicks on the button, toggle between hiding and
 * showing the dropdown content
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