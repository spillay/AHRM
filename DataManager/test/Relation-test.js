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




describe('Emotion Multiple People', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('getSingleRelations', (done) => {
        var dte = new DateHelper("01/01/2001");
        var range = dte.get24HourRange();
        var query = model.getQuery("To", "Terms");
        var dh = new DataHelper();


        var timeFilter = dh.generateTimeFilter(range,"filter");
        query.addFilterObj(timeFilter);
        
        var matchFilter = dh.generateMatchFilter("query","must","model.from.keyword","s..shively@enron.com");
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
            console.log(ndata);
            console.log("before done");
            done();
        },
            function (error) {
                done(error);
            });
            
    });

});
describe('Emotion Multiple People', () => {
    let model;

    before(() => model = new Model(db, settings));

    after(() => model = undefined);


    it('callMultiple', (done) => {
        var dte = new DateHelper("01/01/2001");
        var range = dte.get24HourRange();
        var n = new DataNode("1","email",0);
        expect(n.getLevel()).to.equal(0);
        //console.log(n.getChildrenQuery(range));
        var c = n.addChild("c1","email");
        //console.log("Level:" + n.getLevel());
        n.addChild("c2","email",{cnt:2});
        n.addChild("c3","email",{cnt:2});
        n.addedAllChildren();
        expect(n.getLevel()).to.equal(1);
        c.addChild("d1","email",{cnt:1});
        c.addChild("d2","email",{cnt:2});
        c.addChild("1","email",{cnt:3});
        c.addedAllChildren();
        expect(n.getLevel()).to.equal(2);
        /*
        console.log(n.getNodeData());
        console.log(n.getLevelParent().id);

        n.getIncompleteNodes().forEach(i=>{
            console.log(i.id);
        });
        n.getLeafNodes().forEach(i=>{
            console.log(i.id);
        });
        
        */
        var gh = new GraphHelper();
        expect(gh.getGraphNodes(n.getNodeData()).length).to.equal(6);
       
        var data = gh.getGraphData(n);
        expect(data.nodes.length).to.equal(6);
        expect(data.links.length).to.equal(6);
        console.log(data.links);
            done();
        
    });

});
