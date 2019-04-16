var obj = require('../../SPObject');
import { settings } from '../../config.js';

export function Filter(opts) {
    //console.log("Constructor:Filter");
   // console.log(opts);
    this.filterType = opts.filterType;
    this.filterSub = opts.filterSub;
    this.filter = opts.filter;
    this.category = opts.category;
    if (this.category == undefined){
        this.category = "query";
    }
    this.Params = [];
    // Need to change this so we can use the params instead
    if (opts.filterSub == "Time"){
        console.log(opts.Time);
        this.time = opts.Time;
        this.filter = this.genTimeFilter(settings.TimeFilter);
    }
    //console.log("++++++++++++++++Filter+++++++++++++++++++++++++++++++" + this.filter);
}

Filter.method("addParam",function(name,value){
    var p = {};
    p["name"]=name;
    p["value"]=value;
    this.Params.push(p);
})

Filter.method("genTimeFilter",function(filterTemplate){
    var nfilter = filterTemplate;//db.TimeFilter;
    nfilter = nfilter.replace("##START##",this.time.start);
    nfilter = nfilter.replace("##END##",this.time.end);
    //console.log("+++++++++++++nfilter++++++++++++++++++++++++++" + nfilter);
    return nfilter;
});

Filter.method("getFilter",function(){
    switch(this.filterSub){
        case "match":
            var f = settings.MatchFilter;
            this.Params.forEach(p => {
                var nme = "##" + p.name + "##";
                console.log(nme.toUpperCase() + " : " + p.value);
                f = f.replace(nme.toUpperCase(),p.value);
            });
            this.filter = f;
            break;
    }
    return this.filter;
});