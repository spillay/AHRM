import { emotionPalette } from '../../config/EmotionConfig.js';
import Color from 'color';
import legend from 'd3-svg-legend';
import userImage from '../../../images/user-generic.png'
import GraphData from '../../BL/GraphData';

var obj = require('../../SPObject');
var d3 = require("d3");

export function SPAnimationGraph(opts) {
    console.log("Init SPGraph");
    //this.data = opts.data;
    this.element = opts.element;
    this.width = this.element.offsetWidth;
    this.height = opts.height;
    this.margin = opts.margin;
    this.parent = opts.parent;
    this.email = opts.email;
    this.date = opts.date;
    this.interval = opts.interval;
    this.level = opts.level;
    this.animationStep = 400;
    this.timerID = 0;
    console.log("+++++++++++++++++++++++++++++++++++++++++++++++", opts);
    this.setData(this.email,this.date,this.interval,this.level);
}

SPAnimationGraph.method("setData", function () {
    var that = this;
    var gd = new GraphData({"id":this.email,"name":this.email,"date":this.date,"interval":this.interval,"level":this.level});
    gd.process().then(res=>{
            console.log("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}",gd.getData())
            that.data = gd.getData()
            that.clean()
            that.draw()
    })
})

SPAnimationGraph.method("getColor", function (emotion) {
    console.log("getcolor");
    var emo = emotionPalette.filter(e => e.emotion == emotion);
    console.log(emo);
    if (emo.length > 0) {
        return emo[0].color;
    } else {
        return Color.gray;
    }
});
SPAnimationGraph.method("diagonalLink", function (d) {
    return "M" + d.source.y + "," + d.source.x
        + "C" + (d.source.y + d.target.y) / 2 + "," + d.source.x
        + " " + (d.source.y + d.target.y) / 2 + "," + d.target.x
        + " " + d.target.y + "," + d.target.x;
})

SPAnimationGraph.method("positionLink", function (d) {
    //console.log("postionLink");
    var offset = 30;

    var midpoint_x = (d.source.x + d.target.x) / 2;
    var midpoint_y = (d.source.y + d.target.y) / 2;

    var dx = (d.target.x - d.source.x);
    var dy = (d.target.y - d.source.y);

    var normalise = Math.sqrt((dx * dx) + (dy * dy));

    var offSetX = midpoint_x + offset * (dy / normalise);
    var offSetY = midpoint_y - offset * (dx / normalise);

    return "M" + d.source.x + "," + d.source.y +
        "S" + offSetX + "," + offSetY +
        " " + d.target.x + "," + d.target.y;
})

SPAnimationGraph.method("positionLinkText", function (d) {
    //console.log("positionLinkText",d);
    // keep the node within the boundaries of the svg
    var midpoint_x = (d.source.x + d.target.x) / 2;
    var midpoint_y = (d.source.y + d.target.y) / 2;

    return "translate(" + midpoint_x + "," + midpoint_y + ")";
})

// move the node based on forces calculations
SPAnimationGraph.method("positionNode", function (d) {
    // console.log("positionNode");
    // keep the node within the boundaries of the svg
    var pos = 20
    if (d.x < 0) {
        d.x = pos
    };
    if (d.y < 0) {
        d.y = pos
    };
    if (d.x > this.width) {
        d.x = this.width - pos
    };
    if (d.y > this.height) {
        d.y = this.height - pos
    };
    return "translate(" + d.x + "," + d.y + ")";
})



// check the dictionary to see if nodes are linked
SPAnimationGraph.method("isConnected", function (a, b) {
    return this.linkedByIndex[a.index + "," + b.index] || this.linkedByIndex[b.index + "," + a.index] || a.index == b.index;
})

// fade nodes on hover
SPAnimationGraph.method("mouseOver", function (opacity, d) {
    var link = this.svg.selectAll(".link");
    var node = this.svg.selectAll(".node");
    const that = this;

    node.style("stroke-opacity", function (o) {
        var thisOpacity = that.isConnected(d, o) ? 1 : opacity;
        return thisOpacity;
    });
    node.style("fill-opacity", function (o) {
        var thisOpacity = that.isConnected(d, o) ? 1 : opacity;
        return thisOpacity;
    });
    // also style link accordingly
    link.style("stroke-opacity", function (o) {
        return o.source === d || o.target === d ? 1 : opacity;
    });
    link.style("stroke", function (o) {
        return o.source === d || o.target === d ? o.source.colour : "#ddd";
    });

})

