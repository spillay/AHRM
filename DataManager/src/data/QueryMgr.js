import { Model } from '../data/Model.js'
//import { elasticsearch } from '../../../config.js'
//import { db } from '../../../config.js'

var queryMgr = {
    getSingleton: (function() { // BEGIN iife
        var singleton;
        var _data = null;
        return function() {
            if (!singleton) {
                singleton = {
                    init: function(topics) {
                        console.log("initialize querymgr");
                        _data = new Model(topics);
                    },
                    getOldQuery: function(dataModel,info) {
                        console.log("getQuery");
                        return _data.getOldQuery(dataModel,info);
                    },
                    getQuery: function(topic,queryType){
                        return _data.getQuery(topic,queryType);
                    },
                    getServer: function() {
                        return elasticsearch.server;
                    }
                }
                singleton.init();
            }
            return singleton;
        };
    }()) 
};
export {queryMgr}
