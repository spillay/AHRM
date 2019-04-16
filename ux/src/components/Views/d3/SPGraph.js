import userImage from '../../../images/user-male-sm.png'
var obj = require('../../SPObject');
var d3 = require("d3");


export function SPGraph(opts) {
    console.log("Init SPGraph");
    this.data = opts.data;
    this.element = opts.element;
    this.width = opts.width;
    this.height = opts.height;
    this.margin = opts.margin;
    this.parent = opts.parent;
    console.log("+++++++++++++++++++++++++++++++++++++++++++++++",this.element);
    this.color = d3.scaleOrdinal(d3.schemeCategory20);
    this.draw();

}
SPGraph.method("setData", function (data) {
    this.data = data;
    this.data.forEach(function (d) {
        console.log(d.label);
    });
    this.clean();
    this.draw();
});

SPGraph.method("setMenu", function (elem) {
    var menus = ["Analyze Employee", "Graph Employee"];
    elem.on('contextmenu', function (data, index) {
        d3.selectAll('.context-menu').data([1])
            .enter()
            .append('div')
            .attr('class', 'context-menu');
        // close menu
        d3.select('body').on('click.context-menu', function () {
            d3.select('.context-menu').style('display', 'none');
        });
        // this gets executed when a contextmenu event occurs
        d3.selectAll('.context-menu')
            .html('')
            .append('ul')
            .selectAll('li')
            .data(menus).enter()
            .append('li')
            .on('click', function (d) { 
                console.log(d);console.log(data,index); 
                d3.select('.context-menu').style('display', 'none');
                return d; 
            })
            .text(function (d) { return d; });

        d3.select('.context-menu').style('display', 'none');
        // show the context menu
        d3.select('.context-menu')
            .style('left', (d3.event.pageX - 2) + 'px')
            .style('top', (d3.event.pageY - 2) + 'px')
            .style('display', 'block');
        d3.event.preventDefault();
    });
})

SPGraph.method("draw", function () {
    console.log("drawing SPGraph chart");
    var data = this.data;

    var parent = this.parent;
    var width = this.width;
    var height = this.height;
    console.log("width",width,"height",height);

    var svg = d3.select(this.element).append('svg');
    svg
        .attr("width", width)
        .attr("height", height)


    var simulation = d3.forceSimulation()
        .force("link", d3.forceLink().id(function (d) {
            return d.id;
        })
            .strength(0.025))
        .force("charge", d3.forceManyBody().strength(-200))
        .force("collide", d3.forceCollide().radius(12))
        .force("center", d3.forceCenter(width / 2, height / 2))
        //.force("y", d3.forceY(0))
        //.force("x", d3.forceX(0));

    var link = svg.selectAll(".link")
        .data(data.links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr('stroke', function (d) {
            return "#ddd";
        });

    // add the nodes to the graphic
    var node = svg.selectAll(".node")
        .data(data.nodes)
        .enter().append("g")

    // a circle to represent the node
    node.append("circle")
        .attr("class", "node")
        .attr("r", 15)
        .attr("fill", function (d) {
            return "#008142";
            //return d.colour;
        })
        .on("mouseover", mouseOver(.2))
        .on("mouseout", mouseOut)
        .call(d3.drag()
            .on("start", dragstarted)
            .on("drag", dragged)
            .on("end", dragended));

    var images = node.append("svg:image")
        .attr("xlink:href",  userImage)
        .attr("x", function(d) { return -25;})
        .attr("y", function(d) { return -25;})
        .attr("height", 50)
        .attr("width", 50);

    // hover text for the node
    node.append("title")
        .text(function (d) {
            return d.title;
        });

    // add a label to each node
    node.append("text")
        .attr("dx", 12)
        .attr("dy", ".35em")
        .text(function (d) {
            return d.id;
        })
        .style("stroke", "black")
        .style("stroke-width", 0.5)
        .style("fill", function (d) {
            return "#008142";
            //return d.colour;
        })//.on('contextmenu', function (data, index) {
    // console.log("contextmenu",data,index);
    //d3.event.preventDefault();
    //})
    this.setMenu(node);

    // add the nodes to the simulation and
    // tell it what to do on each tick
    simulation
        .nodes(data.nodes)
        .on("tick", ticked);

    // add the links to the simulation
    simulation
        .force("link")
        .links(data.links);

    // on each tick, update node and link positions
    function ticked() {
        link.attr("d", positionLink);
        node.attr("transform", positionNode);
    }

    // links are drawn as curved paths between nodes,
    // through the intermediate nodes
    function positionLink(d) {
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
    }

    // move the node based on forces calculations
    function positionNode(d) {
        // keep the node within the boundaries of the svg
        var pos = 0
        if (d.x < 0) {
            d.x = pos
        };
        if (d.y < 0) {
            d.y = pos
        };
        if (d.x > width) {
            d.x = width - pos
        };
        if (d.y > height) {
            d.y = height - pos
        };
        return "translate(" + d.x + "," + d.y + ")";
    }

    // build a dictionary of nodes that are linked
    var linkedByIndex = {};
    data.links.forEach(function (d) {
        linkedByIndex[d.source.index + "," + d.target.index] = 1;
    });

    // check the dictionary to see if nodes are linked
    function isConnected(a, b) {
        return linkedByIndex[a.index + "," + b.index] || linkedByIndex[b.index + "," + a.index] || a.index == b.index;
    }

    // fade nodes on hover
    function mouseOver(opacity) {
        return function (d) {
            // check all other nodes to see if they're connected
            // to this one. if so, keep the opacity at 1, otherwise
            // fade
            node.style("stroke-opacity", function (o) {
                var thisOpacity = isConnected(d, o) ? 1 : opacity;
                return thisOpacity;
            });
            node.style("fill-opacity", function (o) {
                var thisOpacity = isConnected(d, o) ? 1 : opacity;
                return thisOpacity;
            });
            // also style link accordingly
            link.style("stroke-opacity", function (o) {
                return o.source === d || o.target === d ? 1 : opacity;
            });
            link.style("stroke", function (o) {
                return o.source === d || o.target === d ? o.source.colour : "#ddd";
            });
        };
    }

    function mouseOut() {
        node.style("stroke-opacity", 1);
        node.style("fill-opacity", 1);
        link.style("stroke-opacity", 1);
        link.style("stroke", "#ddd");
    }
    function dragstarted(d) {
        if (!d3.event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }

    function dragended(d) {
        if (!d3.event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }





});

SPGraph.method('init', function () {
    console.log(this.data);
    this.data.forEach(function (d) {
        //d.count = +d.count;
        d.enabled = true;
    });

});
SPGraph.method('clean', function () {
    d3.select("svg").remove();
    d3.select(this.element).selectAll("*").remove();
});
SPGraph.method('createDrill', function () {
    var dataset = this.data;
    var path = this.path;
    var parent = this.parent;
    path.on('click', function (d) {
        console.log('Drill Down ' + d.data.label);
        parent.redraw(d.data.label);
    });
});