SPAnimationGraph.method("mouseOut", function () {
    var link = this.svg.selectAll(".link");
    var node = this.svg.selectAll(".node");
    node.style("stroke-opacity", 1);
    node.style("fill-opacity", 1);
    link.style("stroke-opacity", 1);
    link.style("stroke", "#ddd");
})
SPAnimationGraph.method("dragstarted", function (d) {
    if (!d3.event.active) this.simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
})

SPAnimationGraph.method("dragged", function (d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
})

SPAnimationGraph.method("dragended", function (d) {
    if (!d3.event.active) this.simulation.alphaTarget(0);
    d.fx = null;
    d.fy = null;
})

SPAnimationGraph.method("ticked", function () {
    //console.log("this >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ", this);
    //console.log(this.svg);
    var link = this.svg.selectAll(".link");
    var node = this.svg.selectAll(".node");
    var linktext = this.svg.selectAll(".linktext");
    //console.log(node);
    //var text = this.svg.selectAll(".text");
    //console.log(link);
    link.attr("d", (d) => this.positionLink(d));
    linktext.attr("transform", (d) => this.positionLinkText(d));
    node.attr("transform", (d) => this.positionNode(d));
    //text.attr("transform", (d) => this.positionNode(d));
})

SPAnimationGraph.method("initForce", function () {
    console.log("initForce");
    var data = this.data;
    var parent = this.parent;
    var width = this.width;
    var height = this.height;

    this.svg = d3.select(this.element).append('svg');
    this.svg
        .attr("width", width)
        .attr("height", height);


    console.log("width", width, "middle", width / 2);
    this.simulation = d3.forceSimulation()
        .force("link", d3.forceLink().id(function (d) {
            return d.id;
        })
            .strength(0.025))
        .force("charge", d3.forceManyBody().strength(-200))
        .force("collide", d3.forceCollide().radius(12))
        .force("center", d3.forceCenter(width / 2, height / 2))
    //.force("y", d3.forceY(0))
    //.force("x", d3.forceX(0));
    console.log("before link", this.svg);

    var link = this.svg.selectAll(".link")
        .data(data.links)
        .enter()
        .append("g")
        .attr("class", "glink");

    link.append("path")
        .attr("class", "link")
        .attr('stroke', function (d) {
            return "#ddd";
        })
    

    console.log("before node");
    // add the nodes to the graphic
    var node = this.svg.selectAll(".node")
        .data(data.nodes)
        .enter().append("g");

    node.attr("class", "node");

    console.log("before circle");
    // a circle to represent the node
    var circle = node.append("circle")
        .attr("class", "circle")
        .attr("r", 15)
        .attr("fill", function (d) {
            return "gray";
            //return "#008142";
            //return d.colour;
        })



    var images = node.append("svg:image")
        .attr("class", "image")
        .attr("xlink:href", userImage)
        .attr("x", function (d) { return -25; })
        .attr("y", function (d) { return -25; })
        .attr("height", 50)
        .attr("width", 50)
        .on("mouseover", (d) => this.mouseOver(.2, d))
        .on("mouseout", () => this.mouseOut())
        .call(d3.drag()
            .on("start", (d) => this.dragstarted(d))
            .on("drag", (d) => this.dragged(d))
            .on("end", (d) => this.dragended(d)));

 
    node.append("text")
        .attr("dx", 30)
        .attr("dy", ".35em")
        .text(function (d) {
            return d.id;
        })
        .style("stroke", "black")
        .style("stroke-width", 0.5);


    link.append("text")
        .attr("class", "linktext")
        .attr("font-family", "Arial, Helvetica, sans-serif")
        .attr("fill", "Black")
        .attr("color", "red")
        .style("font", "normal 12px Arial")
        .attr("dy", ".35em")
        .attr("text-anchor", "middle")
        .text(function (d) {
            console.log(d.cnt);
            return d.cnt;
        });

    console.log("simulation", this.simulation);
    this.simulation
        .nodes(data.nodes)
        .on("tick", () => this.ticked());

    // add the links to the simulation
    this.simulation
        .force("link")
        .links(data.links);

    // on each tick, update node and link positions

    console.log("before link");
    // build a dictionary of nodes that are linked
    var linkedByIndex = {};
    console.log("after link");
    data.links.forEach(function (d) {
        // console.log(d);
        //console.log(d.source.index + "," + d.target.index);
        linkedByIndex[d.source.index + "," + d.target.index] = 1;
    });
    this.linkedByIndex = linkedByIndex;
    console.log("end");
})

