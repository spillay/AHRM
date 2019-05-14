import { DataNode } from 'DataManager'
import { DataHelper } from 'DataManager'
import { DateHelper } from 'DataManager'
import { GraphHelper } from 'DataManager'
import ESService from '../security/ESService';

var obj = require('../SPObject');
var d3 = require("d3");


export default function GraphData(opts) {
    console.log("Init GraphData----------------------------------------------->");
    console.log(opts)
    this.dte = new DateHelper(opts.date);
    this.root = new DataNode(opts.id,opts.name,0)
    this.interval = opts.interval;
    this.range =  this.dte.get24HourRange();
    console.log(":::::::::::::::::::::::::::::::::::::::::::::::",this.range)
    this.nodeList = [];
    this.essvr = new ESService();
    this.nodeList.push(this.root)
    this.level = opts.level;
}


GraphData.prototype.processChildren3 =  function (nodeList,level) {
    return this.essvr.getData(JSON.parse(this.root.getChildrenQuery(this.range)))
 }

GraphData.prototype.process =  function () {
    console.log("++++++++++++++++++++++++++++++++++++process")
    let res =  this.processChildren(this.nodeList,this.level).then(()=>{
        return this.processEmotions();
    })
    return res
    
}

// GraphData.methodA("process", async function (email,date) {
    
//     //return new Promise(resolve => {

//         (async () => {
//             let res = await this.processChildren3(this.nodeList,1);
//             console.log("resssssssssssssssss",res,this.root);
//             //let res2 = await this.processEmotions();
//             return res
//             //resolve(res2);
//           })().catch(e => console.log("Error " + e));


          
         

//     //});
    
//     //this.processChildren(this.nodeList,1)
// })
GraphData.method("getData", function () {
    return this.data
})
GraphData.method("getEmotionQuery", function (email) {
    var dh = new DataHelper();
    var query = dh.getModel().getQuery("Prime", "Series");
    this.dte.getSeries(query, this.interval, 6);
    var timeFilter = dh.getTimeFilter(this.range);
    query.addFilterObj(timeFilter);
    query.addFilter("must", "", "{\"term\": { \"model.from.keyword\": \"" + email + "\" }}");
    var res = query.getFullQuery();
    return res;
})
GraphData.method("processEmotions", function () {
    var that = this
    return new Promise(function(resolve, reject) {
    
        var gh = new GraphHelper();
        var data = gh.getGraphData(that.root);
        
        
        var dh = new DataHelper();
        var query = dh.getModel().getQuery("Prime", "Series");
        let promises = [];
        

        data.nodes.forEach(n => {
            //console.log(n.id);
            var q = that.getEmotionQuery(n.id);
            //console.log("emotion query ----------------------------------",q);
            promises.push(that.essvr.getData(JSON.parse(q)));
        });

        Promise.all(promises)
            .then((results) => {
                console.log("All done", results);
                let all = [];
                var idx = 0;
                results.forEach(r => {
                    var ndata = dh.getData(JSON.parse(r), query.dataposition);
                    console.log(ndata);
                    var tdata = [];
                    ndata.forEach(t => {
                        //console.log(t.prime.buckets[0].key);
                        tdata.push(t.prime.buckets[0].key);
                    })
                    data.nodes[idx]["emotions"] = tdata;
                    data.nodes[idx]["index"] = 0;
                    data.nodes[idx]["color"] = "gray";
                    idx = idx + 1;
                });
                console.log("final result",data)
                that.data = data
                resolve()
            }).catch((e) => {
                console.log(e);
                reject()
            });
    })        
})


GraphData.method("processChildren2", async function (nodeList,level) {
    let promises = [];
    this.nodeList.forEach(c => {
        console.log("::::::::::::::::::::::::::::::::::::::::::::::::::::",c.getChildrenQuery(this.range));
        promises.push(this.essvr.getData(JSON.parse(c.getChildrenQuery(this.range))));
    });
    return Promise.all(promises)
})
GraphData.method("processChildren", function (nodeList,level) {
    console.log("processing Children")
    var that = this
    return new Promise(function(resolve, reject) {

            let promises = [];
            nodeList.forEach(c => {
                console.log("::::::::::::::::::::::::::::::::::::::::::::::::::::",c.getChildrenQuery(that.range));
                promises.push(that.essvr.getData(JSON.parse(c.getChildrenQuery(that.range))));
            });
            Promise.all(promises)
                    .then((results) => {
                        console.log("All done", results);
                        if (results.length > 0) {
                            let all = [];
                            results.forEach(r => {
                                console.log(r);
                            })
                            console.log("All done0");
                            var idx = 0;
                            nodeList.forEach(c => {
                                c.addChildNodes(JSON.parse(results[idx]));
                                idx = idx + 1;
                            });
                            if (that.root.getLevel() < level) {
                                nodeList.forEach(c => {
                                    that.processChildren(c.getLeafNodes(), level).then(()=>{
                                        resolve()
                                    })
                                })
                            } else {
                                resolve()
                                // var gh = new GraphHelper();
                                // var d = gh.getGraphData(this.root);
                                // return this.processEmotions(d);
                                // // console.log(d)
                            }
                            
                        } else {
                            resolve()
                        }
                        console.log("displaying", that.root.getNodeData());
                    })
                    .catch((e) => {
                        console.log(e);
                    });
            console.log("end of execute",nodeList)
    })
})