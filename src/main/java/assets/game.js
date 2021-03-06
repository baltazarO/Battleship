var isSetup = true;
var isLaser = false;
var isMove = false;
var placedShips = 0;
var game;
var shipType;
var vertical;
var captIsLeft = false;
var rotation = 1;
document.getElementById("reset_button").addEventListener("click", function(){resetPage("Reload the game?")});
document.getElementById("help").addEventListener("click", help);
document.getElementById("is_vertical").addEventListener("click",incrementRotation);
Array.from(document.getElementsByClassName("increaseSize")).forEach((butt) => butt.addEventListener("click", resize));
Array.from(document.getElementsByClassName("decreaseSize")).forEach((butt) => butt.addEventListener("click", resize));

function sound(src) {
    let elem = document.getElementById(src);
    elem.currentTime = 0;
    elem.play();
}

function SetVolume(val) {
    var elems = document.getElementsByTagName("audio");
    for(var i=0; i<elems.length; i++){
        elems[i].volume = val / 100;
    }
}

function doOutputResult(message) {
    document.getElementById("outputBox").value=document.getElementById("outputBox").value +  "\n\n" + message +"!";
    document.getElementById("outputBox").scrollTop = document.getElementById("outputBox").scrollHeight;
}

function resetPage(text){
	var result = confirm(text);
	if(result == true){
		location.reload();
	}
}

function help(){
    var client = new XMLHttpRequest();
    client.addEventListener("load", function(event) {
        alert(client.responseText);
    });
    client.open('GET', '/assets/help.txt');
    client.send();
}

function resize(){
	if(this.classList.contains("opponent")){
		var table = document.getElementById("opponent");
		var row1 = document.getElementById("row1opponent");
		var nums = document.getElementById("rowMarkersContainerOpponent");
	}
	if(this.classList.contains("player")){
		var table = document.getElementById("player");
		var row1 = document.getElementById("row1player");
		var nums = document.getElementById("rowMarkersContainerPlayer");
	}
	let currWidth = parseInt(table.style.width,10);
	let currHeight = parseInt(table.style.height,10);
	if(currWidth === 0){ currWidth = table.offsetWidth; }
	if(currHeight === 0){ currHeight = table.offsetHeight; }
	if(this.classList.contains("increaseSize")){
		var newWidth = currWidth + 100 + "px";
		var divWidth = (currWidth + 100) / 10 + "px";
	} else {
		var newWidth = currWidth - 100 + "px";
		var divWidth = (currWidth - 100) / 10 + "px";
	}
	table.style.width = newWidth;
	table.style.height = newWidth;
	row1.style.width = newWidth;
	Array.from(nums.children).forEach((number) => number.style.height = divWidth);
}

function makeGrid(table) {
	for (i=0; i<10; i++) {
		let row = document.createElement('tr');
		for (j=0; j<10; j++) {
			let column = document.createElement('td');
			column.addEventListener("click", cellClick);
			row.appendChild(column);
		}
		table.appendChild(row);
	}
}

function markHits(board, elementId, surrenderText) {
	var attacker;
	if(elementId === "player"){
		attacker = document.getElementById("opponentName").value;
	}
	else if(elementId === "opponent"){
		attacker = document.getElementById("playerName").value;
	}
	let className;
	document.getElementById("outputBox").classList.remove("errorText");
	board.attacks.forEach((attack) => {
		//let className;
		if (attack.result === "MISS"){
			className = "miss";
			}
		else if (attack.result === "HIT"){
			className = "hit";
			}
		else if(attack.result === "CRITICAL"){
			className = "critical";
		    }
		else if (attack.result === "SUNK"){
			className = "sink"
			}
		else if (attack.result === "SURRENDER"){
		    sound("surrender.mp3");
			resetPage(surrenderText);
			}
	    else { className = "invalid"};
		document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
	});
	if(!isSetup && !isMove){
	    if(elementId === "opponent"){
	        sound(className + ".mp3");
	    }
	    doOutputResult(attacker + " " + className);
	}
}

