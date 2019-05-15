import { Topic } from '../data/query/Topic.js';
import { Query } from '../data/query/Query.js';

export  function Model(opts,settings) {
    console.log("Constructor:Model");
    //console.log(opts);
    this.topics = [];
    this.Init(opts,settings);
    console.log("Constructor:Model::Complete");
}

Model.method("Init", function (opts,settings) {
    console.log("opts-------------------------------------:" + opts);
    console.log("opts-------------------------------------:" + opts.Topics);
    var data = JSON.parse(opts.Topics);
    data.forEach(e => {
        console.log(e);
        var t = new Topic(e,settings);
        this.topics.push(t);
    });
    console.log("end of init");
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

