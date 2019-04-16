var ds = require('../ds/DataSource');


export function ESDS(opts){
    this.query = opts.query;
    this.server = opts.server;
}

ESDS.method("getResult",function() {
    // var client = new es.Client(JSON.parse(this.server));
    // client.search(JSON.parse(this.query)).then(function (resp) {
    //     //console.log(d3);
    //     return resp.hits.hits;
    // });
});


