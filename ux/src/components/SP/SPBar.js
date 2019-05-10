import Color from 'color';
import { emotionPalette } from '../config/EmotionConfig.js';
var obj = require('../SPObject');
var d3 = require("d3");


export function SPBar(opts) {
    console.log("Init SPBar");
    this.data = opts.data;
    this.element = opts.element;
    this.width = opts.width;
    this.height = opts.height;
    this.margin = opts.margin;
    this.parent = opts.parent;
    // console.log(this.parent);
    this.color = d3.scaleOrdinal(d3.schemeCategory10);
    this.draw();

}
SPBar.method("setData", function (data) {
    this.data = data;
    this.data.forEach(function (d) {
        console.log(d.label);
    });
    this.clean();
    this.draw();
});


SPBar.method("getColor", function (emotion) {
    var emo = emotionPalette.filter(e => e.emotion == emotion);
    console.log(emo.length);
    if (emo.length > 0) {
        return emo[0].color;
    } else {
        return 'rgb(171,171,171)';
    }
    return Color('rgb(255, 255, 255)');
});

SPBar.method("draw", function () {
    console.log("drawing SPBar chart");
    const that = this;
    var data = this.data;
    var parent = this.parent;

    // Coerce values into numbers
    data.forEach(function (d) {
        d.count = +d.count;
        console.log("--------> values" + d.count);
    });

    this.width = 960;
    this.height = 500;
    console.log("complete : width: " + this.width + " height: " + this.height + " margin " + this.margin);
    var margin = { top: 20, right: 20, bottom: 30, left: 40 },
        width = this.width - margin.left - margin.right,
        height = this.height - margin.top - margin.bottom;

    console.log("adjusted : width: " + width + " height: " + height);
    /*
    var x = d3.scaleBand().rangeRound([0, width]).paddingInner(0.05);
    //var x = d3.scaleBand()
    //  .range([0, width], .5);

    var y = d3.scaleLinear()
        .range([height, 0]);
    */
    var x = d3.scaleBand()
        .range([0, width])
        .padding(0.1);
    var y = d3.scaleLinear()
        .range([height, 0]);

    x.domain(data.map(function (d) { return d.label; }));
    y.domain([0, d3.max(data, function (d) { return d.count; })]);




    d3.select(this.element).append("div")
        .attr("class", "sptooltip")
        .html(function () {
            return "<p><strong>Percentage Value</strong></p><p><span id=\"value\" > 100</span >%</p > "
        });

    var svg = d3.select(this.element).append('svg');

    var chart = svg
        .attr("class", "barchart")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");


    chart.selectAll(".bar")
        .data(data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function (d) { return x(d.label); })
        .attr("width", x.bandwidth())
        .attr("y", function (d) { return y(d.count); })
        .attr("height", function (d) { return height - y(d.count); })
        .attr("fill",function (d) { console.log(d);return that.getColor(d.label); })
        .on("mouseover", function (d) {


            var xPosition = parseFloat(d3.select(this).attr("x")) + x.bandwidth() / 2;
            var yPosition = parseFloat(d3.select(this).attr("y")) / 2 + height / 2;
            console.log(" x " + xPosition + " y " + yPosition);
            console.log(x.bandwidth());
            console.log(d);
            //xPosition = 40;
            //yPosition = 40;

           
            function roundToTwo(num) {    
                return +(Math.round(num + "e+2")  + "e-2");
            }
            d3.select(".sptooltip")
                .style("left", xPosition + "px")
                .style("top", yPosition + "px")
                .select("#value")
                .text(roundToTwo(d.count));


            d3.select(".sptooltip").classed("hidden", false);

        })
        .on("mouseout", function () {


            d3.select(".sptooltip").classed("hidden", true);

        })
        .on("click", function (d) {
            console.log(d);
            console.log("click " + d + d.type + " : " + d.value);
            console.log(parent);
            parent.redraw(d.label);
        });

    console.log(height);
    var nheight = height + margin.top;
    // add the x Axis
    svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + nheight + ")")
        .call(d3.axisBottom(x));

    // add the y Axis
    svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        .call(d3.axisLeft(y));

  

});

SPBar.method('init', function () {
    console.log(this.data);
    this.data.forEach(function (d) {
        //d.count = +d.count;
        d.enabled = true;
    });

});
SPBar.method('clean', function () {
    d3.select("svg").remove();
    d3.select(this.element).selectAll("*").remove();
    this.color = d3.scaleOrdinal(d3.schemeCategory20);
});
SPBar.method('createDrill', function () {
    var dataset = this.data;
    var path = this.path;
    var parent = this.parent;
    path.on('click', function (d) {
        console.log('Drill Down ' + d.data.label);
        parent.redraw(d.data.label);
    });
});
