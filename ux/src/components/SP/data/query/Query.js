
import { Builder } from '../query/Builder.js';
import Filter from '../query/Filter.js';
var obj = require('../../../SPObject');

export default function Query(opts,map,db) {
   // console.log("Constructor:Query");
    this.queryType = opts.queryType;
    this.map = map;
    this.allFilters = [];
    this.childFilters = [];
    this.params = [];
    this.Init(opts.childfilter);
    this.db = db;
    opts.Params.forEach(e=>{
        this.params.push(e);
    });
    this.dataposition = opts.DataPosition;
}

Query.method("addParams", function (it) {
    this.params.push(it);
});

Query.method("addTimeSeriesParams", function (dateField,interval,size,start,end) {
    this.params = [];
    this.addParams({"DATEFIELD":dateField});
    this.addParams({"INTERVAL":interval});
    this.addParams({"SIZE":size});
    this.addParams({"START":start});
    this.addParams({"END":end});
    console.log("start time >>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + start)
});

Query.method("addFilter", function (filter) {
    this.allFilters.push(filter);
});

Query.method("cleanFilters", function () {
    //this.childFilters = [];
    this.allFilters = [];
});

Query.method("checkifexists", function (fs,nf) {
    fs.forEach(f=>{
        if (f.filter == nf.filter){
            return true;
        }
    })
    return false;

});

Query.method("getChildFilters", function () {
    // get All Filters to pass to child nodes
    var fs = [];
    this.allFilters.forEach(f=>{
        if (!this.checkifexists(fs,f)){
            fs.push(f);
        }
    });
    this.childFilters.forEach(f=>{
        if (!this.checkifexists(fs,f)){
            fs.push(f);
        }
    });
    return fs;
});

Query.method("Init", function (filters) {
   // console.log(filters);
    filters.forEach(e => {
       // console.log(e);
        for(var key in e){
          //  console.log(e[key]);
            switch(key){
                case "must": {
                    var obj = e[key];
                    for(var s in obj){
                       // console.log(obj[s].filter);
                        var f = new Filter({
                            filterType: "must",
                            filterSub: "",
                            filter: obj[s].filter
                        });
                        this.childFilters.push(f);
                    }
                    break;
                }
                case "must_not": {
                    var obj = e[key];
                    for(var s in obj){
                        var f = new Filter({
                            filterType: "must",
                            filterSub: "",
                            filter: obj[s].filter
                        });
                        this.childFilters.push(f);
                    }
                    break;
                }
            }
        }
    });
    //console.log("all filters " + this.allFilters);
});

Query.method("getAllFilters", function () {
    var filter = this.db.FilterTemplate;
    var mustfilter = "";
    var mustnotfilter = "";
    this.allFilters.forEach(f=>{
        switch(f.filterType){
            case "must":{
                if (mustfilter!=""){ mustfilter+=","}
                mustfilter += f.getFilter();
                //console.log("mustfilter " + mustfilter);
                break;
            }
            case "must_not":{
                if (mustnotfilter!=""){ mustnotfilter+=","}
                mustnotfilter += f.getFilter();
                //console.log("mustnotfilter " + mustnotfilter)
                break;
            }
        }
    });
    return filter.replace("##MUSTFILTER##",mustfilter).replace("##MUSTNOTFILTER##",mustnotfilter);
});
Query.method("processParams", function (query) {
    this.params.forEach(p=>{
        for(var key in p){
            console.log("params " + key + " value " + p[key])
            query = query.replaceAll("##" + key + "##",p[key]);
        }
    });
    return query;
});
Query.method("getFullQueryIndex", function (index) {
    var query = "";
    switch(this.queryType){
        case "Terms":{
            query = this.db.BasicTemplate;
            query = query.replace("##FILTER##",this.getAllFilters());
            query = query.replace("##SUB##",this.genTerms());
            break;
        }
        case "ConstantQuery":{
            query = this.db.ConstantQuery;
            var filter = this.getAllFilters();
            if ( filter != ""){ filter = filter + ",";}
            query = query.replace("##FILTER##",filter);
            break;
        }
        case "Series":{
            query = this.db.SeriesTemplate;
            query = query.replace("##MAP##",this.map);
            break;
        }
    }
    console.log("before query " + query)
    query = this.processParams(query);
    console.log("after query " + query)
    var fullquery = "{";
    fullquery += index + ",";
    fullquery += query;
    fullquery += "}";
    console.log("full query" + fullquery);
    return fullquery;
});

// Deprecated Function
Query.method("getFullQuery", function () {
    var query = "";
    switch(this.queryType){
        case "Terms":{
            query = this.db.BasicTemplate;
            query = query.replace("##FILTER##",this.getAllFilters());
            query = query.replace("##SUB##",this.genTerms());
            break;
        }
        case "ConstantQuery":{
            query = this.db.ConstantQuery;
            var filter = this.getAllFilters();
            if ( filter != ""){ filter = filter + ",";}
            query = query.replace("##FILTER##",filter);
            break;
        }
    }
    query = this.processParams(query);
    var fullquery = "{";
    fullquery += this.db.Index + ",";
    fullquery += query;
    fullquery += "}";
    return fullquery;
});

Query.method("genTerms",function(){
    return '"terms" : { "field" : "' + this.map + '" }';
});

Query.method("getFilters", function (drill,info) {
    if (this.checkField(info)){
        //console.log(info);
        //console.log(drill);
        return info;
    }
    return "";
});

Query.method('checkField', function (obj) {
    if (typeof obj != "undefined"){
        if (obj != null){
            return true;
        }
    }
    return false;
});
