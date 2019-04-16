
import { Model } from '../dist/data/Model.js'
import {db} from '../dist/config.js';
import {settings} from '../dist/config.js';
import {Filter} from '../dist/data/query/Filter.js'

/*
export default class DataManager  {
  constructor() {
    this.model = new Model(db,settings)
  }

  getQuery(topic,querytype) {
      console.log("getQuery")
      var q = this.model.getQuery(topic,querytype)
      if ( q != null){
          return q
      }
      return "None"
  }
  getServer(){
      return settings.server;
  }
  getTimeFilter(time){
    var f =new Filter({
        filterType: "must",
        filterSub: "Time",
        Time: time,
        filterTimeTemplate: settings.TimeFilter 
    });
    return f;
  }
  getData(data,dataposition){
    dataposition.forEach(s=>{
        data = data[s];
    });
    return data;
  }
  getModel(){
      return this.model;
  }
  
}
*/