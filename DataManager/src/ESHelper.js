import es from 'elasticsearch';
import { settings } from './config.js';
import { devsettings } from './config';

export default class ESHelper {
    constructor(debug) {
        if (debug == undefined){
            this.client = new es.Client(JSON.parse(settings.server));
        } else {
            this.client = new es.Client(JSON.parse(devsettings.server));
        }
    }
    getData(query,updateFunc) {
        console.log("in getdata")
        this.client.search(JSON.parse(query)).then(function (resp) {
            console.log("in promise");
            updateFunc(resp);
        },function (error) {console.log(error);});
    }

}
