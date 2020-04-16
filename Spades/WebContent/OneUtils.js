/**
 * CardUtils - card data structures and utility functions
 */
'use strict'; 
const minorVersion ="1b" ;
const versionString1="OneUtils.js version 0." + 
	minorVersion +
	"(prerelease) [do not use .mjs experimental version.]";
// console.warn(VersionString); // Moved to GameConsole.html
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
var sRanks="A23456789TJQK";
var sSuits="CDHS";
// this is ignored ZZZ
/**
 * Moving SocketUtils and NewCardUtils into one file to avoid the module
 * import/export bugs Virtual Card and Table added playVirtualCard
 * clearVirtualTable resetVirtualTable
 */
/*
 * playVirtualCard
 */
function playVirtualCard(card, position) {
	// clear physical table
	// adjust leader, if necessary
	// add card to vector at position
	// set hascards[position] to false
	// set transition on last card
}
/*
 * clearVirtualTable
 */
function clearVirtualTable(positionWinner) {
	// reset leader and current to -1
	// foreach element set to null/false
	// do nothing to table, except to
	// let transitions run; eventually show who took trick visually
}
/*
 * resetVitualTable
 */
function resetVirtualTable() {
	// blank the board for the first trick
	// killing off any transitions if they are happening
}
// end ignored ZZZ

var theDeck = null;
var deckInitialized=false;
function initializeTheDeck() {
	if (theDeck != null)
		return;
	theDeck = new Array();
	deckInitialized = true;
	var cardindex=0;
	var r=0, s=0;
	for (var suit in Suit) {
		r=0;
		for (var rank in Rank) {
			var card;
			var sRank=""+rank, sSuit=""+suit;
			// console.log("creating:"+rank+suit);
			card = new Card(rank, suit, cardindex);
			card.friendlyName = sRank + sSuit;
			card.shortName = sRanks.charAt(r) + sSuits.charAt(s);
			theDeck.push(card);
			cardindex ++;
			r++;
		}
		s++;
	}

	var verboseInit=false;
	if (verboseInit) {
		for (var i=0; i<theDeck.length; i++) {
			console.log("created: " + theDeck[i].rank + theDeck[i].Suit.name);
		}
	}
}

/*
 * decodeCard - determine a card in the deck by rank and string i.e. this is
 * used when reading server protocol messages and placing cards in the hand,
 * deleting them, etc. returns the card it finds or null
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
	var buttonName = event.target.id;
	var uniqueId = t.type + t.id;
	var special = t.textContent;
	// console.log("Whoa!" + uniqueId+ "->" + t.textContent);
	// alert("Whoa Nellie! in called with" + t.id + special);
	var digitString=buttonName.replace(/\D/g,"");
	var cardIndex = parseInt(digitString);
	//
	// if the pass dialog is up, pass it. 
	// otherwise play it
	if (bPassingCardsInProgress)
		addCardToPassDialog(cardIndex);
	else
		playCardFromButtonPress(cardIndex);
}

function cardSelected2(event) {
	var t = event.target;
	var uniqueId = t.type + t.id;
	console.log("Double Whoa!" + uniqueid);
	alert("Whoa Nellie! a doubleclick was seen in:" + t.id);
	playCardFromButtonPress(parseInt(t.id,10));
}

/*
 * wsHandInit - (was wsShowHand) initialize hand details including creating
 * buttons for each card to be added to a hand
 */
function wsHandInit() {
	var i=0;
	// alert(handWindow.location.href);
		var controlDiv=document.getElementById("CardsInHandDiv");
		// now in main window...
		// var controlDiv=document.getElementById("CardsInHandDiv");
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
		    // theDeck[i].handButton = cardBtn;
		    var card = theDeck[i];
		    card.handButton = cardBtn;

	       // Set the attributes on the button
	       cardBtn.setAttribute("id", "CardButton" + i);
	       cardBtn.setAttribute("type","button");
	       cardBtn.setAttribute("value","Search");
	       /*
	        * aaa images: don't set innerText
	        */
	       //cardBtn.innerText = card.friendlyName;
	       cardBtn.setAttribute("name","label" + i);
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
	       cardBtn.style.alignItems="left";
	       /*
	        * construct name from shortname + _thumb.png
	        */
	       var fname = '"thumblib/' + card.shortName + '_thumb.png"';
	       //  fname = '"buttonfacetest.png"';

	       cardBtn.style.backgroundImage = 'url(' + fname + ')';
	       cardBtn.style.backgroundRepeat = "no-repeat";
	       /*
			 * Bug: For no apparent reason adding the event listeners with
			 * setAttribute doesn't work. I have no idea why. But
			 * addEventListener does work.
			 */
	       cardBtn.addEventListener("click", cardSelected);
	       cardBtn.addEventListener("dblclick", cardSelected2);
	       cardBtn.setAttribute("data-arg1", "User-Button"+ i);
	       // cardBtn.style.marginLeft = "20px";
	       // cardBtn.style.marginTop = "20px";
	       
	       // Add the button to the div holding cards
	       // see below:
	       //controlDiv.appendChild(cardBtn);
		}
		/*
		 * Place cards in suit-related div
		 */
		controlDiv=document.getElementById("ClubsInHandDiv");
		for (i=0; i<52; i++) {
			card = theDeck[i];
			cardBtn = card.handButton;
			switch (i) {
			case 13:
				controlDiv=document.getElementById("DiamondsInHandDiv");
				break;
			case 26:
				controlDiv=document.getElementById("SpadesInHandDiv");
				break;
			case 39:
				controlDiv=document.getElementById("HeartsInHandDiv");
				break;
			}
			controlDiv.appendChild(cardBtn);			
		}
	
}

