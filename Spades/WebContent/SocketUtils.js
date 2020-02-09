/**
 * 
 */
function initSocketUtils() {
	alert("initSocket: stub");
	console.log("initSocke: stub");
}

/*
function serverWrite(){
	webSocket.send(message.value);
	echoText.value += "Message sent to srvr:" + message.value + "\n";
	message.value = "";
} */


const sServiceName = "Spades";
var webSocket = null;
function openWebSocket() {
	webSocket = new WebSocket("ws://localhost:8080/" +
				sServiceName +
				"/websocketendpoint");
	// todo: something about an error...
}
function xstatusUpdate(sMsg) {
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
 * filter keys to look for <enter>
 */
var verbose=true;
function keyFilter(event) {
	if (verbose == true) {
		console.log('<key=' + event.keyCode + '>');
	}
	if (event.keyCode == 13 ) {
		console.log("return seen. Process!");
		xstatusUpdate("[return]Sending to server...");
		processSubmitAndClearMsgText();
	}
}

function selectButtonPress(event) {
	//event.preventDefault();
	console.warn("js:Button Pressed");
	if (event == null) return;
	//alert("Button Pressed...");
	processSubmitAndClearMsgText();
	//var s = document.getElementById("msgText").value;
	//console.warn("js: textvalue =" + s);
	//appendTextToTextArea(s);
	xstatusUpdate("[ButtonPress]Sending to server...");
}

function serverWrite(msg){
	if (webSocket == null) {
		openWebsocket();
	}
	webSocket.send(msg);	
}

//get text string, append, and clear box
//Handle <return> and submit button the same way
function processSubmitAndClearMsgText() {
	var line = document.getElementById("msgText").value;
	if (line == null) {
		console.log("null msg box - ignored.");
		return;
		}
	console.log("full-line:" + line);
	// add to the scroll box...
	appendTextToTextArea("local:" + line + '\n'); // temporary...
	//
	serverWrite(line);
	document.getElementById("msgText").value = "";
	//
	// And set the focus to the text area... only necessary when button is pressed...
	document.getElementById("msgText").focus();
}

function appendTextToTextArea(newtext) {
	var old = document.getElementById("statusArea").value;
	document.getElementById("statusArea").value = old + newtext;
}
		
export {openWebSocket, selectButtonPress, keyFilter }
