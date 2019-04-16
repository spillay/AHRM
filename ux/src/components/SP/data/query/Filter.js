var obj = require('../../../SPObject');



export default function Filter(opts) {
    console.log("Constructor:Filter");
    //console.log(opts);
    this.filterType = opts.filterType;
    this.filterSub = opts.filterSub;
    this.filter = opts.filter;
    console.log("end filter 123");
    console.log(opts.filterSub)
    if (opts.filterSub == "Time"){
        console.log(opts.Time);
        console.log(opts.filterTimeTemplate)
        this.time = opts.Time;
        this.filter = this.genTimeFilter(opts.filterTimeTemplate);
    }
    console.log("end filter");
    
}

Filter.method("genTimeFilter",function(filterTemplate){
    var filter = filterTemplate;//db.TimeFilter;
    filter = filter.replace("##START##",this.time.start);
    filter = filter.replace("##END##",this.time.end);
    return filter;
});

Filter.method("getFilter",function(){
    return this.filter;
});
