import TSNE from '../../BL/TSNE';

var obj = require('../../SPObject');
var d3 = require("d3");



export function SPTGraph(opts) {
    console.log("Init SPTGraph");
    this.data = opts.data;
    this.dists = opts.dists;
    this.costThreshold = opts.costThreshold;
    this.element = opts.element;
    this.width = opts.width;
    this.height = opts.height;
    this.margin = opts.margin;
    this.parent = opts.parent;
    console.log("+++++++++++++++++++++++++++++++++++++++++++++++",this.element);
    this.color = d3.scaleOrdinal(d3.schemeCategory10);
    this.centerx = d3.scaleLinear()
        .range([this.width / 2 - this.height / 2 + this.margin, this.width / 2 + this.height / 2 - this.margin]);
    this.centery = d3.scaleLinear()
        .range([this.margin, this.height - this.margin]);
    this.draw();

}
SPTGraph.method("setData", function (data) {
    this.data = data;
    this.data.forEach(function (d) {
        console.log(d.label);
    });
    this.clean();
    this.draw();
});

SPTGraph.method("isIntersect",function(point, circle) {
    return Math.sqrt((point.x-circle.x) ** 2 + (point.y - circle.y) ** 2) < circle.r;
})
  
SPTGraph.method("onClick", function (mouse) {
   console.log("onclick");
   console.log(this)
   //console.log(that2);
   console.log(mouse)
   console.log(this.data)
   this.data.forEach(circle => {
    if (this.isIntersect({x:mouse[0],y:mouse[1]}, circle)) {
      alert('click on node: ' + circle.name);
    }
  });
});

SPTGraph.method("drawCanvas", function (canvas,nodes) {
    //console.log("drawCanvas");
    let context = canvas.node().getContext("2d");
    context.clearRect(0, 0, context.canvas.width, context.canvas.height);

    for (var i = 0, n = nodes.length; i < n; ++i) {
        var node = nodes[i];
        //console.log("in draw node: ",node.x,node.y,node.r,node.color)
        context.beginPath();
        context.moveTo(node.x, node.y);
        //context.arc(node.x, node.y, node.r, 0, 2 * Math.PI);
        context.arc(node.x, node.y, node.r, 0, 2 * Math.PI);
        context.closePath();
        context.lineWidth = 0.5;
        context.fillStyle = node.color;
        context.fill();
    }
    

})

SPTGraph.method("draw", function () {
    console.log("drawing SPTGraph chart");

    var parent = this.parent;
    var width = this.width;
    var height = this.height;
    var margin = this.margin;
    var dists = this.dists;
    var data = this.data;
    var centerx = this.centerx;
    var centery = this.centery;
    var that = this;
    
    

    const model = new TSNE({
        dim: 2,
        perplexity: 30,
    });
    model.initDataDist(dists)

    var canvas = d3.select(this.element).append("canvas")
        .attr("class","tsne-canvas")
        .attr("width", width)
        .attr("height", height);

    canvas.style.border = "1px solid red";

    canvas.on("click", function(){
        var mouse = d3.mouse(this);
        that.onClick(mouse);
    })
    
    var force = d3.forceSimulation(data.map(d => (d.x = width / 2, d.y = height / 2, d)))
    var tseForce = force
        .alphaDecay(0.0) // default would be 0.005
        .alpha(0.1)
        .force('tsne', function (alpha) {
            // every time you call this, solution gets better
            //console.log("alpha",alpha);
            var cost = model.step();
            //console.log("cost",cost)
            // Y is an array of 2-D points that you can plot
            let pos = model.getSolution();

            centerx.domain(d3.extent(pos.map(d => d[0])));
            centery.domain(d3.extent(pos.map(d => d[1])));

            data.forEach((d, i) => {
                d.x += alpha * (centerx(pos[i][0]) - d.x);
                d.y += alpha * (centery(pos[i][1]) - d.y);
            });
            d3.select('#costOuput').attr("value",cost)
            if (cost < that.costThreshold){
                tseForce.stop();
            }
        })
    var collideForce = force.force('collide', d3.forceCollide().radius(d => 1.5 + d.r))
        .on('tick', function () {

            let nodes = data.map((d, i) => {
                return {
                    x: d.x,
                    y: d.y,
                    r: d.r,
                    color: d.color,
                    name: d.name
                };
            }); 
            //console.log("before draw",nodes)
            that.drawCanvas(canvas, nodes);

        });
    d3.select('#stop').on("click",function(){
        console.log("stop clicked")
        tseForce.stop();    
    })
    d3.select('#pause').on("click",function(){
        console.log("pause clicked")
        tseForce.stop();    
        console.log(d3.select('#resume'))
        d3.select('#resume').attr('disabled', null);
    })
    d3.select('#resume').on("click",function(){
        console.log("resume clicked")
        // Cannot simply resume   
    })
});


SPTGraph.method('clean', function () {
    console.log("in clean")
    d3.select("svg").remove();
    d3.select(this.element).selectAll("*").remove();
});