function redrawGrid() {
	Array.from(document.getElementById("opponent").childNodes).forEach((row) => row.remove());
	Array.from(document.getElementById("player").childNodes).forEach((row) => row.remove());
	makeGrid(document.getElementById("opponent"));
	makeGrid(document.getElementById("player"));
	if (game === undefined) {
		return;
	}

	game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
		document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
		if(square.captains){
		    document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("captains");
		}
	}));
	markHits(game.opponentsBoard, "opponent", "You won the game, nice job " + document.getElementById("playerName").value + "\nClick ok to restart");
	markHits(game.playersBoard, "player", "You lost to " + document.getElementById("opponentName").value + ", ouch\nClick ok to restart");
}

var oldListener;
function registerCellListener(f) {
	let el = document.getElementById("player");
	for (i=0; i<10; i++) {
		for (j=0; j<10; j++) {
			let cell = el.rows[i].cells[j];
			cell.removeEventListener("mouseover", oldListener);
			cell.removeEventListener("mouseout", oldListener);
			cell.addEventListener("mouseover", f);
			cell.addEventListener("mouseout", f);
		}
	}
	oldListener = f;
}

function cellClick() {
	let row = this.parentNode.rowIndex + 1;
	let col = String.fromCharCode(this.cellIndex + 65);

	if (isSetup) {
		sendXhr("POST", "/place", {game: game, shipType: shipType, x: row, y: col, isVertical: vertical, captIsLeft: captIsLeft}, function(data) {
			Array.from(document.getElementsByClassName("ship")).forEach((ship) => ship.classList.remove("selected"));
			game = data;
			redrawGrid();
			sound("place.mp3");
			doOutputResult(document.getElementById("playerName").value + " placed " + shipType);
			placedShips++;
			if (placedShips == 3) {
				isSetup = false;
				registerCellListener((e) => {});
			}
		});
	} else {
		sendXhr("POST", "/attack", {game: game, x: row, y: col}, function(data) {
			game = data;
			redrawGrid();
		})
	}
}

function sendXhr(method, url, data, handler) {
	var req = new XMLHttpRequest();
	req.addEventListener("load", function(event) {
		if (req.status != 200) {
			if(isSetup){
				doOutputResult("\nERROR CAN NOT PLACE SHIP!");
			} else {
				doOutputResult("\nERROR CAN NOT COMPLETE THAT ATTACK!");
			}
			sound("error.mp3");
			document.getElementById("outputBox").classList.add("errorText");
			return;
		}
		handler(JSON.parse(req.responseText));
	});
	req.open(method, url);
	req.setRequestHeader("Content-Type", "application/json");
	req.send(JSON.stringify(data));
}

function place(size) {
	return function() {
		let row = this.parentNode.rowIndex;
		let col = this.cellIndex;
        assignPlaced(size, row, col);
	}
}

/*New Function derived from the place function, does the same action but makes it easier to set the row and col values when rotating*/
function assignPlaced(size, row, col){
		vertical = document.getElementById("is_vertical").checked;
		let table = document.getElementById("player");
		for (let i=0; i<size; i++) {
			let cell;
			if(vertical) {
				let tableRow = table.rows[row+i];
				if (tableRow === undefined) {
					// ship is over the edge; let the back end deal with it
					break;
				}
				cell = tableRow.cells[col];
			} else {
				cell = table.rows[row].cells[col+i];
			}
			if (cell === undefined) {
				// ship is over the edge; let the back end deal with it
				break;
			}
			cell.classList.toggle("placed");
		}
		assignCQ(size, row, col);
}