/*
 * make card (by cardindex) visible in hand
 */
function addCardToHand(cardindex) {
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
	// card.cardBtn.style.visibility = "visible";
}

/*
 * deleteCardFromHand - just set to to be not visible
 */
function deleteCardFromHand(cardindex) {
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
    // card.handButton.style.height = "75px";
    // card.handButton.style.width = "54px";
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
/* Was:
const cardwidth=180;		// shared constants...
const cardheight=250;
const xmarginwidth=23;
const ymarginwidth=16;
*/
const cardwidth=180.4;	// (180) 1pixel fix 4-16-20	
const cardheight=250.3;	// (250)
const xmarginwidth=22.75;	// (23)
const ymarginwidth=16.5;	// (16) 1pixel fix 4-16-20

/*
 * wsInitFelt - initialize the canvas in the game window
 */
var canvasWidth=500;
var canvasHeight=500;
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
				feltContext.fillRect(0,0, canvasWidth, canvasHeight);

				var cardWidth=200, cardHeight=500;
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
					 * cardheight, // source rectangle 0, 0, cardwidth,
					 * cardheight // destination rectangle );
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
	// alert(handWindow.location.href);
	
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
				// splat the playing felt
				feltContext.fillRect(0,0, canvasWidth, canvasHeight);
				var cardWidth=200, cardHeight=500;
				/*
				for (let i=0; i<52; i++) {
					feltContext.fillRect(i*cardWidth,0,cardWidth,cardHeight);
					feltContext.strokeRect(i*cardWidth,0,cardWidth, cardHeight);	
					
				} */
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
				suitcardImages = feltWindow.document.getElementById("ClubsImage");
				clubsCardImages = suitcardImages; // first one... clean this
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
					 * feltContext.drawImage(suitcardImages, 0, 0, cardwidth,
					 * cardheight, // source rectangle 0, 0, cardwidth,
					 * cardheight // destination rectangle );
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
	// return fakeRandom++;
	var j = Math.random() * between0andXminus1;
	// if (j < 1) return 1;
	return Math.floor(j);
}

// cards are in a 5x3 grid in the .jpg file; result is zero indexed
// return x-coordinate of serial number (integer) card in pixels
function cardXoffset(card) {
	// determine the column card is in
	var cardInCol=Math.floor(card%5);
	// now determine the number of pixels + border to offset in x direction
	var pixels=cardInCol * cardwidth + cardInCol * xmarginwidth;
	return pixels;
}

// return y-coordinate of serial number (integer) card in pixels
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
 * cardPos is 0, NORTH at top, clockwise to number of players rotate through
 * positions; state is currentSeatToPlay
 */
/*
 * game state machine
 */
var currentSeatToPlay=0; // badly named; should be cardSeat or something like
							// that
var indexCheck=false;	// debugging variable for console writes
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
			feltContext.fillRect(0,0, canvasWidth, canvasHeight);
			var cardWidth=200, cardHeight=500;
			// what the hell is this crap?
			for (let i=0; i<52; i++) {
				feltContext.fillRect(i*cardWidth,0,cardWidth,cardHeight);
				//feltContext.strokeRect(i*cardWidth,0,cardWidth, cardHeight);					
			}
		}

	if (bResetTrick)
		currentSeatToPlay = 0;
	}
}

function turnover1card() { // Bug: currently ignores random card and retries it
							// in 1-4 and 1-6...
	var randomcard=jrandom(52);	// pick a card, any card.
	var card=theDeck[randomcard];
	if (indexCheck == true) {
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
		console.log("Card:" + card.cardIndex);
	}
	// Next, switch on the table size...
	switch (nTableSize) {
	case 4:
		turnover1card4(card, currentSeatToPlay);
		break;
	case 6:
		// console.log("Recently implemented 6...");
		turnover1card6(card, currentSeatToPlay);
		break;
	default:
		alert("Unknown table size"+nTableSize);
		return;	// do not pass go, or change currentSeatToPlay;
	}

	currentSeatToPlay = (currentSeatToPlay + 1) % nTableSize;
}
var isBusy=false;
var hurryTransitions = false;