SPAnimationGraph.method("createLegend", function () {
    var domain = [];
    var range = [];
    emotionPalette.forEach(e => {
        //console.log(e);
        domain.push(e.emotion);
        range.push(e.color);
    })
    //console.log(domain);
    //console.log(range);
    var linear = d3.scaleOrdinal()
        .domain(domain)
        .range(range);

    var svg = d3.select("svg");

    svg.append("g")
        .attr("class", "legendLinear")
        .attr("transform", "translate(20,20)");


    var legendLinear = legend.legendColor()
        .shapeWidth(30)
        .cells(10)
        .orient('vertical')
        .scale(linear);

    svg.select(".legendLinear")
        .call(legendLinear);


});
SPAnimationGraph.method("setLevel", function (i) {
    this.level = i;
    this.setData();
})

SPAnimationGraph.method("setPos", function (i) {
    console.log("set Position", i);
    this.timerID = i;
    const that = this;
    d3.selectAll('circle')
        .attr("fill", setColor(i));
    function setColor(index) {
        return function (d, i, a) {
            console.log("emotion length", d.emotions.length);
            if (d.emotions.length > 0 && index < d.emotions.length) {
                var c = that.getColor(d.emotions[index]);
                console.log("color", c)
                d["color"] = c;
                return Color(c);
            } else {
                return Color(d["color"]);
            }
        }
    }
})

SPAnimationGraph.method("draw", function () {
    console.log("drawing SPGraph chart");
    const that = this;
    this.initForce();
    var simulation = this.simulation;
    simulation.restart();
    var svg = this.svg;
    var easeparam = "linear";
    this.createLegend();
    console.log("createDrill")
    this.createDrill();
    d3.select('#advance').on('click', function () {
        console.log("transition");
        var circle = svg.selectAll(".circle");
        circle.transition()
            .duration(100)
            .ease(d3.easeLinear)
            .attrTween("fill", translateFn());
        function translateFn() {
            return function (d, i, a) {
                return function (t) {
                    console.log("|||||||||||||||||||||||||||||||", t, d.emotions.length, d.index, d["color"]);
                    if (d.emotions.length > 0 && d.index < d.emotions.length) {
                        var idx = d.index;
                        d.index = d.index + 1;
                        var c = that.getColor(d.emotions[idx]);
                        console.log("color", c)
                        d["color"] = c;
                        return Color(c);
                    } else {
                        return Color(d["color"]);
                    }
                };
            };
        }
    });
    d3.select('#levels').on('change', function () {
        var selectedLevel = d3.select(this).property("value");
        console.log("selected value",selectedLevel)
        that.level = selectedLevel;
        that.setData();
    });
    d3.select('#play').on('click', function () {
        simulation.slowMotion = true;
        simulation.fullSpeed = false;
        simulation.restart();
    });
    d3.select('#stop').on('click', function () {
        console.log("in stop");
        if (simulation) {
            simulation.stop();
        }
        clearInterval(that.intervalID);
    })
    d3.select('#slow').on('click', function () {
        console.log("play");
        var i = that.timerID,
            oRange = document.querySelector('input[type=range]');

        console.log(oRange.max);
        var index = -1;
        that.intervalID = setInterval(function () {
            oRange.value = i;
            oRange.style.fontSize = i + 'px';
            var output = document.querySelector('#output');
            output.setAttribute("value","at " + i + "th hour")
            i++;
            that.timerID = i;
            
            if (i>oRange.max){
                clearInterval(that.intervalID)
            }
            index = index + 1;
            d3.selectAll('circle')
                .attr("fill", setColor(index))
        }, 1000);
        function setColor(index) {
            return function (d, i, a) {
                console.log("emotion length", d.emotions.length);
                if (d.emotions.length > 0 && index < d.emotions.length) {
                    var c = that.getColor(d.emotions[index]);
                    console.log("color", c)
                    d["color"] = c;
                    return Color(c);
                } else {
                    return Color(d["color"]);
                }
            }
        }
    });
    d3.select('#reset').on('click', function () {
        console.log("reset", simulation);
        if (simulation) {
            simulation.stop();
        }
        var oRange = document.querySelector('input[type=range]');
        oRange.value = 0;
        console.log("reset", that.intervalID);
        clearInterval(that.intervalID)
        that.data.nodes.forEach(n => {
            n["index"] = 0;
            n["color"] = "gray";
        })
        that.timerID = 0;
        console.log(that.data);
        that.clean();
        that.initForce();
        that.createLegend();
    });

});

