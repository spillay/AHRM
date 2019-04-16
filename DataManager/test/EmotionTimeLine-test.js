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


describe('Search for a user using Regex', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getUsers', (done) => {
        done();
    });

});

describe('Emotion Time Line Incoming Emails', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getInbox', (done) => {
        var dte = new DateHelper("01/01/2001");
        var range = dte.get24HourRange();
        var query = model.getQuery("To", "Simple");
        query.addParams({ "VALUE": "\"s..shively@enron.com\"" });
        var dh = new DataHelper();
        var timeFilter = dh.getTimeFilter(range);
        query.addFilterObj(timeFilter);
        var res = query.getFullQuery();
        console.log(res);
        var client = new es.Client(JSON.parse(devsettings.server));

        client.search(JSON.parse(res)).then(function (resp) {
            var data = resp;
            //console.log(data);
            expect(data).to.not.equal('');
            var dh = new DataHelper();
            var ndata = dh.getData(data, query.dataposition);
            expect(ndata.length).to.gt(0);
            done();
        },
            function (error) {
                done(error);
            });
    });

});
describe('Emotion Series by Person', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getSeriesByPerson', (done) => {
        var dte = new DateHelper("01/01/2001");
        var query = model.getQuery("Prime", "Series");
        var range = dte.get24HourRange();
        dte.getSeries(query,'"index": "inbox-items"',"1d",6);
        //query.addParams({ "VALUE": "\"s..shively@enron.com\"" });
        var timeFilter = new Filter({"filterType":"must","filterSub":"Time","Time":range,"filterTimeTemplate":settings.TimeFilter});
        query.addFilterObj(timeFilter);
        query.addFilter("must","","{\"term\": { \"model.to.keyword\": \"s..shively@enron.com\" }}");
        var res = query.getFullQuery();
        console.log(res);
      
        var client = new es.Client(JSON.parse(devsettings.server));

        client.search(JSON.parse(res)).then(function (resp) {
            var data = resp;
            //console.log(data);
            expect(data).to.not.equal('');
            var dh = new DataHelper();
            var ndata = dh.getData(data, query.dataposition);
            expect(ndata.length).to.gt(0);
            done();
            },
            function (error) {
                done(error);
            });
    });

});

describe('Emotion Time Line Outgoing Emails', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getOutbox', (done) => {
        var query = model.getQuery("From", "Simple");
        query.addParams({ "VALUE": "\"s..shively@enron.com\"" });
        var res = query.getFullQuery();
        //console.log(res);
        var client = new es.Client(JSON.parse(devsettings.server));

        client.search(JSON.parse(res)).then(function (resp) {
            var data = resp;
            //console.log(JSON.stringify(data));
            expect(data).to.not.equal('');
            var dh = new DataHelper();
            var ndata = dh.getData(data, query.dataposition);
            expect(ndata.length).to.gt(0);
            done();
        },
            function (error) {
                done(error);
            });
    });

});
describe('Emotion Time Line All Emails', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getAll', (done) => {
        var dh = new DataHelper();
        dh.getEmails("s..shively@enron.com");
        done();
    });

});

describe('Emotion Time Line Incoming Emails', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getInbox', (done) => {
        var dte = new DateHelper("01/01/2001");
        var range = dte.get24HourRange();
        var query = model.getQuery("To", "SimpleFilter");
        var dh = new DataHelper();


        var timeFilter = dh.generateTimeFilter(range,"filter");
        query.addFilterObj(timeFilter);
        
        var matchFilter = dh.generateMatchFilter("query","must","model.to","xxs..shively@enron.com");
        query.addFilterObj(matchFilter);
        
        console.log(query.getFiltersCategory());
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
            done();
        },
            function (error) {
                done(error);
            });
    });

});