function hurryUp() {
	hurryTransitions = true;
}
function hurryUp(b) {
	hurryTransitions = b;
}
// drawFadeInImage(feltcontext, card.cardImage, firstx, firsty, cardwidth,
// cardeight,
// halfwidth=halfcardwidth, 0, cardwith, cardheight);
function drawFadeInImageXXX(ctx, 
		rotation, trX, trY,
		img,
		x, y, width, height, 
		tgtx, tgty, tgtwidth, tgtheight) {

	// var img; // / current image to fade in
	        var opacity = 0; // / current globalAlpha of canvas

	    // / if we're in a fade exit until done
	    if (isBusy) return;
	    isBusy = true;

	    // / what image to use
	    // img = toggle ? image2 : image;

	    // / fade in
	    (function fadeIn() {
	        // / set alpha
	    	if (hurryTransitions)
	    		opacity = 1.0;
	    	
	    	ctx.globalAlpha = opacity;

			/*
			 * feltContext.translate(halfwidth, halfheight);
			 * feltContext.rotate(rotation);
			 */

	        ctx.save();
	        if (trX != 0 || trY !=0)
	        	ctx.translate(trX, trY);
	        if (rotation != 0)
	        	ctx.rotate(rotation);
	        // / draw image with current alpha
	        ctx.drawImage(img, x, y, width, height, 
	        		tgtx, tgty, tgtwidth, tgtheight);
	        ctx.restore();
	        // / increase alpha to 1, then exit resetting isBusy flag
	        opacity += fIncrement;	// was .02
	        if (opacity < 1)
	            requestAnimationFrame(fadeIn);
	        else
	            isBusy = false;
	    })();

}

function resolve(p1){}
function reject(p1){}
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
	        if (trX != 0 || trY !=0)
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
 * passed an animationScene
 */
function resolveWithClear() {
	clearCardTable(false); //xxx
}
var fIncrement = .01;
function fadeOutTrick(scene) {

	if (scene == null)
		return;
	
	   let promise = new Promise(function(resolveWithClear, reject) {
		   var opacity = 0.0; // / current globalAlpha of canvas
		   let ctx=feltContext;
		//
		// changes the opacity and calls paint in a loop
	 (function fadeOut() {
	        // set alpha
	        //ctx.globalAlpha = opacity;
	    	scene.paint(true, ctx, opacity);
	        if (opacity <= 1.0) {
		        opacity += fIncrement;	// was .02
	            requestAnimationFrame(fadeOut);
	        } else {
	            isBusy = false;
	            //scene.paint(ctx, 0.0);
	            //clearCardTable(false);
	        }
	        opacity += fIncrement;	// was .02
	    })();
	        });
}

// repaint...
function repaint(scene) {

	//clearCardTable(true);
	if (scene == null)
		return;
	
	   let promise = new Promise(function(resolve, reject) {
		   var opacity = 0.0; // / current globalAlpha of canvas
		   let ctx=feltContext;
		//
		// fade in the entire trick so far...
	 (function rp() {
	    	scene.paint(false, ctx, opacity);
	        if (opacity <= 1.0) {
		        opacity += fIncrement;	// was .02
	            requestAnimationFrame(rp);
	        } else {
	            isBusy = false;
	            //scene.paint(ctx, 0.0);
	            //clearCardTable(false);
	        }
	        opacity += fIncrement;	// was .02
	    })();
	        });
}

// ok, so
/*
 * turnover1card4 show card in position; if the card is null... turn over a
 * cardback in the position
 */