function assignCQ(size, row, col){
    let table = document.getElementById("player");
	let cell;
	if(vertical){
		if(captIsLeft && size === 2){ cell = table.rows[row].cells[col]; }
        else if (!captIsLeft && size === 4){ cell = table.rows[row+2].cells[col]; }
        else { cell = table.rows[row+1].cells[col]; }
	} else {
		if(captIsLeft && size === 2){ cell = table.rows[row].cells[col]; }
        else if (!captIsLeft && size === 4){ cell = table.rows[row].cells[col+2]; }
        else { cell = table.rows[row].cells[col+1]; }
	}
	if(cell != undefined && size != undefined){cell.classList.toggle("cq");}
}

/*Give this ship type in all lower case!*/
function initShip(ship, size){
    document.getElementById(ship).addEventListener("click", function(e) {
        Array.from(document.getElementsByClassName("ship")).forEach((ship) => ship.classList.remove("selected"));
        if(isSetup){
            this.classList.add("selected");
            shipType = ship.toUpperCase();
            registerCellListener(place(size));
        }
    });
}

function initGame() {
	makeGrid(document.getElementById("opponent"));
	makeGrid(document.getElementById("player"));
	initShip("minesweeper", 2);
	initShip("destroyer", 3);
	initShip("battleship", 4);
	sendXhr("GET", "/game", {}, function(data) {
		game = data;
	});
};

/*rotates ship when scrolling*/
document.getElementById('player').addEventListener("wheel", function(){
    if(isSetup){
        incrementRotation();
        rotateShip();
    }
});
/*Detects the 'r' key and invokes rotation before placement*/
window.onkeyup = function(e) {
	var key = e.keycode ? e.keycode : e.which;
	if(key == 82 && isSetup){
		incrementRotation();
		rotateShip();
	}
}

/*Counts to 4, moving the CQ for the even sized ships every 2nd rotation*/
function incrementRotation(){
    if(rotation < 2){
        captIsLeft = false;
    } else {
        captIsLeft = true;
    }
    if(rotation < 3){
        rotation++;
    } else {
        rotation = 0;
    }
}

/*calls place to reassign the classes that display the placement ui when rotating since the even listener is not triggered*/
function rotateShip(){
	document.getElementById("is_vertical").checked = !(document.getElementById("is_vertical").checked);

	Array.from(document.getElementsByClassName("placed")).forEach((ship) => ship.classList.remove("placed"));
	Array.from(document.getElementsByClassName("cq")).forEach((ship) => ship.classList.remove("cq"));

	if(document.querySelectorAll(":hover")[7] != null){
	    let myCell = document.querySelectorAll( ":hover" )[7];
	    if(myCell.parentNode.parentNode == document.getElementById("player")){
	        let row = myCell.parentNode.rowIndex;
	        let col = myCell.cellIndex;
	        let size;
	        if(shipType == "MINESWEEPER"){
	            size = 2;
	        } else if(shipType == "DESTROYER"){
	            size = 3;
	        } else if(shipType == "BATTLESHIP"){
	            size = 4;
	        }
            assignPlaced(size, row, col);
        }
    }
}

/*MOVING FLEET BUTTON FUNCTIONS******************************************************************/
function requestMove(moveDirection) {
    sendXhr("POST", "/move", {game: game, dir: moveDirection}, function(data) {
        game = data;
        isMove = true;
        redrawGrid();
        isMove = false;
    })
}

Array.from(document.getElementsByClassName("moveButton")).forEach((butt) => butt.style.visibility = "hidden");
document.getElementById("movePlayerFleetCenter").addEventListener("click", function(){
    Array.from(document.getElementsByClassName("moveButton")).forEach((butt) => {
        if(butt.style.visibility == "hidden"){
            butt.style.visibility = "visible";
        }
        else{
            butt.style.visibility = "hidden";
        }
    });
});
document.getElementById("movePlayerFleetWest").addEventListener("click", function(){ requestMove(0); });
document.getElementById("movePlayerFleetNorth").addEventListener("click", function(){ requestMove(1); });
document.getElementById("movePlayerFleetEast").addEventListener("click", function(){ requestMove(2); });
document.getElementById("movePlayerFleetSouth").addEventListener("click", function(){ requestMove(3); });
