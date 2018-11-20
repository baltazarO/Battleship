
function defined (a, b) {
  return a != null ? a : b;
}

const targetFPS = 1000 / 60;

function Node(garden) {
  this.garden = garden;
  this.reset();
}

  Node.prototype.reset = function({x, y, vx, vy, m} = {}) {
    this.x = defined(x, Math.random() * this.garden.width);
    this.y = defined(y, Math.random() * this.garden.height);
    this.vx = defined(vx, Math.random() * 0.5 - 0.25);
    this.vy = defined(vy, Math.random() * 0.5 - 0.25);
    this.m = defined(m, Math.random() * 2.5 + 0.5);
  }

  Node.prototype.addForce = function(force, direction) {
    this.vx += force * direction.x / this.m;
    this.vy += force * direction.y / this.m;
  }

  Node.prototype.distanceTo = function(node) {
    const x = node.x - this.x;
    const y = node.y - this.y;
    const total = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

    return { x, y, total };
  }

  Node.prototype.update = function(deltaTime) {
    this.x += this.vx * deltaTime / targetFPS;
    this.y += this.vy * deltaTime / targetFPS;

    if (this.x > this.garden.width + 50 || this.x < -50 || this.y > this.garden.height + 50 || this.y < -50) {
      // if node over screen limits - reset to a init position
      this.reset();
    }
  }

  Node.prototype.squaredDistanceTo = function(node) {
    return (node.x - this.x) * (node.x - this.x) + (node.y - this.y) * (node.y - this.y);
  }

  Node.prototype.collideTo = function(node) {
    node.vx = node.m * node.vx / (this.m + node.m) + this.m * this.vx / (this.m + node.m);
    node.vy = node.m * node.vy / (this.m + node.m) + this.m * this.vy / (this.m + node.m);

    this.reset();
  }

  Node.prototype.render = function() {
    this.garden.ctx.beginPath();
    this.garden.ctx.arc(this.x, this.y, this.getDiameter(), 0, 2 * Math.PI);
    this.garden.ctx.fill();
  }

  Node.prototype.getDiameter = function() {
    return this.m;
  }

const { devicePixelRatio = 1, requestAnimationFrame } = window;