var currentScene = null;
var saveScene=null;
function turnover1card4(card, position) { // New rotation
	// if there is no current scene, create one;
	if (currentScene == null)
		currentScene = new AnimationScene();
	if (indexCheck == true)
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
	// console.log("Card:" + card.cardIndex);
	feltContext = feltCanvas.getContext("2d");
	var lastx, lasty;
	// All cards are packed into their files the same way, so
	// location is based (symmetrically) on the card's rank.
	// Also note that Arrays are 0-based, and the offsets are 1-based
	
	var firstx=0;
	var firsty=0;
	var imagefile=null;
	if (card == null) {	// i.e. display a card back
		firstx=cardXoffset(14);
		firsty=cardYoffset(14);
		imagefile=suitcardImages;
		// use suitcardImages
	} else {
		firstx=cardXoffset(Rank[card.rank] - 1);
		firsty=cardYoffset(Rank[card.rank] - 1);
	}

	/*
	 * console.log("currentSeatToPlay Seat=" + position);
	 * console.log("xy-source["+firstx+", "+firsty+"]");
	 * console.log("width-height["+cardwidth+", " + cardheight + "]");
	 */
	
	// xxx
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
	// Assume 4handed for now
	var halfwidth=Math.floor(feltCanvas.width/2);
	var halfheight=Math.floor(feltCanvas.height/2); 
	var halfxmarginwidth=Math.floor(xmarginwidth/2);
	var halfcardwidth=Math.floor(cardwidth/2)
	// var halfymarginheight=Math.floor(xmarginheight/2);
	var topy=0;
	if (halfheight > cardheight)
		topy = cardheight;
	else
		topy = halfheight;
	var rotation=(90/180) * pi;	// rotation in radians for 90 degrees
	var pc = null;	// playcard to save in scene
	switch (position) {
	case 0:	// North-center
		// bug: halwdith=halfcardwidth? maybe - etc.?
		drawFadeInImage(feltContext, 0, 0, 0, imagefile, firstx, firsty, cardwidth, cardheight,
				halfwidth=halfcardwidth, 0, cardwidth, cardheight);

		pc = new PlayCard(imagefile, 0, 0, 0, firstx, firsty, cardwidth, cardheight,
				halfwidth=halfcardwidth, 0, cardwidth, cardheight);
		/*
		 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
		 * cardheight, // source rectangle halfwidth-halfcardwidth, 0 ,
		 * cardwidth, cardheight // destination rectangle );
		 */
		break;
	case 1: // East half cardwidth shifted left, half screen down
		// genius use of 90-degree rotation
		var upperLeftX=halfwidth+halfcardwidth+xmarginwidth;
		var upperLeftY=topy;
		feltContext.translate(halfwidth, halfheight);
		feltContext.rotate(rotation);
		drawFadeInImage(feltContext, rotation, halfwidth, halfheight, 
				imagefile, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				-cardwidth, -cardheight , // not 0 after rotation...
					cardwidth, cardheight		// destination rectangle
				); 
		pc = new PlayCard(imagefile, rotation, halfwidth, halfheight, 
				firstx, firsty, cardwidth, cardheight,
				-cardwidth, -cardheight, cardwidth, cardheight);

		/*
		 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
		 * cardheight, // source rectangle -cardwidth, -cardheight , // not 0
		 * after rotation... cardwidth, cardheight // destination rectangle );
		 */
		// Undo rotation and translation for next seat
		feltContext.rotate(-rotation);
		feltContext.translate(-halfwidth, -halfheight);
		break;
	case 2:	// South rotated 180 upside down.
		/*
		 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
		 * cardheight, // source rectangle halfwidth-halfcardwidth, halfheight,
		 * cardwidth, cardheight // destination rectangle );
		 */
		drawFadeInImage(feltContext, 0, 0, 0, imagefile, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth-halfcardwidth, halfheight, cardwidth, cardheight		// destination
																				// rectangle
				);
		pc = new PlayCard(imagefile, 0, 0, 0, firstx, firsty, cardwidth, cardheight,
				halfwidth-halfcardwidth, halfheight, cardwidth, cardheight);
		break;
	case 3:	// West
		// genius use of 90-degree rotation
		var upperLeftX=halfwidth+halfcardwidth+xmarginwidth;
		var upperLeftY=topy;
		feltContext.rotate(rotation);
		/*
		 * feltContext.drawImage(card.cardImage, firstx, firsty, cardwidth,
		 * cardheight, // source rectangle halfwidth-halfcardwidth, -cardheight , //
		 * not 0 after rotation... cardwidth, cardheight // destination
		 * rectangle );
		 */
		drawFadeInImage(feltContext, rotation, 0, 0, 
				imagefile, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth-halfcardwidth, -cardheight , // not 0 after
														// rotation...
					cardwidth, cardheight		// destination rectangle
				)
		pc = new PlayCard(imagefile, rotation, 0, 0, firstx, firsty, cardwidth, cardheight,
				halfwidth-halfcardwidth, -cardheight, cardwidth, cardheight);
		
		feltContext.rotate(-rotation);
		break;
		default:
			console.log("Switch: can't happen.");
	}
	currentScene.add(pc);
}

