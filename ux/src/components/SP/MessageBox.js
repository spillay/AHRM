var obj = require('../SPObject');
var d3 = require("d3");

export function MessageBox(opts) {
    this.msg = opts.data;
    this.element = opts.element;
    this.width = opts.width;
    this.height = opts.height;
    this.margin = opts.margin;
    this.draw();
}

MessageBox.method("draw", function () {
    var svg = d3.select(this.element)
        .append('svg')
        .attr('width', this.width)
        .attr('height', this.height)
        .append('g')
        .attr('transform', 'translate(0,0)');
        //.attr('transform', 'translate(' + (this.width / 2) + ',' + (this.height / 2) + ')');

    var data = [];
    data.push("Error:" + this.msg);
    
    var r = 15;

    var bar = svg.selectAll("g")
        .data(data)
        .enter().append("g")
        .attr("transform", function (d, i) { return "translate(100,100)"; });

    /*
    bar.append("rect")
        .attr("width", 200)
        .attr("height", 100)
        .on("mouseover", function () {
            d3.select(this.nextSibling)
                .attr("opacity", "1")
        })
        .on("mouseout", function () {
            d3.select(this.nextSibling)
                .attr("opacity", "0")
        });
    */
    bar.append("text")
        .attr("width", 200)
        .attr("height", 100)
        .attr("dy", "-.35em")
        .attr("opacity", "1")
        .text(function (d) { return d; });
});
