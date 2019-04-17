var obj = require('../../SPObject');
import { Builder } from '../query/Builder.js';
import { Filter } from '../query/Filter.js';


export function Query(opts, map, db) {
    // console.log("Constructor:Query");
    this.queryType = opts.queryType;
    this.map = map;
    this.allFilters = [];
    this.childFilters = [];
    this.params = [];
    this.Init(opts.childfilter);
    this.db = db;
    opts.Params.forEach(e => {
        this.params.push(e);
    });
    this.dataposition = opts.DataPosition;
}

Query.method("addParams", function (it) {
    this.params.push(it);
});

Query.method("addTimeSeriesParams", function (dateField, interval, size, start, end) {
    this.params = [];
    this.addParams({ "DATEFIELD": dateField });
    this.addParams({ "INTERVAL": interval });
    this.addParams({ "SIZE": size });
    this.addParams({ "START": start });
    this.addParams({ "END": end });
    //console.log("start time >>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + start)
});

Query.method("addFilterObj", function (filter) {
    this.allFilters.push(filter);
});

Query.method("addFilter", function (type, sub, template) {
    var f = new Filter({
        filterType: type,
        filterSub: sub,
        filter: template
    });
    this.allFilters.push(f);
});

Query.method("cleanFilters", function () {
    //this.childFilters = [];
    this.allFilters = [];
    console.log("cleanFilters")
});

Query.method("getChildFilters", function () {
    // get All Filters to pass to child nodes
    var fs = [];
    this.allFilters.forEach(f => {
        fs.push(f);
    });
    this.childFilters.forEach(f => {
        fs.push(f);
    });
    return fs;
});

Query.method("Init", function (filters) {
    // console.log(filters);
    filters.forEach(e => {
        // console.log(e);
        for (var key in e) {
            //  console.log(e[key]);
            switch (key) {
                case "must": {
                    var obj = e[key];
                    for (var s in obj) {
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
                    for (var s in obj) {
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
Query.method("getFiltersCategory", function () {
    var filter = this.db.FilterCatTemplate;
    var mustfilter = "";
    var mustnotfilter = "";
    var filtercat = "";
    this.allFilters.forEach(f => {
        //console.log(f);
        if (f.category == "query") {
            switch (f.filterType) {
                case "must": {
                    if (mustfilter != "") { mustfilter += "," }
                    mustfilter += f.getFilter();
                    //console.log("mustfilter " + mustfilter);
                    break;
                }
                case "must_not": {
                    if (mustnotfilter != "") { mustnotfilter += "," }
                    mustnotfilter += f.getFilter();
                    //console.log("mustnotfilter " + mustnotfilter)
                    break;
                }
            }
        } else {
            if (filtercat != ""){ filtercat+=","}
            filtercat += f.getFilter();
        }
    });
    return filter.replace("##MUSTFILTER##", mustfilter).replace("##MUSTNOTFILTER##", mustnotfilter).replace("##FILTER##",filtercat);
});

Query.method("getAllFilters", function () {
    var filter = this.db.FilterTemplate;
    var mustfilter = "";
    var mustnotfilter = "";
    this.allFilters.forEach(f => {
        switch (f.filterType) {
            case "must": {
                if (mustfilter != "") { mustfilter += "," }
                mustfilter += f.getFilter();
                //console.log("mustfilter " + mustfilter);
                break;
            }
            case "must_not": {
                if (mustnotfilter != "") { mustnotfilter += "," }
                mustnotfilter += f.getFilter();
                //console.log("mustnotfilter " + mustnotfilter)
                break;
            }
        }
    });
    return filter.replace("##MUSTFILTER##", mustfilter).replace("##MUSTNOTFILTER##", mustnotfilter);
});
Query.method("processParams", function (query) {
    this.params.forEach(p => {
        for (var key in p) {
            //console.log("params " + key + " value " + p[key])
            query = query.replaceAll("##" + key + "##", p[key]);
        }
    });
    return query;
});
Query.method("getFullQuery", function () {
    var query = "";
    var index = '"index": "ahrm"';
    switch (this.queryType) {
        case "Terms": {
            query = this.db.BasicTemplate;
            query = query.replace("##FILTER##", this.getAllFilters());
            query = query.replace("##SUB##", this.genTerms());
            break;
        }
        case "ConstantQuery": {
            query = this.db.ConstantQuery;
            var filter = this.getAllFilters();
            if (filter != "") { filter = filter + ","; }
            query = query.replace("##FILTER##", filter);
            break;
        }
        case "Series": {
            query = this.db.SeriesTemplate;
            query = query.replace("##MAP##", this.map);
            var filter = this.getAllFilters();
            //if ( filter != ""){ filter = filter + ",";}
            query = query.replace("##FILTER##", filter);
            break;
        }
        case "Simple": {
            query = this.db.SimpleQuery;
            var filter = this.getAllFilters();
            if (filter != "") { filter = filter + ","; }
            query = query.replace("##FILTER##", filter);
            query = query.replace("##MAP##", this.map);
            break;
        }
        case "SimpleFilter": {
            query = this.db.SimpleFilterQuery;
            var filter = this.getFiltersCategory();
            query = query.replace("##FILTER##", filter);
            break;
        }
        case "Regex": {
            query = this.db.RegexQuery;
            query = query.replace("##MAP##", this.map);
            break;
        }
    }
    query = this.processParams(query);
    var fullquery = "{";
    fullquery += index + ",";
    fullquery += query;
    fullquery += "}";
    return fullquery;
});

// Deprecated Function
// Query.method("getFullQuery", function () {
//     var query = "";
//     switch (this.queryType) {
//         case "Terms": {
//             query = this.db.BasicTemplate;
//             query = query.replace("##FILTER##", this.getAllFilters());
//             query = query.replace("##SUB##", this.genTerms());
//             break;
//         }
//         case "ConstantQuery": {
//             query = this.this.this.db.ConstantQuery;
//             var filter = this.getAllFilters();
//             if (filter != "") { filter = filter + ","; }
//             query = query.replace("##FILTER##", filter);
//             break;
//         }
//     }
//     query = this.processParams(query);
//     var fullquery = "{";
//     fullquery += this.db.Index + ",";
//     fullquery += query;
//     fullquery += "}";
//     return fullquery;
// });

Query.method("genTerms", function () {
    return '"terms" : { "field" : "' + this.map + '" }';
});

Query.method("getFilters", function (drill, info) {
    if (this.checkField(info)) {
        //console.log(info);
        //console.log(drill);
        return info;
    }
    return "";
});

Query.method('checkField', function (obj) {
    if (typeof obj != "undefined") {
        if (obj != null) {
            return true;
        }
    }
    return false;
});
