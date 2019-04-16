
import obj from "../../SPObject";
import { Model } from '../data/Model.js';
import { elasticsearch } from '../spconfig.js';

var dataMgr = {
    getSingleton: (function() { // BEGIN iife
        var singleton;
        var _data = [];
        return function() {
            if (!singleton) {
                singleton = {
                    init: function() {
                        console.log("initialize datamgr");
                        _data.push(new Model(
                            {topic: "Deception",
                            subTopic: "Country",
                            queryType: "ES",
                            query: elasticsearch.DeceptiveCountry,
                            server: elasticsearch.server}));
                        _data.push(new Model(
                            {topic: "Country",
                            subTopic: "Indicator",
                            queryType: "ES",
                            query: elasticsearch.CountrybyIndicator,
                            server: elasticsearch.server}));
                        _data.push(new Model(
                            {topic: "Country",
                            subTopic: "Country",
                            queryType: "ES",
                            query: ' ',
                            server: elasticsearch.server}));
                    },
                    getDS: function(opts) {
                        console.log("get DS");
                        var ds;
                        _data.forEach(e =>{
                            var lds = e.match(opts);
                            if (lds != null){
                                ds=lds;
                            }
                        })
                        return ds;
                    }
                }
                singleton.init();
            }
            return singleton;
        };
    }()) 
};
export {dataMgr}
/*
class DataMgr {
    constructor() {
        this._data = [];
    }

    init(){
        this._data.push(new Model({topic: "Deception",subTopic: "Country"}));
    }
    
    add(item) {
        this._data.push(item);
    }

    get(id) {
        return this._data.find(d => d.id === id);
    }
}

const dataMgr = new DataMgr();
Object.freeze(dataMgr);

export default dataMgr;

/*
export function DataMgr(opts) {
    console.log("Constructor:DataMgr");
    this.dataMgrs = [];
}

DataMgr.method("init", function () {
    this.dataMgrs.push(new Model({topic: "Deception",subTopic: "Country"}));
});

DataMgr.method("register", function (model) {
    this.dataMgrs.push(model);
});

DataMgr.method("find", function (topic,subTopic) {
    this.dataMgrs.forEach(e => {
        if (e.match(topic,subTopic) != null){
            return e;
        }
    });
});

var DataSrc = (function () {
    var instance;
 
    function createInstance() {
        var object = new DataMgr({});
        object.init();
        return object;
    }
 
    return {
        getInstance: function () {
            if (!instance) {
                instance = createInstance();
            }
            return instance;
        }
    };
})();

export default DataSrc;
*/