function turnover1card6(card, position) { // New rotation
	if (indexCheck == true)
		console.log("Card.index=", card.cardIndex, "for (suit,rank)=", card.suit, card.rank);
	// console.log("Card:" + card.cardIndex);
	feltContext = feltCanvas.getContext("2d");
	var lastx, lasty;
	// All cards are packed into their files the same way, so
	// location is based (symmetrically) on the card's rank.
	// Also note that Arrays are 0-based, and the offsets are 1-based
	var firstx=cardXoffset(Rank[card.rank] - 1);
	var firsty=cardYoffset(Rank[card.rank] - 1);

	// console.log("currentSeatToPlay Seat=" + position);
	// console.log("xy-source["+firstx+", "+firsty+"]");
	// console.log("width-height["+cardwidth+", " + cardheight + "]");

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
	var rotation=((position*60)/180) * pi;	// rotation in radians for 90
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
	var rotation=((position*60)/180) * pi;	// rotation in radians for 90
											// degrees
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
	// alert("Image loaded?");
	imageFilesLoaded++;
	// console.log("image files loaded=" + imageFilesLoaded);
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
	// var halfymarginheight=Math.floor(xmarginheight/2);
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
			halfwidth-halfcardwidth, 0 , cardwidth, cardheight		// destination
																	// rectangle
			);
		break;
	case 1: // East half cardwith shifted left, half screen down
		rotation = pi/2;
		// feltContext.translate(halfwidth, halfheight);
		// feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth+halfcardwidth+xmarginwidth, topy, cardwidth, cardheight		// destination
																						// rectangle
				);
		// feltContext.rotate(-rotation);
		// feltContext.translate(-halfwidth, -halfheight);
		break;
	case 2:	// South rotated 180 upside down.
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				halfwidth-halfcardwidth, halfheight, cardwidth, cardheight		// destination
																				// rectangle
				);
		break;
	case 3:	// West
		rotation = (45/180)*pi;
		// feltContext.rotate(rotation);
		feltContext.drawImage(card.cardImage, 
				firstx, firsty, cardwidth, cardheight, // source rectangle
				0, topy, cardwidth, cardheight		// destination rectangle
				);
		// feltContext.rotate(-rotation);
		break;
		default:
			console.log("Switch: can't happen.");
	}
	
	currentSeatToPlay = (currentSeatToPlay + 1) % nTableSize;
}

var webSocket = null;

var debugXStatus=false;
function xstatusUpdate(sMsg) { 
	if (debugXStatus)
		alert(sMsg);
	// Note: bizarre soul-sucking bug if you try to user InnerHTMl at least on
	// UNIX browsers...
	document.getElementById("statusArea").textContent 
		= sMsg ; // this is the only of these that works reliably
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
	= sMsg ;	
}

/*
 * The format of the trick update msg is !000LWB !0 seatid 00LWB[subdeck]
 * 00=trick number L=lead W=Winner B=hearts broken
 */

// new AnimationScene
class PlayCard {
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
		this.tgtx= tgtx;
		this.tgty = tgty;
		this.tgtwidth = tgtwidth;
		this.tgtheight = tgtheight;
	}
	
	// pc = new PlayCard(imagefile, 0, 0, 0, firstx, firsty, cardwidth,
	// cardheight,
		// halfwidth=halfcardwidth, 0, cardwidth, cardheight);

}

class AnimationScene {
	constructor(){
		this.cards = new Array();	// list of playcards
	}
	add(pc) {
		this.cards.push(pc);
	}
	// takes a graphic context and opacity
	// and paint a single frame (called 60 times per second by caller
	paint(bBackground, ctx, opacity) {
		var i, c;
		ctx.globalAlpha = opacity;
		for (i=0; i<this.cards.length; i++) {
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
				c.x,	c.y,
				c.width, c.height, 
				c.tgtx, c.tgty, 
				c.tgtwidth, c.tgtheight);				
			}
			ctx.restore();
		}
	}
};
/*
 * clearTrick - first routine to use the new AnimationScene object
 */
function clearTrick(sMsg) {
	document.getElementById("trickArea").textContent 
	= sMsg ;	
	var i;
	// No... use transitions here...
	// xxx
	// figure out who took the trick
	// and display the appropriate animation
	// ! 00 L W bBroken
	var leader=parseInt(sMsg.charAt(4));
	var winner=parseInt(sMsg.charAt(5));
	console.log("Winner=" + winner);
	// clearCardTable(false);
	// draw a card back at the winner's place...
	switch (nTableSize) {
	case 4:
		fadeOutTrick(currentScene);
		turnover1card4(null, winner);		
		// then draw a null card in the winner's space...
		// could interfere with creating a new trick...
		saveScene = currentScene;
		currentScene = null;
		break;
	case 6:
		break;
	default:
		console.log("Snark Magic 4:Uh oh...");

	}
}