function NodeGarden() {
    this.nodes = [];
    this.container = container;
    this.canvas = document.createElement('canvas');
    this.ctx = this.canvas.getContext('2d');
    this.started = false;
    this.nightmode = false;

    if (devicePixelRatio && (devicePixelRatio !== 1)) {
      // if retina screen, scale canvas
      this.canvas.style.transform = 'scale(' + 1 / devicePixelRatio + ')';
      this.canvas.style.transformOrigin = '0 0';
    }
    this.canvas.id = 'nodegarden';

      window.addEventListener('mousedown', (e) => {
      /*e.preventDefault();*/
      const bcr = container.getBoundingClientRect();
      const scrollPos = {
        x: window.scrollX,
        y: window.scrollY
      };
      // Add mouse node
      const mouseNode = new Node(this);
      mouseNode.x = (e.pageX - scrollPos.x - bcr.left) * devicePixelRatio;
      mouseNode.y = (e.pageY - scrollPos.y - bcr.top) * devicePixelRatio;
      mouseNode.m = 15;

      mouseNode.update = () => {};
      mouseNode.reset = () => {};
      mouseNode.render = () => {};

      this.nodes.unshift(mouseNode);

      window.addEventListener('mousemove', (e) => {
        mouseNode.x = (e.pageX - scrollPos.x - bcr.left) * devicePixelRatio;
        mouseNode.y = (e.pageY - scrollPos.y - bcr.top) * devicePixelRatio;
      });

      window.addEventListener('mouseup', (e) => {
        for (let i = 0; i < this.nodes.length; i++) {
          if (this.nodes[i] === mouseNode) {
            this.nodes.splice(i--, 1);
            break;
          }
        }
      });
    });

    this.container.insertBefore(this.canvas,this.container.childNodes[0]);
    this.resize();
}

  NodeGarden.prototype.start = function() {
    if (!this.playing) {
      this.playing = true;
      this.render(true);
    }
  }

  NodeGarden.prototype.stop = function() {
    if (this.playing) {
      this.playing = false;
    }
  }

  NodeGarden.prototype.resize = function() {
    this.width = this.container.scrollWidth * devicePixelRatio;
    var actualHeight = Math.max( document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.offsetHeight);
    this.height = actualHeight * devicePixelRatio;
    this.area = this.width * this.height;

    // calculate nodes needed
    this.nodes.length = Math.sqrt(this.area) / 25 | 0;

    // set canvas size
    this.canvas.width = this.width;
    this.canvas.height = this.height;

    //color the nodes
    //this.ctx.fillStyle = '#000000';
    this.ctx.fillStyle = '#FFFFFF';

    // create nodes
    for (let i = 0; i < this.nodes.length; i++) {
      if (this.nodes[i]) {
        continue;
      }
      this.nodes[i] = new Node(this);
    }
  }

  NodeGarden.prototype.render = function(start, time) {
    if (!this.playing) {
      return;
    }

    if (start) {
      requestAnimationFrame((time) => {
        this.render(true, time);
      });
    }

    const deltaTime = time - (this.lastTime || time);

    this.lastTime = time;

    // clear canvas
    this.ctx.clearRect(0, 0, this.width, this.height);

    // update links
    for (let i = 0; i < this.nodes.length - 1; i++) {
      const nodeA = this.nodes[i];
      for (let j = i + 1; j < this.nodes.length; j++) {
        const nodeB = this.nodes[j];
        const squaredDistance = nodeA.squaredDistanceTo(nodeB);

        // calculate gravity force
        const force = 3 * (nodeA.m * nodeB.m) / squaredDistance;

        const opacity = force * 100;

        if (opacity < 0.025) {
          continue;
        }

        if (squaredDistance <= (nodeA.m / 2 + nodeB.m / 2) * (nodeA.m / 2 + nodeB.m / 2)) {
          // collision: remove smaller or equal - never both of them
          if (nodeA.m <= nodeB.m) {
            nodeA.collideTo(nodeB);
          } else {
            nodeB.collideTo(nodeA);
          }
          continue;
        }

        const distance = nodeA.distanceTo(nodeB);

        // calculate gravity direction
        const direction = {
          x: distance.x / distance.total,
          y: distance.y / distance.total
        };

        // draw gravity lines
        this.ctx.beginPath();
        //this.ctx.strokeStyle = 'rgba(0,0,0,' + (opacity < 1 ? opacity : 1) + ')';
        this.ctx.strokeStyle = 'rgba(255,255,255,' + (opacity < 1 ? opacity : 1) + ')';
        this.ctx.moveTo(nodeA.x, nodeA.y);
        this.ctx.lineTo(nodeB.x, nodeB.y);
        this.ctx.stroke();

        nodeA.addForce(force, direction);
        nodeB.addForce(-force, direction);
      }
    }

    // render and update nodes
    for (let i = 0; i < this.nodes.length; i++) {
      this.nodes[i].render();
      this.nodes[i].update(deltaTime || 0);
    }
  }

function initGarden() {
const pixelRatio = window.devicePixelRatio;
const $container = document.getElementById('container');

const nodeGarden = new NodeGarden($container);

// start simulation
nodeGarden.start();

let resetNode = 0;

$container.addEventListener('click', (e) => {
  const bcr = $container.getBoundingClientRect();
  const scrollPos = {
    x: window.scrollX,
    y: window.scrollY
  };
  resetNode++;
  if (resetNode > nodeGarden.nodes.length - 1) {
    resetNode = 1;
  }
  nodeGarden.nodes[resetNode].reset({
    x: (e.pageX - scrollPos.x - bcr.left) * pixelRatio,
    y: (e.pageY - scrollPos.y - bcr.top) * pixelRatio,
    vx: 0,
    vy: 0
  });
});

window.addEventListener('resize', () => {
  nodeGarden.resize();
});

Array.from(document.getElementsByClassName("increaseSize")).forEach((butt) => butt.addEventListener("click", () => {
    nodeGarden.resize();
}));
Array.from(document.getElementsByClassName("decreaseSize")).forEach((butt) => butt.addEventListener("click", () => {
    nodeGarden.resize();
}));
var toggle = 1;
document.getElementById("toggleGarden").addEventListener("click",function(){
    if(toggle === 1){
        nodeGarden.stop();
        nodeGarden.resize();
        toggle = 0;
    } else {
        nodeGarden.start();
        nodeGarden.resize();
        toggle = 1;
    }
});
}
