
import { Model } from '../dist/data/Model.js'
import { db } from '../dist/config.js';
import { settings } from '../dist/config.js';
import { devsettings } from '../dist/config.js';
import { Filter } from '../dist/data/query/Filter.js';
import es from 'elasticsearch';

const MONTH_NAMES = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
];

export default class DataHelper {
    constructor() {
        this.model = new Model(db,settings)
    }
    getServer(){
        return settings.server;
    }
    generateTimeFilter(time,category){
        var f =new Filter({
            filterType: "must",
            filterSub: "Time",
            category: category,
            Time: time,
            filterTimeTemplate: settings.TimeFilter 
        });
        return f;
    }
    generateMatchFilter(category,type,map,value){
        console.log("category" + category);
        var f =new Filter({
            filterType: type,
            filterSub: "match",
            category: category
        });
        f.addParam("map",map);
        f.addParam("value",value);
        return f;
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
    getQuery(topic,querytype) {
        console.log("getQuery")
        var q = this.model.getQuery(topic,querytype)
        if ( q != null){
            return q
        }
        return "None"
    }
   
    convertProcDate(dte){
        var d = new Date(dte);
        console.log(d);
        var m = ("0" + (d.getMonth() + 1)).slice(-2);
        var dd = ("0" + (d.getDate())).slice(-2);
        var str = m + "-" + dd + "-" + d.getFullYear();
        return str;
    }
    convertDate(dte){
        var d = new Date(dte);
        var str = MONTH_NAMES[d.getMonth()] + "," + d.getDate() + "," + d.getFullYear();
        return str;
    }
    cleanString(str){
        return str.replace(new RegExp('"', 'gi'), '');
    }
    transform(row,email){
        var it = {}
        it["date"] = this.convertDate(row._source.model.date);
        it["epochdate"] = row._source.model.date;
        it["to"] = row._source.model.to;
        it["from"] = row._source.model.from;
        var longContent = row._source.model.textContent;
        var content = longContent;
        if (longContent.length>300){
            content = longContent.substr(0,300) + "  ...";
        }
        it["content"] = content;
        it["longContent"] = longContent;
        it["prime"] =row._source.prime;
        it["norm"] = row._source.norm;
        it["id"] = row._id;
        if (row._source.model.to == email){
            it["box"] = "inbox";
        }
        if (row._source.model.from == email){
            it["box"] = "sent";
        }
        // Note if messages are going to multple people we need to deal with this case
        return it;
    }
    getEmails(email) {
        let promises = [];

        var emailAdd = "\"" + email + "\"";
        var model = new Model(db, settings);

        var inboxquery = model.getQuery("To", "Simple");
        inboxquery.addParams({ "VALUE": emailAdd });
        var inboxRes = inboxquery.getFullQuery();

        var outboxquery = model.getQuery("From", "Simple");
        outboxquery.addParams({ "VALUE": emailAdd });
        var sentRes = outboxquery.getFullQuery();


        var client = new es.Client(JSON.parse(devsettings.server));

        promises.push(client.search(JSON.parse(inboxRes)));
        promises.push(client.search(JSON.parse(sentRes)));

        Promise.all(promises)
            .then((results) => {
                console.log("All done", results);
                let all = [];

                results.forEach(r => {
                    all = all.concat(this.getData(r, inboxquery.dataposition));

                })
                var cnt = 0;
                all.sort(function (a, b) {
                    var c = new Date(a._source.model.date);
                    var d = new Date(b._source.model.date);
                    return c - d;
                });
                all.forEach(i => {
                    //console.log(cnt + " : " + JSON.stringify(i._source.model.date) + " : " + JSON.stringify(i._source.model.to) + " : " + JSON.stringify(i._source.model.from) + " : " + JSON.stringify(i._source.model.textContent) + " : " + JSON.stringify(i._source.prime));
                    cnt = cnt + 1;
                })

            })
            .catch((e) => {
                console.log(e);
            });
    }

}
