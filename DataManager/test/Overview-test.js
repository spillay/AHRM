import { expect } from 'chai';
import QueryMgr from '../dist/data/QueryMgr';
import { Model } from '../dist/data/Model';
import { db } from '../dist/config.js';
import { settings } from '../dist/config.js';
import { devsettings } from '../dist/config.js';
import DateHelper from '../dist/DateHelper.js';
import DataHelper from '../dist/DataHelper.js';
import es from 'elasticsearch';
import { Filter } from '../dist/data/query/Filter';

describe('Test utilities', () => {
  let model;
  let pivotDate = "01-01-2001";

  before(() => model = new Model(db, settings));

  after(() => model = undefined);


  it('chkDate', (done) => {
    var dte = new Date(pivotDate);
    expect(dte.getTime()).to.equal(978325200000);
    var dt = new DataHelper();
    var strdt = dt.convertProcDate(978325200000);
    expect(strdt).to.equal(strdt);
    done();
  });
});
describe('Test Date Ranges', () => {
  let model;
  let pivotDate = "01-01-2001";

  before(() => model = new Model(db, settings));

  after(() => model = undefined);


  it('24Hr Range', (done) => {
    var dte = new Date(pivotDate);
    expect(dte.getTime()).to.equal(978325200000);
    var dt = new DataHelper();
    var strdt = dt.convertProcDate(978325200000);
    expect(strdt).to.equal(pivotDate);
    var dh = new DateHelper(strdt);
    var range = dh.get24HourRange();
    console.log("start " + range.start + " end " + range.end);
    done();
  });
});


describe('Overview Tests used on main DashBoard', () => {
  let model;
  let pivotDate = "01/01/2001";

  before(() => model = new Model(db, settings));

  after(() => model = undefined);


  it('getSeries', (done) => {
    var dt = new DateHelper(pivotDate);
    var range = dt.get24HourRange();
    expect(range.start).to.equal("12-31-2000");
    expect(range.end).to.equal("01-01-2001");
    var query = model.getQuery("Prime", "Series")
    query.addTimeSeriesParams("model.date", "1h", "6", range.start, range.end);
    var timeFilter = new Filter({ "filterType": "must", "filterSub": "Time", "Time": range, "filterTimeTemplate": settings.TimeFilter });
    query.addFilterObj(timeFilter);
    var res = query.getFullQuery();

    //console.log(res);
    //console.log(devsettings.server);
    var client = new es.Client(JSON.parse(devsettings.server));
    client.search(JSON.parse(res)).then(function (resp) {
      var data = resp;
      expect(data).to.not.equal('');
      done();
    },
      function (error) {
        //console.log(error.message);
        done(error);
      });
  });

});
describe('Overview Tests used on main DashBoard', () => {
  let model;
  let pivotDate = "01/01/2001";

  before(() => model = new Model(db, settings));

  after(() => model = undefined);


  it('getTerms', (done) => {
    var dt = new DateHelper(pivotDate);
    var range = dt.get24HourRange();
    expect(range.start).to.equal("12-31-2000");
    expect(range.end).to.equal("01-01-2001");
    var query = model.getQuery("Prime", "Terms")
    query.addTimeSeriesParams("model.date", "1h", "6", range.start, range.end);
    var timeFilter = new Filter({ "filterType": "must", "filterSub": "Time", "Time": range, "filterTimeTemplate": settings.TimeFilter });
    query.addFilterObj(timeFilter);
    var res = query.getFullQuery();

    console.log(res);
    //console.log(devsettings.server);
    var client = new es.Client(JSON.parse(devsettings.server));
    client.search(JSON.parse(res)).then(function (resp) {
      var data = resp;
      expect(data).to.not.equal('');
      done();
    },
      function (error) {
        //console.log(error.message);
        done(error);
      });
  });

});
