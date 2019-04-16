var obj = require('../../SPObject');
import {queryMgr} from '../QueryMgr.js';

export function Builder(opts) {
    //console.log("Constructor:Builder");
    this.topic = opts.topic;
    this.filter = opts.filter;
    this.map = opts.map;
}
/*
Builder.method("getQuery", function (time) {
    var query = "{";
    query += db.Index + ",";
    query += this.getBasicQuery(time);
    query += "}";
    return query;
});

Builder.method("getBasicQuery", function (time) {
    var query = db.BasicTemplate;
    query = query.replace("##FILTER##",this.genTimeFilter(time));
    query = query.replace("##SUB##",this.genTerms());
    return query;
});

Builder.method("genTimeFilter",function(time){
    var filter = db.TimeFilter;
    filter = filter.replace("##START##",time.start);
    filter = filter.replace("##END##",time.end);
    // Note a comma , is required before the next statement for other filters
    if (this.filter!=""){ this.filter = "," + this.filter;}
    filter = filter.replace("##OTHER##",this.filter);
    filter += ",";
    return filter;
});

Builder.method("genTerms",function(){
    return '"terms" : { "field" : "' + this.map + '" }';
});
*/
