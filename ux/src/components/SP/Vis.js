import { SPPieChart } from './SPPieChart.js';
import { SPBar } from './SPBar.js';
import { MessageBox } from './MessageBox.js';
import DataGrid from './DataGrid.js';
import SPDataGrid from './SPDataGrid.js';
import { dataMgr } from './data/DataMgr';
import {queryMgr} from './data/QueryMgr';
import Filter from './data/query/Filter';


import ReactDOM from 'react-dom';
import React from 'react';
import EmoService from '../security/EmoService';
import ESService from '../security/ESService';
import { settings } from './spconfig'
var d3 = require("d3");

export function Vis(opts, filters,server) {
    console.log("Init Vis>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",server);
    console.log(opts);
    this.width = opts.width;
    this.height = opts.height;
    this.margin = opts.margin;
    this.type = opts.type;
    this.server = server;


    this.dataModel = opts.dataModel;
    this.index = "\"index\":\"" + this.dataModel.index.index + "\"";
    console.log(this.dataModel);
    console.log("---Vis" + this.dataModel.topic + " : " + this.dataModel.queryType);
    this.query = queryMgr.getSingleton().getQuery(this.dataModel.topic, this.dataModel.queryType);
    filters.forEach(f => {
        this.query.addFilter(f);
    });
    console.log("after query " + this.query);
    console.log("-------------Filter-------------->" + this.query.getAllFilters());

    this.parent = this;
    this.child = null;
    if (typeof opts.child != "undefined") {
        if (opts.child != null) {
            console.log("Vis:adding child");
            console.log(opts.child);
            var v = new Vis(opts.child, [],server);
            //var v = new Vis(opts.child,[]);
            this.child = v;
        }
    }
}

Vis.method('checkField', function (obj) {
    if (typeof obj != "undefined") {
        if (obj != null) {
            return true;
        }
    }
    return false;
});
Vis.method('addChildFilters', function (filters) {
    if (this.child != null) {
        this.child.query.cleanFilters();
        filters.forEach(f => {
            console.log("filters +++++++++++++++++++++++++++++++" + f.filter);
            this.child.query.addFilter(f);
        });
    }
});

Vis.method("getDrillFilter", function (info) {
    console.log("--------------drill-------- " + info);
    var val = JSON.stringify(this.dataModel.drill);
    console.log(">>>>>>>>>>>>>>>>>" + val);
    info = info.replaceAll('"', "\\\"");
    val = val.replace("##LABEL##", info);
    console.log(val + " for " + this.dataModel.topic);
    var obj = JSON.parse(val);
    var childfilters = [];
    this.query.getChildFilters().forEach(f => {
        var filter = f.getFilter();
        for (var e in obj) {
            console.log(e);
            console.log("key " + e);
            console.log("value " + obj[e]);
            var v = obj[e];
            v = v.replaceAll('"', "\\\"");
            //filter = filter.replaceAll("##" + e + "##",v);   
            filter = filter.replaceAll("##" + e + "##", v);
        }
        var nf = new Filter({
            filterType: f.filterType,
            filterSub: f.filterSub,
            Time: f.time,
            filter: filter,
            filterTimeTemplate: settings.TimeFilter
        });
        childfilters.push(nf);
    })
    this.addChildFilters(childfilters);
});

Vis.method("setContainer", function (container) {
    this.parent = container;

});

Vis.method("getChild", function () {
    // set container for child as well
    if (this.child !== null) {
        this.child.setContainer(this.parent);
        return this.child;
    }
    return null;
});

Vis.method("setChild", function (c) {
    this.child = c;
});

Vis.method("draw", function (element, info) {
    console.log("**********************draw*************************** " + info);
    //var server = queryMgr.getSingleton().getServer();
    //var query = this.query.getFullQueryIndex(this.index);
    var query = this.query.getFullQuery();
    var dataposition = this.query.dataposition;
    var container = this.parent;
    console.log("-------------Query---> " + query);
    console.log("--------------------------))))))))))))))Type: " + this.type);

    //var cf = JSON.parse(query);
    //console.log(cf);

    if (this.type === "Pie") {
        var essvr = new ESService();
        essvr.getDatabyHost(JSON.parse(query),this.server).then(function (resp) {
            var data = JSON.parse(resp);
            console.log(resp);
            dataposition.forEach(s => {
                data = data[s];
            });
            var esData = data;
            var dataset = [];

            esData.forEach(e => {
                dataset.push({ label: e.key, count: e.doc_count });
            });

            var p = new SPPieChart({
                'element': element,
                'parent': container,
                'data': dataset,
                'width': container.state.width,
                'height': container.state.height,
                'margin': container.state.margin
            });
        }, function (error) {
            console.log(error.message);
            var m = new MessageBox({
                'element': element,
                'parent': container,
                'data': "ElasticSearch " + error.message,
                'width': container.state.width,
                'height': container.state.height,
                'margin': container.state.margin
            });
        });
    }
    if (this.type === "DataGrid") {


        ReactDOM.render(<SPDataGrid query={query} server={this.server} pageSize="5" />, element);
        /*
        var client = new es.Client(JSON.parse(server));
        client.search(JSON.parse(query)).then(function (resp) {
            var data = resp;
            dataposition.forEach(s=>{
                data = data[s];
            });
            var esData = data;
            console.log(esData);
            ReactDOM.render(<DataGrid query={query}/>,element);
            //ReactDOM.render(<div>hello</div>,element);
        });
       */

    }
    if (this.type === "Bar") {
        /*
        console.log(" info ----====" + info);
        var nvalue = info.replaceAll("\"","\\\"");
        console.log(" nvalue ----====" + nvalue);
        
        console.log(this.ds.query);
        this.ds.query = this.ds.query.replaceAll("##country##",nvalue);
        console.log(this.ds.query);
        */

        // var client = new es.Client(JSON.parse(server));
        // client.search(JSON.parse(query)).then(function (resp) {
        var essvr = new ESService();
        essvr.getDatabyHost(JSON.parse(query),this.server).then(function (resp) {
            var data = JSON.parse(resp);
            dataposition.forEach(s => {
                data = data[s];
            });
            var esData = data;
            var dataset = [];

            var tot = 0;
            esData.forEach(e => {
                tot += e.doc_count;
            });
            console.log(tot);
            esData.forEach(e => {
                console.log("label " + e.key + " count " + e.doc_count);
                console.log((e.doc_count / tot) * 100);
                dataset.push({ label: e.key, count: (e.doc_count / tot) * 100 });
            });
            var p = new SPBar({
                'element': element,
                'parent': container,
                'data': dataset,
                'width': container.state.width,
                'height': container.state.height,
                'margin': container.state.margin
            });
        }, function (error) {
            console.log(error.message);
            var m = new MessageBox({
                'element': element,
                'parent': container,
                'data': "ElasticSearch " + error.message,
                'width': container.state.width,
                'height': container.state.height,
                'margin': container.state.margin
            });
        })
    }
});