SPAnimationGraph.method('init', function () {
    console.log(this.data);
    this.data.forEach(function (d) {
        //d.count = +d.count;
        d.enabled = true;
    });

});
SPAnimationGraph.method('clean', function () {
    clearInterval(this.intervalID)
    d3.select("svg").remove();
    d3.select(this.element).selectAll("*").remove();
});
SPAnimationGraph.method('createDrill', function () {
    var that = this;
    d3.selectAll('.node').on('click', function (d) {
        console.log('Drill Down ',d.name);
        that.email = d.name;
        that.setData();
    });
    this.contextMenu();
});


SPAnimationGraph.method('contextMenu', function () {
    console.log("svg")
    
      
    d3.selectAll('.node').on("contextmenu", function(data, index) {
        console.log(data.x,"in context menu")
        
        var width = 100,height = 30,margin=0.1;
        var position = d3.mouse(this);
        // var x = 100;
        // var y = 300;
        var x = data.x;
        var y = data.y;

        var cm = d3.select("svg").append("g")
                        .attr("class", "context-menu")
                        .attr("id","person-context");
        
        cm.style('position', 'absolute')
            .attr('x',x)
            .attr('y',y)
            //.attr("transform", "translate("+x+","+y+")")
            .style('left', position[0] + "px")
            .style('top', position[1] + "px")
            .style('display', 'block');
        cm.on("mouseout",function(d,i){
            console.log(d,i,d3.select(this))
            d3.select(this).remove()
        })
        var cmenu = cm.append('g').attr('class', 'menu-entry')
        
        cmenu.append('rect')
            .attr('x', x)
            //.attr('y', function(d, i){ return y + (i * height); })
            .attr('y', y)
            .attr('width', width)
            .attr('height', height)
        
        cmenu.append("text")
            .text("hello")
            .attr('x', x)
            .attr('y', y)
            //.attr('y', function(d, i){ return y + (i * height); })
            .attr('dy', height - margin / 2)
            .attr('dx', margin)

        console.log("contextmenu2",cm)
        d3.event.preventDefault();
    })
})

SPAnimationGraph.method('contextMenu1', function () {
    d3.selectAll('.node').on("contextmenu", function(data, index) {
        var position = d3.mouse(this);
        console.log("xposit",d3.select(this),position);
        console.log(this,data,position[0],data,position[1])
        var x = position[0];
        var y = position[1];
        var width = 1500,height = 1500,margin=0.1;
        console.log("context",d3.select('#person-context'))
        var cmenu = d3.select('#person-context')
          .attr('x', x)
          .attr('y', y)
          .style('position', 'fixed')
          .style('left', position[0] + "px")
          .style('top', position[1] + "px")
          .style('display', 'inline-block')
          .append('g').attr('class', 'menu-entry')

        cmenu.append('rect')
            .attr('x', x)
            //.attr('y', function(d, i){ return y + (i * height); })
            .attr('y', y)
            .attr('width', width)
            .attr('height', height)
            
        cmenu.append("text")
            .text("hello")
            .attr('x', x)
            .attr('y', y)
            //.attr('y', function(d, i){ return y + (i * height); })
            .attr('dy', height - margin / 2)
            .attr('dx', margin)
            
        d3.event.preventDefault();
    });
})