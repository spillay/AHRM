/* global it, describe, before, after */
/* eslint import/no-extraneous-dependencies: ["error", {"devDependencies": true}] */


import { expect } from 'chai';
import QueryMgr from '../dist/data/QueryMgr';
import { Model } from '../dist/data/Model';
import { db } from '../dist/config.js';
import { settings } from '../dist/config.js';
import { devsettings } from '../dist/config.js';
import es from 'elasticsearch';

describe('Test to see if we can ping elasticsearch', () => {
  let model;

  before(() => model = new Model(db, settings));

  after(() => model = undefined);


  it('Ping', (done) => {
    var client = new es.Client(JSON.parse(devsettings.server));
    client.ping({
      requestTimeout: 30000,
    }, function (error) {
      if (error) {
        done(error);
      } else {
        done();
      }
    });
  });

});
