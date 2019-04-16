
import Query  from '../query/Query.js';
import { Builder } from '../query/Builder.js';
var obj = require('../../../SPObject');

export function Topic(opts,db) {
    //console.log("Constructor:Topic");
    //console.log(opts.name);
    this.name = opts.name;
    this.map = opts.map;
    this.queries = [];
    opts.queries.forEach(e => {
        var q = new Query(e,this.map,db);
        this.queries.push(q);
    });
}

Topic.method("getQuery", function (type) {
   var q = null;
   this.queries.forEach(e=>{
        if (e.queryType == type){
            q = e;
        }
   });
   return q;
});