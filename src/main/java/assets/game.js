var isSetup = true;
var placedShips = 0;
var game;
var shipType;
var vertical;
document.getElementById("reset_button").addEventListener("click", resetPage);
document.getElementById("help").addEventListener("click", help);
Array.from(document.getElementsByClassName("increaseSize")).forEach((butt) => butt.addEventListener("click", resize));
Array.from(document.getElementsByClassName("decreaseSize")).forEach((butt) => butt.addEventListener("click", resize));

function resetPage(){
    var result = confirm("Reload the game?");
    if(result == true){
        location.reload();
    }
}

function help(){
    alert("How to play the game:\n 1. Place all 3 ships on your board\n 2. Click enemy board to attack\n 3. Game ends when all ships for 1 player are sunk\n\n Have fun!");
}

function resize(){
    var opCells = document.getElementsByClassName("opCell");
    var playerCells = document.getElementsByClassName("playerCell");
    if(this.classList.contains("opponent")){
        resizeLoop(opCells, this);
    }
    if(this.classList.contains("player")){
        resizeLoop(playerCells, this);
    }
}

function resizeLoop(cells, button){
    for (var i = 0, max = cells.length; i < max; i++) {
         var currWidth = parseInt(cells[i].style.width);
         if(!currWidth){
            currWidth = 30;
         }
         if(button.classList.contains("increaseSize")){
            var newWidth = currWidth + 10 + "px";
         }
         if(button.classList.contains("decreaseSize")){
            var newWidth = currWidth - 10 + "px";
         }
         cells[i].style.width = newWidth;
         cells[i].style.height = newWidth;
    }
}

function makeGrid(table, isPlayer) {
    let row1 = document.createElement('tr');
    for (i=0; i<11; i++){
         var itoa = String.fromCharCode(i+64);
         if(i == 0){
            itoa = String.fromCharCode(32);
         }
         let letters = document.createElement("P");
         letters.classList.add("letters");
         if(isPlayer){
            letters.classList.add("playerCell");
         } else{
            letters.classList.add("opCell");
         }
         var t1 = document.createTextNode(itoa);
         letters.appendChild(t1);
         row1.appendChild(letters);
    }
    table.parentNode.insertBefore(row1,table.parentNode.firstChild);

    for (i=0; i<10; i++) {
        let row = document.createElement('tr');

         let numbers = document.createElement("P");
         numbers.classList.add("letters");
         if(isPlayer){
            numbers.classList.add("playerCell");
         }
         else {
            numbers.classList.add("opCell");
         }
         var t = document.createTextNode(i+1);
         numbers.appendChild(t);
         row.appendChild(numbers);
        for (j=0; j<10; j++) {
            let column = document.createElement('td');
            if(isPlayer){
                column.classList.add("playerCell");
            } else{
                column.classList.add("opCell");
            }
            column.addEventListener("click", cellClick);
            row.appendChild(column);
        }
        table.appendChild(row);
    }
}

function markHits(board, elementId, surrenderText) {
    var attacker;
    if(elementId == "player"){
        attacker = "computer";
    }
    else if(elementId == "opponent"){
        attacker ="player";
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
        else if (attack.result === "SUNK"){
            className = "sink"
            }
        else if (attack.result === "SURRENDER"){
            alert(surrenderText);
            }
        document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
    });
    if(!isSetup){
        document.getElementById("outputBox").value=document.getElementById("outputBox").value +  "\n" + attacker + " " + className +"!";
        document.getElementById("outputBox").scrollTop = document.getElementById("outputBox").scrollHeight;
    }
}

function redrawGrid() {
    Array.from(document.getElementById("opponent").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("player").childNodes).forEach((row) => row.remove());
    document.getElementById("leftBoard").firstChild.remove();
    document.getElementById("rightBoard").firstChild.remove();
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    if (game === undefined) {
        return;
    }

    game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
    }));
    markHits(game.opponentsBoard, "opponent", "You won the game, nice job");
    markHits(game.playersBoard, "player", "You lost the game, ouch");
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
        sendXhr("POST", "/place", {game: game, shipType: shipType, x: row, y: col, isVertical: vertical}, function(data) {
            Array.from(document.getElementsByClassName("ship")).forEach((ship) => ship.classList.remove("selected"));
            game = data;
            redrawGrid();
            document.getElementById("outputBox").value=document.getElementById("outputBox").value +  "\nYou placed " + shipType;
            document.getElementById("outputBox").scrollTop = document.getElementById("outputBox").scrollHeight;
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
                document.getElementById("outputBox").value=document.getElementById("outputBox").value + "\n\nERROR CAN NOT PLACE SHIP!";
            } else {
                document.getElementById("outputBox").value=document.getElementById("outputBox").value + "\n\nERROR CAN NOT COMPLETE THAT ATTACK!";
            }
            document.getElementById("outputBox").classList.add("errorText");
            document.getElementById("outputBox").scrollTop = document.getElementById("outputBox").scrollHeight;
            return;
        }
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

var globalRow
var globalCol
var globalSize
function place(size) {
    return function() {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
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
       globalCol = col;
       globalRow = row;
       globalSize = size;
    }
}

function initGame() {
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    document.getElementById("minesweeper").addEventListener("click", function(e) {
        Array.from(document.getElementsByClassName("ship")).forEach((ship) => ship.classList.remove("selected"));
        this.classList.add("selected");
        shipType = "MINESWEEPER";
       registerCellListener(place(2));
    });
    document.getElementById("destroyer").addEventListener("click", function(e) {
        Array.from(document.getElementsByClassName("ship")).forEach((ship) => ship.classList.remove("selected"));
        this.classList.add("selected");
        shipType = "DESTROYER";
       registerCellListener(place(3));
    });
    document.getElementById("battleship").addEventListener("click", function(e) {
        Array.from(document.getElementsByClassName("ship")).forEach((ship) => ship.classList.remove("selected"));
        this.classList.add("selected");
        shipType = "BATTLESHIP";
       registerCellListener(place(4));
    });
    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });
};

window.onkeyup = function(e) {
    var key = e.keycode ? e.keycode : e.which;

    if(key == 82){
        document.getElementById("is_vertical").checked = !(document.getElementById("is_vertical").checked);

        Array.from(document.getElementsByClassName("placed")).forEach((ship) => ship.classList.remove("placed"));

        //copied from place above
        let row = globalRow;
        let col = globalCol;
        let size = globalSize;
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
    }
}
