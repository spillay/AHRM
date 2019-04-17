import React, { Component } from 'react';
import withTour from '../../components/tour/withTour';
import withNav from '../../components/navigation/withNav';

import TicketPanel from '../../components/Views/TicketPanel';
import Notification from '../../components/Views/Notification';
import Header from '../../components/navigation/Header';
import SPPanel from '../../components/Views/ui/SPPanel'
import Donut from '../../components/Views/ui/Donut';

import { DataHelper } from 'DataManager';
import { DateHelper } from 'DataManager';

import { colorPalette } from '../../components/config/EmotionConfig.js';
import { strokePalette } from '../../components/config/EmotionConfig.js';
import EmoService from '../../components/security/EmoService';
import ESService from '../../components/security/ESService';
import withAuth from '../../components/security/withAuth';

import {
  Tooltip,
  XAxis, YAxis, Area,
  CartesianGrid, AreaChart, Bar, BarChart,
  ResponsiveContainer
} from 'recharts';

import {
  MenuItem,
  DropdownButton,
  Panel, PageHeader, ListGroup, ListGroupItem, Button,
} from 'react-bootstrap';



// Steps to provide a guided tour of the component
const steps = [
  {
    target: '#org-select',
    content: 'Please select a date range to view the emotion distribution graphs',
    placement: 'bottom',
  },
  {
    target: '#org-area',
    content: 'This Area Graph displaying the emotions detected across the organisation for the selected Time Period',
    placement: 'bottom',
  },
  {
    target: '#org-bar',
    content: 'This Bar Graph displaying the emotions detected across the organisation for the selected Time Period',
    placement: 'bottom',
  },
  {
    target: '#org-donut',
    content: 'This Donut Graph displaying the emotions detected across the organisation for the selected Time Period',
    placement: 'bottom',
  }
]

class Home extends Component {
  constructor(props) {
    super(props)
    this.state = {
      data: [],
      dataDonut: [],
      pivotDate: "05/05/2019",
      dh: new DataHelper()
    };
  }
  onSelect = (target) => {
    console.log("Outputting from here: ", target);
    if (target == 1) {
      var dt = new DateHelper(this.state.pivotDate);
      var range = dt.get24HourRange();
      console.log("--------------------" + range.start + " : " + range.end)
      var query = this.getSeries("1h", range.start, range.end);
      this.executeQuery(query, this.updateData);
      var query2 = this.getTerms(range.start, range.end);
      console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>." + query2);
      this.executeQuery(query2, this.updateDataDonut);
    }
    if (target == 2) {
      var dt = new DateHelper(this.state.pivotDate);
      var range = dt.getWeekRange();
      console.log("--------------------" + range.start + " : " + range.end)
      var query = this.getSeries("1d", range.start, range.end);
      console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>." + query);
      this.executeQuery(query, this.updateData);
      var query2 = this.getTerms(range.start, range.end);
      this.executeQuery(query2, this.updateDataDonut);
    }
    if (target == 3) {
      var dt = new DateHelper(this.state.pivotDate);
      var range = dt.getMonthRange();
      console.log("--------------------" + range.start + " : " + range.end)
      var query = this.getSeries("1w", range.start, range.end);
      this.executeQuery(query, this.updateData);
      console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>." + query);
      var query2 = this.getTerms(range.start, range.end);
      this.executeQuery(query2, this.updateDataDonut);
    }
    if (target == 4) {
      var dt = new DateHelper(this.state.pivotDate);
      var range = dt.getYearRange();
      console.log("--------------------" + range.start + " : " + range.end)
      var query = this.getSeries("1M", range.start, range.end);
      console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>." + query);
      this.executeQuery(query, this.updateData);
      var query2 = this.getTerms(range.start, range.end);
      this.executeQuery(query2, this.updateDataDonut);
    }

  }

