import { Model } from './Model'
import { elasticsearch } from '../spconfig'
import { settings } from '../spconfig'
import { db } from '../spconfig'

var queryMgr = {
    getSingleton: (function() { // BEGIN iife
        var singleton;
        var _data = null;
        return function() {
            if (!singleton) {
                singleton = {
                    init: function() {
                        console.log("initialize querymgr");
                        //console.log(db.Topics);
                        _data = new Model(db.Topics,settings);
                        console.log(_data);
                    },
                    getOldQuery: function(dataModel,info) {
                        console.log("getQuery");
                        return _data.getOldQuery(dataModel,info);
                    },
                    getQuery: function(topic,queryType){
                        console.log(_data);
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