function clearTrickSave(sMsg) {
	document.getElementById("trickArea").textContent 
	= sMsg ;	
	// No... use transitions here...
	// xxx
	// figure out who took the trick
	// and display the appropriate animation
	// ! 00 L W bBroken
	var leader=parseInt(sMsg.charAt(4));
	var winner=parseInt(sMsg.charAt(5));
	console.log("Winner=" + winner);
	// clearCardTable(false);
	// draw a card back at the winner's place...
	switch (nTableSize) {
	case 4:
		turnover1card4(null, winner);
		// clear the table, and put the card back in front of the leader
		clearCardTable(false);
		turnover1card4(null, winner);		
		clearCardTable(false);
		turnover1card4(null, winner);		
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
var verboseGUIIO=false;
function keyFilter(event) {
	if (verboseGUIIO == true) {
		console.log('<key=' + event.keyCode + '>');
	}
	if (event.keyCode == 13 ) {
		// console.log("return seen. Process!");
		var line=getMsgText();
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
	var line=getMsgText();
	xstatusUpdate("[submit]Sending{" + line + "} to server...");
	processSubmitAndClearMsgText();
	// var s = document.getElementById("msgText").value;
	// console.warn("js: textvalue =" + s);
	// appendTextToTextArea(s);
	// xstatusUpdate("[ButtonPress]Sending to server...");
}
function wsOpen(message){
	// echoText.value += "Connected ... \n";
	setReconnectDisabled(true);
	// xxx don't write this here... write it when the write succeeds maybe?
	//xstatusUpdate("Connected...");
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
 * Messages of the form +Cards -Card
 */
function isProtocol(msg) {
	if (msg.charAt(0) in protocolMessageTypes) {
		return true;	
	}
	return false;
}

var seatId=-1;
function setSeatId(seatid) {
	seatId=seatid + 0;	// make sure it's an int, or can be turned into one
	console.log("Setting seatid:" + seatId)
}
function playCardFromButtonPress(cardindex) {
	// console.log("Sending Card to server:"+cardindex);
	var card=theDeck[cardindex];
	var shortname=card.shortName;
	// make a protocol message, and send to server
	var msg = "" + "=" + seatId + shortname;
	serverWrite(msg);
}

/*
 * pcs processCardString -- process a card protocol message from server
 */
function processCardString(cardString) {
	var card=null;
	var bDelete=false;
	var cUser;
	var i=0;
	var c0=cardString.charAt(0);
	switch(c0) {
	case '-':
		bDelete=true;
	case '+':
		// char 1 is the user id.
		if (!bDelete) {
			// when you are dealt cards set the seat value for future messages
			setSeatId(parseInt(cardString.charAt(1)));
		}
		for (i=2; i<cardString.length; i+=2) {
			card = decodeCard(cardString.charAt(i),
					cardString.charAt(i+1));
			if (card != null) {// yikes; null check if something bad happened
				// console.log("Adding:" + card.cardIndex);
				if (bDelete)
					deleteCardFromHand(card.cardIndex);
				else
					addCardToHand(card.cardIndex);
			}
		}
		break;
	case '?':
		// tell user: your move
		cUser = cardString.charAt(1);
		gamestatusUpdate("Your move! seat<"+cUser+">");
		break;
	case '&':	// 
		/*
		 * Just got a new message... check that transitions are finished before
		 * we start another one...
		 */
		// console.log("Trickupdate under construction");
		i=2;
		card = decodeCard(cardString.charAt(i),
				cardString.charAt(i+1));
		var user=parseInt(cardString.charAt(1), 10);
		switch (nTableSize) {
		case 4:
			turnover1card4(card, user);
			// sleep so that user sees it...
			// no doesn't really work. Make cleartable use transitions...
			// sleep(1000);
			break;
		case 6:
			// console.log("Recently implemented 6...");
			turnover1card6(card, user);
			break;
		default:
			alert("Unknown table size"+nTableSize);
			return;	// do not pass go, or change currentSeatToPlay;
		}

		break;
	case '!':
		console.log("clearTrick under construction");
		// line.slice(8)
		clearTrick(cardString);
		// clearCardTable(true);
		break;
	case '%':
		// console.log("%error:" + cardString);
		// report user error
		gamestatusUpdate("error:" + cardString);
		break;
	default:
		console.log("unimplemented protocol msg:" 
				+ c0 
				+ " not implemented");
			break;
	}
	
}

/*
 * Passing cards.
 *  if we are passing cards, selected cards from hand
 *  are diverted.
 */
var bPassingCardsInProgress=false;
/*
 * PassCards are implemented as buttons, from which we
 *  scrape the names off of to pass on.
 *  Not ideal, obviously.
 */
var passCards=[null,null,null];
var iCurrentFreeCardinPass=0;
var iPassSize=3;

var passWindow=null;
var passDiv=null;
var bPassDialogInit=false;
function wsInitPassDialog(n, sMsg) {
	// divert selected cards to the dialog
	bPassingCardsInProgress = true;
	iCurrentFreeCardinPass=0;
	var w=window.open("PassCards.html", 
					"Pass Cards",
					"width=410,height=325,status=no,toolbars=no,resizeable=yes,location=no"
			);
    passWindow = w;
    bPassDialogInit = true;
}

/*
 * wsPassDialog - bring up the pass card dialog
 */
function wsPassDialog(n, sMsg) {
	// somehow closing it destroys the window;
	// for now, create it every time...
	bPassDialogInit = false; 
	if (!bPassDialogInit)
		wsInitPassDialog(n, sMsg);
    // This doesn't work when we do this here.
    // Why? Don't know. Blame javascript.
    //passBtn = w.document.getElementById("passCardsButton");
    //passBtn.addEventListener("click", passCardsFromButtonPress);
	//passWindow.visibility = "visible";
}

/*
 * createNewButton - create button to be placed in passed card dialog
 */
function createNewPassCardButton(cardindex) {
	var cardBtn;
	cardBtn = document.createElement("Button");
    cardBtn.setAttribute("id", "CardIndex" + cardindex);
    cardBtn.setAttribute("type","button");
    cardBtn.setAttribute("value","Search");
    /*
     * aaa images: don't set innerText
     */
    //cardBtn.setAttribute("name","label" + i);
    cardBtn.style.height = "0";
    cardBtn.style.width = "0";
    cardBtn.style.alignItems="center";
    cardBtn.style.marginLeft="auto";
    cardBtn.style.marginRight="auto";
    // Monkeyed with style to get the display of card/buttons ok in this dialog
    //style="display:flex;padding:10px;margin-right:auto;margin-left:auto;align-items:center;
    cardBtn.style.visibility = "hidden";
    /*
     * set background image
     */
    var card = theDeck[cardindex];
    var fname = '"thumblib/' + card.shortName + '_thumb.png"';
    //    fname = '"buttonfacetest.png"';
    cardBtn.style.backgroundImage = 'url(' + fname + ')';
    cardBtn.style.backgroundRepeat = "no-repeat";
    	/*
	 * Bug: For no apparent reason adding the event listeners with setAttribute
	 * doesn't work. I have no idea why. But addEventListener does work.
	 */
    cardBtn.addEventListener("click", passCardSelected);
    cardBtn.addEventListener("dblclick", passCardSelected);
    passWindow.document.getElementById("passDiv").appendChild(cardBtn);
    return cardBtn;
}

function passCardSelected(event) {
	// return the card to the hand, delete from the dialog
	// actually just make it not visible.
	//
	// clear out the passDiv so the next time it comes up
	// there are no cards in it.
	console.log("passCard:Click Seen in dialog. Return card...");
}

function passCardsFromButtonPress(event) {
	// xxx in progress
	// first, stop diverting selected cards
	bPassingCardsInProgress = false;
	console.log("passCardtoServer:Click Seen in dialog. Sending...");	

	//alert("Yea! but still need to implement actual pass");

	// pull the cards from the list, construct message
	var cardnames=""
	for (var i=0; i<passCards.length; i++) {
		var btn=passCards[i];
		/* 
		 * sad but true
		 * Determine what card to pass
		 * from the name of the button...
		 * Is there a better way to store (hide)
		 * a value in a button?
		 */
		var buttonName = btn.name;
		var shortname=buttonName;
		cardnames += shortname;
	}
	// and send to the server
	var msg = "" + "~" + seatId + cardnames;
	serverWrite(msg);

	// hide/dismiss the dialog
	// reset index to passCards
	iCurrentFreeCardinPass=0;
	//
	// this doesn't work for windows...
	// passWindow.visibility = "hidden";
	// just close it and create a new one...
	passWindow.close();
	xstatusUpdate("Passing:" + msg);
}

/*
 * Todo:
 * Note that clicking on the card in the dialog to return it is not
 * actually implemented
 */
function addCardToPassDialog(cardindex) {
	var card = theDeck[cardindex];
	// xxx get card from index...
	var i = iCurrentFreeCardinPass;
	// push cardPassWindow to top
	passWindow.focus();
	if (i >= iPassSize) {
		// Ooh. Bad User. 
		// trying to select more cards to pass than is legal
		//alert("Warning: Can only pass " + iPassSize + " cards. i=" + i);
		alert("Click on card to return it to hand. Can only pass " + iPassSize + " cards. i=" + i);
		return;
	} 
	passCards[i] = createNewPassCardButton(cardindex);
	var cardBtn = passCards[i];
    /*
     * aaa images: don't set innerText
     */
//	cardBtn.innerText = card.friendlyName;
//	cardBtn.innerText = card.shortName;
	/*
	 * Experiment with adding an image...
	 * ++
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
	cardBtn.style.height = "75px";
	cardBtn.style.width = "54px";
	
	iCurrentFreeCardinPass++;
	// iPassSize cards? enable the send button		
	if (i >= iPassSize - 1) {
		var passBtn;
		passWindow.document.getElementById("passCardsButton").disabled = false;
	    passBtn = passWindow.document.getElementById("passCardsButton");
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

function wsClose(message){
	// echoText.value += "Disconnect ... \n";
	setReconnectDisabled(false);
	xstatusUpdate("Disconnected w/msg:" + message); 
}

// obsolete: wserror -- superceded by xstatusUpdate
function wsError(message){
	echoText.value += "Error ... \n";
}

function whereami() {
	//var s=document.getElementById("scriptdiv").outerHTML;
	var s=location.hostname;
	s = window.location.host;
	console.log("url:" + s);
	return s;
}

// Construct sConnectionString as:
//	sWS + sHost + sPort + sRoot + sEndPoint
//so ws:// dragonreef.net :8080 /Spades/ + websocket/ + user
// by convention each component (except host) that needs a leading / adds it7
//(nothing starts with a / except sRoot)
// also: sHost and sPort have no slashes
// the port is tacked on my window.host
var sWS="ws://";
var sHost="172.98.72.44:8080";	// determined at runtime or set by user with .server=
//var sPort=":8080";			// can't be set by user, yet
var sRoot = "/Spades";	// was sServiceName
var sEndPoint="/server/ws";
var sUser="";
var sConnectionString="";	// constructed by formatConnectionString

/*
 * formatConnectionString - build the connection string from componenets:
 * called to make use componenets that were changed interactively
 */
var bDetermineHostDynamically=true;
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
//var sEndPoint="gameserver";
//var sEndPoint="server/ws";
// Try making the directory structure congruent
// to where the endpoint is to be deployed


//added showhack
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

//var sService = "ws://127.0.0.1:8080/"
function openWebSocket() {
	formatConnectionString();
	console.log("opening:" + sConnectionString + "...");
	//appendTextToTextArea("connecting to " + sConnectionString + "...");
	echoText.value = "connecting to " + sConnectionString + "..." + echoText.value;
	/*webSocket = new WebSocket("ws://localhost.net:8080/" +
				sServiceName +
				"/websocketendpoint"); */
	webSocket = new WebSocket(sConnectionString);

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

// returns true on successful write; false on error
function serverWrite(msg){
	if (webSocket == null || webSocket.readyState > 1) {
		openWebSocket();
	}
	// wait (but not too long) while connecting...
	for (var i=0; i<10; i++) {
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
		// Todo: Review: Not sure this is useful anymore...
		// from the 2nd char after the = to the end
		gamestatusUpdate(line.slice(8)); 
	} else if (line.includes("unhack")) {
		unHack();
	} else if (line.includes("hack=")) {
		// sets the endpoint componenet of the connect string
		// from the 2nd char after the = to the end
		var param=line.slice(6);	// length + .
		//hackWSString(param); 
		sstatusUpdate("Obolete: must set individual components: .host= .port= .root= .endpoint= .user= ")
		//xstatusUpdate("string="+param);
	} else if (line.includes("whereami")) {
		// sets the endpoint componenet of the connect string
		// from the 2nd char after the = to the end
		xstatusUpdate("host:"+whereami()+ " aka " + window.location.host);
	} else if (line.includes("host=")) {
		// sets the server component
		// from the 2nd char after the = to the end
		var param=line.slice(6);	// length + .
		setHost(param); 
		//xstatusUpdate("string="+param);
		xstatusUpdate("host="+param + " connect=" + sConnectionString);
	} else if (line.includes("root=")) {
		// from the 2nd char after the = to the end
		var param=line.slice(6);
		setRoot(param); 
		formatConnectionString();
		xstatusUpdate("root="+param + " connect=" + sConnectionString);
	} else if (line.includes("endpoint=")) {
		// sets the endpoint componenet of the connect string
		// from the 2nd char after the = to the end
		var param=line.slice(10);	// length + .
		//hackWSString(param); 
		//xstatusUpdate("string="+param );
		setEndpoint(param);
		xstatusUpdate("endpoint="+param + " connect=" + sConnectionString);
	} else if (line.includes("user=")) {
		// from the 2nd char after the = to the end
		var param=line.slice(6);
		setUser(param); 
		//xstatusUpdate("user="+param);
		xstatusUpdate("user="+param + " connect=" + sConnectionString);
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
		clearCardTable(false)
		fadeOutTrick(currentScene);
    	//clearCardTable(true);
    	xstatusUpdate("Table Cleared and Reset.");
    } else if (line.includes("repaint")) {
		clearCardTable(false);
    	if (currentScene == null)
    		repaint(saveScene);
    	else 
    		repaint(currentScene);
    	xstatusUpdate("Repainting...");
    } else if (line.includes("last")) {
    	repaint(saveScene);
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

function wsSendMessage1(){
	var line=getMsgText();
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
