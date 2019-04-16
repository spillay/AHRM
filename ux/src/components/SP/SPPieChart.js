var obj = require('../SPObject');
var d3 = require("d3");

export function SPPieChart(opts){
    this.data = opts.data;
    this.element = opts.element;
    this.width = opts.width;
    this.height = opts.height;
    this.margin = opts.margin;
    this.parent = opts.parent;
    this.drillValue = opts.drill;
    if (opts.showLegend === undefined){
        this.showLegend = true; 
    } else {
        this.showLegend = opts.showLegend;
    }
    if (opts.createDrill === undefined){
        this.createDrill = true;
    } else {
        this.createDrill = opts.createDrill;
    }
    if (opts.fullPie === undefined){
        this.fullPie = false;
    } else {
        this.fullPie = opts.fullPie;
    }
    console.log("fullpie " + this.fullPie);
    console.log(this.parent);
   
   this.color = d3.scaleOrdinal(d3.schemeAccent);
   this.draw();
}
SPPieChart.method("setData", function (data){
    this.data = data;
    this.clean();
    this.draw();
});

SPPieChart.method("draw", function (){
    console.log("drawing pie chart");
    if ( this.parent !== null){
        console.log("setting size according to parent");
        this.width = this.parent.state.width;
        this.height = this.parent.state.height;
    }
    console.log("color " + this.color);
    //console.log(this.color("h"));
    var radius = Math.min(this.width - this.margin, this.height - this.margin) / 2;
    var color = this.color;
    
    var svg = d3.select(this.element)
        .append('svg')
        .attr('width', this.width)
        .attr('height', this.height)
        .append('g')
        .attr('transform', 'translate(' + (this.width / 2) +  ',' + (this.height / 2) + ')');

    var arc;    
    if (this.fullPie){
        console.log("full pie");
        arc = d3.arc()
            .innerRadius(radius)
            .outerRadius(radius);
    } else {
        // This is a donut
        arc = d3.arc()
            .innerRadius(radius-75)
            .outerRadius(radius);
    }

    var pie = d3.pie()
        .value(function(d) { return d.count; })
        .sort(null);

    var path = svg.selectAll('path')
        .data(pie(this.data))
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('fill', function(d, i) {
            return color(d.data.label);
        });
    
    this.pie = pie;
    this.path = path;
    this.arc = arc;
    this.svg = svg;
    this.init();
    if(this.showLegend) { this.createLegend(); }
    this.createToolTip();
    if(this.createDrill){ this.setDrill(); }
});
SPPieChart.method('init',function(){
    console.log(this.data);
    this.data.forEach(function(d) {
        //d.count = +d.count;
        d.enabled = true;                                         
    });
    
});
SPPieChart.method('clean',function(){
    d3.select("svg").remove(); 
    d3.select(this.element).selectAll("*").remove();
    this.color = d3.scaleOrdinal(d3.schemeCategory20);
});
SPPieChart.method('setDrill',function(){
    var dataset = this.data;
    var path = this.path;
    var parent = this.parent;
    path.on('click', function(d) {
       console.log('Drill Down ' + d.data.label);
       parent.redraw(d.data.label);
    });
});
SPPieChart.method('createToolTip',function(){
    var dataset = this.data;
    var path = this.path;
    var tooltip = d3.select(this.element)            
    .append('div')                             
    .attr('class', 'sptooltip');                 
  
    tooltip.append('div')                        
        .attr('class', 'label');                   
    
    tooltip.append('div')                        
        .attr('class', 'count');                   
    
    tooltip.append('div')                        
        .attr('class', 'percent');                 
    
    tooltip.style('display', 'none');

    path.on('mouseover', function(d){
        var total = d3.sum(dataset.map(function(d) {
        return d.count;
        }));
        var percent = Math.round(1000 * d.data.count / total) / 10;
        console.log(percent);
        tooltip.select('.label').html(d.data.label);
        tooltip.select('.count').html(d.data.count);
        tooltip.select('.percent').html(percent + '%');
        tooltip.style('display', 'block');
    });

    path.on('mousemove', function(d) {
        tooltip.style('top', (d3.event.layerY + 10) + 'px')
        .style('left', (d3.event.layerX + 10) + 'px');
    });

    path.on('mouseout', function() {
        tooltip.style('display', 'none');
    });
});
SPPieChart.method('createLegend',function(){
    console.log("creating legend");
    var legendRectSize = 18;                             
    var legendSpacing = 4;  
    // Make values accessible within functions
    var color = this.color;
    var data = this.data;
    var pie = this.pie;
    var path = this.path;
    var arc = this.arc;
    console.log(this.svg);
    var legend = this.svg.selectAll('.legend')                     
        .data(color.domain())                                   
        .enter()                                                
        .append('g')                                            
        .attr('class', 'legend')                                
        .attr('transform', function(d, i) {                     
            var height = legendRectSize + legendSpacing;          
            var offset =  height * color.domain().length / 2;     
            var horz = -2 * legendRectSize;                       
            var vert = i * height - offset;                       
            return 'translate(' + horz + ',' + vert + ')';        
        });    
    legend.append('rect')                                     
        .attr('width', legendRectSize)                          
        .attr('height', legendRectSize)                         
        .style('fill', color)                                   
        .style('stroke', color)
        .on('click', function(label) {                            
              var rect = d3.select(this);                             
              var enabled = true;                                     
              var totalEnabled = d3.sum(data.map(function(d) {     
                return (d.enabled) ? 1 : 0;                           
              }));                                                    
              console.log(totalEnabled);
              console.log(rect.attr('class'));
              if (rect.attr('class') === 'disabled') {                
                rect.attr('class', '');                               
              } else {                                                
                if (totalEnabled < 2) return;                         
                rect.attr('class', 'disabled');                       
                enabled = false;                                      
              }                                                       
              console.log(rect.attr('class'));
              pie.value(function(d) {                                 
                if (d.label === label) d.enabled = enabled;           
                return (d.enabled) ? d.count : 0;                     
              });                                                     

              path = path.data(pie(data));                         

              path.transition()                                       
                .duration(750)                                        
                .attrTween('d', function(d) {                         
                  var interpolate = d3.interpolate(this._current, d); 
                  this._current = interpolate(0);                     
                  return function(t) {                                
                    return arc(interpolate(t));                       
                  };                                                  
                });                                                   
            });                                     
      legend.append('text')                                     
        .attr('x', legendRectSize + legendSpacing)              
        .attr('y', legendRectSize - legendSpacing)              
        .text(function(d) { return d; });          
});

