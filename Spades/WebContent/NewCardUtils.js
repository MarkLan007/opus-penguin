/**
 * CardUtils - card data structures and utility functions
 */

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
const Suit = {
		CLUBS:	1,
		DIAMONDS:	2,
		HEARTS:		3,
		SPADES:		4
};

class Card {
	//var cardindex;
	//var rank;
	//var suit;
	constructor(r, s) {
		this.rank = r;
		this.suit = s;		
	};
	
}

var theDeck = new Array();

function initializeTheDeck() {
	for (var suit in Suit) {
		for (var rank in Rank) {
			var card;
			//console.log("creating:"+rank+suit);
			card = new Card(rank, suit);
			theDeck.push(card);
		}
	}
	// Add card images for each card
	// xxx
	var verboseInit=false;
	for (var i=0; i<theDeck.length; i++) {
		if (verboseInit)
			console.log("created: " + theDeck[i].rank + theDeck[i].suit);
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

	var feltContext=null;
	//var feltCanvas=null;

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
				suitcardImages = feltWindow.document.getElementById("ClubsImage");
				if (suitcardImages == null) 
					alert("Failed to obtain card image file");
				else {
					// just draw the first card for now...
					var width=suitcardImages.width;
					var height=suitcardImages.height;
					console.log("width:" + suitcardImages.width + "->" + cardwidth);					
					console.log("height:" + suitcardImages.height + "->" + cardheight);
					
					feltContext.drawImage(suitcardImages, 
							0, 0, cardwidth, cardheight, // source rectangle
							0, 0, cardwidth, cardheight	// destination rectangle
							);
							
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

function turnover1card() {
	var card=jrandom(15); // random card on a suit page...
	console.log("Card:" + card);
	feltContext = feltCanvas.getContext("2d");
	var lastx, lasty;
	var firstx=cardXoffset(card);
	var firsty=cardYoffset(card);
	console.log("xy-source["+firstx+", "+firsty+"]");
	console.log("width-height["+cardwidth+", " + cardheight + "]");

	feltContext.drawImage(suitcardImages, 
			firstx, firsty, cardwidth, cardheight, // source rectangle
			cardwidth+xmarginwidth, 0 , cardwidth, cardheight		// destination rectangle
			);
}

function showCardImage() {
	//alert("Image loaded?");
	console.log("image loaded");	
}

export {wsShowFelt }