  updateData = (resp) => {
    // console.log("response",resp)
    var query = this.state.dh.getQuery("Prime", "Series")
    console.log(query);
    var dataposition = query.dataposition;
    var data = resp;
    console.log(data);
    dataposition.forEach(s => {
      console.log(s, "look for ", data[s])
      data = data[s];
    });
    var esData = data;
    var graphData = [];

    esData.forEach(it => {
      //console.log(it)
      var jsonData = {};
      jsonData["name"] = it.key_as_string;
      it.prime.buckets.forEach(b => {
        //console.log(b.key);
        var key = b.key;
        var val = b.doc_count;
        jsonData[key] = val;
      })
      graphData.push(jsonData);

    })
    this.setState({
      data: graphData
    })
  }
  updateDataDonut = (resp) => {
    console.log("update donut");
    var query = this.state.dh.getQuery("Prime", "Terms")
    var dataposition = query.dataposition;

    var data = resp;
    dataposition.forEach(s => {
      console.log("look for ", data[s])
      data = data[s];
    });
    var esData = data;
    console.log(esData);
    var graphData = [];
    esData.forEach(i => {
      var jsonData = {};
      jsonData["label"] = i.key;
      jsonData["value"] = i.doc_count;
      graphData.push(jsonData);
      console.log(i.key + " : " + i.doc_count)
    })
    this.setState({
      dataDonut: graphData
    })
  }
  getSeries = (interval, start, end) => {
    var query = this.state.dh.getQuery("Prime", "Series")
    query.cleanFilters()
    query.addTimeSeriesParams("model.date", interval, "6", start, end);
    var range = { "start": start, "end": end };
    var timeFilter = this.state.dh.getTimeFilter(range);
    query.addFilterObj(timeFilter);
    return query.getFullQuery();
  }
  getTerms = (start, end) => {
    var query = this.state.dh.getQuery("Prime", "Terms")
    query.cleanFilters()
    var range = {
      "start": start,
      "end": end
    }
    var filter = this.state.dh.getTimeFilter(range);
    console.log("filter" + filter.getFilter())
    query.addFilter(filter);
    return query.getFullQuery();
  }
  executeQuery = (query, updateFunc) => {
    var essvr = new ESService();
    essvr.getData(JSON.parse(query)).then(function (resp) {
      console.log("//////////////////////////////////////////// " + resp);
      updateFunc(JSON.parse(resp))
    }, function (error) {
      console.log("----------------------------" + error.message);
    });
    // essvr.getData(JSON.parse(query)).then(updateFunc, function (error) {
    //   console.log("----------------------------" + error.message);
    // });
  }
  render() {
    return (
      <div>
        <Header />
        <div id="page-wrapper" className="page-wrapper">
          <div className="row">
            <div className="col-lg-12">
              <PageHeader>Emotion Analytics Dashboard</PageHeader>
            </div>
          </div>
          <TicketPanel />
          <div className="row">
            <div className="col-lg-8">
              <SPPanel id="org-area" panelCustomClass="graphPanel" bodyCustomClass="areaBody"
                header={<span>
                  <i className="fa fa-bar-chart-o fa-fw" /> Overview of Emotion Analysis
              <div id="org-select" className="pull-right">
                    <DropdownButton  title="Date Range" bsSize="xs" pullRight id="dropdownButton1" onSelect={this.onSelect}>
                      <MenuItem eventKey="1">Last 24 Hours</MenuItem>
                      <MenuItem eventKey="2">Last Week</MenuItem>
                      <MenuItem eventKey="3">Last Month</MenuItem>
                      <MenuItem eventKey="4">Last Year</MenuItem>
                    </DropdownButton>
                  </div>
                </span>}
                body={<span>
                  <div>
                    <ResponsiveContainer width="100%" aspect={2}>
                      <AreaChart data={this.state.data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }} >
                        <defs>
                          <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="5%" stopColor="#8884d8" stopOpacity={0.8} />
                            <stop offset="95%" stopColor="#8884d8" stopOpacity={0} />
                          </linearGradient>
                          <linearGradient id="colorPv" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="5%" stopColor="#82ca9d" stopOpacity={0.8} />
                            <stop offset="95%" stopColor="#82ca9d" stopOpacity={0} />
                          </linearGradient>
                        </defs>
                        <XAxis dataKey="name" />
                        <YAxis />
                        <CartesianGrid strokeDasharray="3 3" />
                        <Tooltip />
                        <Area type="monotone" dataKey="Joy" stackId="1" stroke={strokePalette['Joy']} fill={colorPalette['Joy']} />
                        <Area type="monotone" dataKey="Fear" stackId="1" stroke={strokePalette['Fear']} fill={colorPalette['Fear']} />
                        <Area type="monotone" dataKey="Sadness" stackId="1" stroke={strokePalette['Sadness']} fill={colorPalette['Sadness']} />

                        <Area type="monotone" dataKey="Disgust" stackId="1" stroke={strokePalette['Disgust']} fill={colorPalette['Disgust']} />
                        <Area type="monotone" dataKey="Pride" stackId="1" stroke={strokePalette['Pride']} fill={colorPalette['Pride']} />
                        <Area type="monotone" dataKey="Anxiety" stackId="1" stroke={strokePalette['Anxiety']} fill={colorPalette['Anxiety']} />
                        <Area type="monotone" dataKey="Agreeableness" stackId="1" stroke={strokePalette['Agreeableness']} fill={colorPalette['Agreeableness']} />
                        <Area type="monotone" dataKey="Contentment" stackId="1" stroke={strokePalette['Contentment']} fill={colorPalette['Contentment']} />
                        <Area type="monotone" dataKey="Relief" stackId="1" stroke={strokePalette['Relief']} fill={colorPalette['Relief']} />
                        <Area type="monotone" dataKey="Interest" stackId="1" stroke={strokePalette['Interest']} fill={colorPalette['Interest']} />
                      </AreaChart>
                    </ResponsiveContainer>
                  </div>
                </span>}
              >
              </SPPanel>
              <SPPanel id="org-bar" panelCustomClass="graphPanel" bodyCustomClass="barBody"
                header={<span>
                  <i className="fa fa-bar-chart-o fa-fw" /> Bar Chart View of Emotional Analysis
              </span>}
                body={<span>
                  <div>
                    <ResponsiveContainer width="100%" aspect={2}>
                      <BarChart data={this.state.data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }} >
                        <CartesianGrid stroke="#ccc" />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="Joy" stackId="1" fill={colorPalette['Joy']} />
                        <Bar dataKey="Fear" stackId="1" fill={colorPalette['Fear']} />
                        <Bar dataKey="Sadness" stackId="1" fill={colorPalette['Sadness']} />
                        <Bar dataKey="Contentment" stackId="1" fill={colorPalette['Contentment']} />
                        <Bar dataKey="Disgust" stackId="1" fill={colorPalette['Disgust']} />
                        <Bar dataKey="Pride" stackId="1" fill={colorPalette['Pride']} />
                        <Bar dataKey="Anxiety" stackId="1" fill={colorPalette['Anxiety']} />
                        <Bar dataKey="Agreeableness" stackId="1" fill={colorPalette['Agreeableness']} />
                        <Bar dataKey="Relief" stackId="1" fill={colorPalette['Relief']} />
                        <Bar dataKey="Interest" stackId="1" fill={colorPalette['Interest']} />
                      </BarChart>
                    </ResponsiveContainer>
                  </div>
                </span>}
              >
              </SPPanel>
            </div>
            <div className="col-lg-4">
              <Notification />
              <SPPanel id="org-donut" panelCustomClass="graphPanel" bodyCustomClass="graphBody"
                header={<span>
                  <i className="fa fa-bar-chart-o fa-fw" /> Donut View of Emotional Analysis
              </span>}
                body={<span>
                  <div>
                    <hr/>
                    <Donut id="org-donut" data={this.state.dataDonut} color="#8884d8" innerRadius="70%" outerRadius="90%" legend={false} />
                  </div>
                </span>}
              >
              </SPPanel>
            </div>

          </div>
        </div>
      </div>
    );
  }
}

export default withAuth(withTour(Home, steps));
        /*
  <div className="col-lg-4">
          <Panel
            header={<span>
              <i className="fa fa-bar-chart-o fa-fw" /> Emotion Distribution</span>}
          >
            <div>
              <Donut data={this.state.dataDonut} color="#8884d8" innerRadius="70%" outerRadius="90%" />
            </div>
          </Panel>
        </div>
        */