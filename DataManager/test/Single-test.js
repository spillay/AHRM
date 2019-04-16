import { expect } from 'chai';
import QueryMgr from '../dist/data/QueryMgr';
import { Model } from '../dist/data/Model';
import { db } from '../dist/config.js';
import { settings } from '../dist/config.js';
import { devsettings } from '../dist/config.js';
import DataHelper from '../dist/DataHelper.js';
import es from 'elasticsearch';
import DateHelper from '../src/DateHelper';
import { Filter } from '../dist/data/query/Filter';
import GraphHelper from '../dist/GraphHelper'

import DataNode from '../dist/data/graph/DataNode';


describe('Emotion Time Line Incoming Emails', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getEmotions', (done) => {
        var dte = new DateHelper("01/01/2001");
        var range = dte.get24HourRange();
        var query = model.getQuery("From", "SimpleFilter");
        var dh = new DataHelper();


        var timeFilter = dh.generateTimeFilter(range,"filter");
        query.addFilterObj(timeFilter);
        
        var matchFilter = dh.generateMatchFilter("query","must","model.from.keyword","s..shively@enron.com");
        query.addFilterObj(matchFilter);
        
        // console.log(query.getFiltersCategory());
        var res = query.getFullQuery();
        console.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        console.log(res);
        
        var client = new es.Client(JSON.parse(devsettings.server));

        client.search(JSON.parse(res)).then(function (resp) {
            var data = resp;
            //console.log(data);
            expect(data).to.not.equal('');
            var dh = new DataHelper();
            var ndata = dh.getData(data, query.dataposition);
            expect(ndata.length).to.gt(0);
            //console.log(ndata);
            var odata = ndata.filter(n=>n._source.model.to != "s..shively@enron.com")
            odata.forEach(n=>{
                console.log(n._source.model.to);
            })
            console.log(ndata);
            done();
        },
            function (error) {
                done(error);
            });
    });

});