import { ESDS } from '../data/ds/ESDS.js';
import { Builder } from '../data/query/Builder.js';
import { Topic } from '../data/query/Topic.js';
import { Query } from '../data/query/Query.js';

export  function Model(opts,settings) {
    //console.log("Constructor:Model");
    //console.log(opts);
    this.topics = [];
    this.Init(opts,settings);
}

Model.method("Init", function (opts,settings) {
    var data = JSON.parse(opts.Topics);
    data.forEach(e => {
        //console.log(e);
        var t = new Topic(e,settings);
        this.topics.push(t);
    });
});

Model.method("getTopic", function (name) {
    var t = null;
    this.topics.forEach(e=>{
        if (e.name == name){
            t = e;
        }
    });
    return t;
});

Model.method("getQuery", function (topic,queryType) {
    var topic = this.getTopic(topic);
    var query = topic.getQuery(queryType);
    return query;
});

Model.method("getOldQuery", function (dataModel,info) {
   // console.log(dataModel);
    var topic = this.getTopic(dataModel.topic);
    //console.log(topic);
    var query = topic.getQuery(dataModel.queryType);
    //console.log(query);
    var opts = {
        topic: topic.name,
        filter: query.getFilters(dataModel.drill,info),
        map: topic.map
    };
    var builder = new Builder(opts);
    return builder.getQuery(dataModel.Time);
});

/*
export function Model(opts) {
    console.log("Constructor:Model");
    this.topic = opts.topic;
    this.subTopic = opts.subTopic;
    this.queryType = opts.queryType;
    this.query = opts.query;
    this.server = opts.server;
    console.log(this.server);
}

Model.method("getName", function () {
    return this.topic + " by " + this.subTopic;
});

Model.method("match", function (opts) {
    if (this.topic == opts.topic && this.subTopic == opts.subTopic){
        return this;
    }
    return null;
});

Model.method("getQuery", function () {
    if ( this.queryType == "ES"){
        console.log("Elastic Search Query");
        var builder  = new Builder({topic:this.topic,subTopic:this.subTopic,filter:''});
        return builder.getQuery({start:'01/01/2017',end:'2018'});
    }
    return "NoResult";
});
*/