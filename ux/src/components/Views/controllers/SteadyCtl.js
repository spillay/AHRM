import React from 'react';
import {
    Panel,
    Button,
    Col,
    PageHeader,
    ControlLabel,
    FormControl,
    HelpBlock,
    FormGroup,
    Checkbox,
    Form,
    Radio,
    InputGroup,
    Glyphicon,
    Grid,
    Row,
    Clearfix,
    MenuItem,
    DropdownButton,
    ListGroup, ListGroupItem,
    Modal
} from 'react-bootstrap';

import DataHelper  from '../../SP/data/DataHelper';
import DateHelper  from '../../SP/data/DateHelper';
import SPLineChart from '../ui/SPLineChart.js';
import { emotionPalette } from '../../config/EmotionConfig.js';
import { graphPalette } from '../../config/EmotionConfig.js';
import { strokePalette } from '../../config/EmotionConfig.js';
import SPPanel from '../ui/SPPanel.js';
import EmoService from '../../security/EmoService';
import ESService from '../../security/ESService';
import ModalView from '../ModalView';
import ReactSpeedometer from "react-d3-speedometer";
import SimpleBarChart from '../ui/SimpleBarChart';
import EntropyHandler from '../../BL/EntropyHandler';
import Spinner from '../ui/Spinner';

const chkData = [
    { "name": "2000-12-31T00:00:00.000-05:00", "Contentment": 0, "Joy": 0, "Sadness": 0, "Unknown": 1 },
    { "name": "2001-01-01T00:00:00.000-05:00", "Contentment": 0, "Joy": 0, "Sadness": 0, "Unknown": 1 }]

export default class SteadyCtl extends React.Component {
    constructor(props) {
        super(props);
        this.log("show in constructor" + this.props.pivotDate + this.props.email, 1)

        var dhandler = new DataHelper();
        this.log(this.props.pivotDate);
        var pivotDate = dhandler.convertProcDate(this.props.pivotDate);
        this.log(pivotDate);
        var email = this.props.email;
        if (email.indexOf(",") > 0) {
            email = email.slice(0, email.indexOf(","));
        }
        this.log(email);
        this.state = {
            pivotDate: pivotDate,
            email: email,
            interval: this.props.interval,
            data: [],
            contentShow: false,
            entropy: 0,
            emailSent: 0,
            isloading: true,
            selected: "Date Range"
        };
        if (this.props.load === true) {
            var dte = new DateHelper(this.state.pivotDate);
            this.fetchData(dte.get24HourRange(), "1h");
        }
        this.log("after construct");
    }
    log = (mesg, type) => {
        if (type === 10) {
            console.log(mesg)
        }
    }
    handleContentClose = (e) => {
        this.setState({ contentShow: false })
    }
    componentDidUpdate() {
        this.log("update ------------------------> LineChartCtl");
        var dhandler = new DataHelper();
        var pivotDate = dhandler.convertProcDate(this.props.pivotDate);
        this.log(this.props.email + " : " + this.state.email)
        this.log(pivotDate + " : " + this.state.pivotDate)
        if (this.props.email !== this.state.email || pivotDate !== this.state.pivotDate) {
            //this.log("============================ updating all for LineChartCtl")
            var email = this.props.email;
            if (email.indexOf(",") > 0) {
                email = email.slice(0, email.indexOf(","));
            }
            this.log(email);
            this.setState({
                data: [],
                pivotDate: pivotDate,
                email: email
            });

        }
    }
    getEmotions = (norm, date, from) => {
        if (norm === "") {
            norm = {}
        } else {
            norm = JSON.parse(norm)
        }
        this.log(norm);
        var res = {}
        var emotions = ['Agreeableness', 'Anger', 'Anxiety', 'Contentment', 'Disgust', 'Fear', 'Interest', 'Joy', 'Pride', 'Relief', 'Sadness', 'Shame']
        emotions.forEach(e => {
            //this.log("================================", e, norm, norm[e]);
            if (norm[e] !== undefined) {
                //this.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>norm" + norm[e],10)
                res[e] = norm[e];
            } else {
                res[e] = 0.0;
            }
        })
        var wordemotions = ['Word-Agreeableness', 'Word-Anger', 'Word-Anxiety', 'Word-Contentment', 'Word-Disgust', 'Word-Fear', 'Word-Interest', 'Word-Joy', 'Word-Pride', 'Word-Relief', 'Word-Sadness', 'Word-Shame']
        wordemotions.forEach(e => {
            res[e] = '';
        })
        var dateHelp = new DateHelper();
        var strDate = dateHelp.getStr(date, "YYYY-MM-DD");
        res["date"] = strDate;
        var timeDate = dateHelp.getStr(date, "YYYY-MM-DD HH:MM:SS");
        res["time"] = timeDate;
        res["neg"] = 0;
        res["sender"] = from;
        return res;
    }
    setData = (steady) => {
        this.log("setdata", 10)
        this.log(steady, 10)
        var graphData = []
        //var p = JSON.parse(steady)
        // for (var key in p) {
        //     console.log(key)
        //     if (p.hasOwnProperty(key)) {
        //         console.log(key + " -> " + p[key]["sender"]);
        //         if (key !== "neg") {
        //             var item = {}
        //             item["x"] = key
        //             item["y"] = p[key]["sender"]
        //             item["color"] = graphPalette[key];
        //             graphData.push(item)
        //         }
        //     }
        // }
        //console.log(p)
        steady.forEach(k=>{
            console.log(k)
            var item = {}
            item["x"] = k[0]
            item["y"] = k[1]
            item["color"] = graphPalette[k[0]];
            graphData.push(item)
        })
        this.log(JSON.stringify(graphData), 10)
        this.setState({ data: graphData,isloading:false })
    }
    updateData = (resp) => {
        this.log("Updating data");
        var data = JSON.parse(resp);
        var dh = new DataHelper();
        var query = dh.getModel().getQuery("From", "SimpleFilter");

        var ndata = dh.getData(data, query.dataposition);
        this.log(JSON.stringify(ndata),10);
        var graphData = [];

        var cnt = 0;
        ndata.forEach(it => {
            this.log(it, 1)
            cnt = cnt + 1;
            var jsonData = this.getEmotions(it._source.norm, it._source.model.date, it._source.model.from);
            graphData.push(jsonData);

        })
        this.log(cnt, 10)
        this.setState({ emailSent: cnt })

        if (graphData.length === 0) {
            this.setState({ data: [], contentShow: true });
        } else {
             // Get Entropy from the server
            this.log("graphData------------------------------------", 10)
            this.log(graphData, 10)
            var res = {}
            res["text"] = graphData;
            var essvr = new EmoService();
            var that = this
            console.log("=============================================================before entropy")
            var ent = new EntropyHandler(graphData)
            var handlerData = ent.processByDay(graphData)
            if (handlerData.states.length > 0){
                essvr.getEntropy(handlerData).then((resp) => {
                    that.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + resp, 1)
                    var rdata = JSON.parse(resp)
                    that.log(rdata["norm_steady_states"], 1)
                    var steady = rdata["norm_steady_states"]
                    that.setData(steady)
                }).catch(function (e) {
                    that.log("Cannot connect to Server" + e, 1)
                });
                this.forceUpdate();
                this.log("completed");
            } else {
                alert("No Data Found for Steady States")
            }
        }
    }
    onSelect = (target) => {
        this.setState({ isloading: true })
        var dte = new DateHelper(this.state.pivotDate);
        if (target == 1) {
            this.fetchData(dte.get24HourRange(), "1h");
            this.state.selected = "Last 24 Hours";
        }
        if (target == 2) {
            this.fetchData(dte.getWeekRange(), "1d");
            this.state.selected = "Last Week";
        }
        if (target == 3) {
            this.fetchData(dte.getMonthRange(), "1w");
            this.state.selected = "Last Month";
        }
        if (target == 4) {
            this.fetchData(dte.getYearRange(), "1M");
            this.state.selected = "Last Year";
        }
        if (target == 5) {
            this.fetchData("none", "1M");
            this.state.selected = "All";
        }

        this.forceUpdate();

    }
    
    fetchData = (range, interval) => {
        var dh = new DataHelper();
        var dte = new DateHelper(this.state.pivotDate);

        var query = dh.getModel().getQuery("From", "SimpleFilter");

        if (range !== "none") {
            var timeFilter = dh.generateTimeFilter(range, "filter");
            query.addFilterObj(timeFilter);
        }
        var matchFilter = dh.generateMatchFilter("query", "must", "model.from.keyword", this.state.email);
        query.addFilterObj(matchFilter);

        // this.log(query.getFiltersCategory());
        var res = query.getFullQuery();



        this.log(res, 1);

        // var client = new es.Client(JSON.parse(dh.getServer()));

        // client.search(JSON.parse(res)).then((resp) => this.updateData(resp));

        var essvr = new ESService();
        essvr.getData(JSON.parse(res)).then((resp) => this.updateData(resp));
    }

    render() {
        this.log("rerendering");
        this.log(this.state.data);
        return (<div>
            <SPPanel headerCustomClass="contentHeading" bodyCustomClass="contentBody" panelCustomClass="contentPanel"
                header={<span>
                    <i className="fa fa-clock-o fa-fw" /> Steady Emotional States
                    <div className="pull-right">
                        <DropdownButton title={this.state.selected} bsSize="xs" pullRight id="dropdownButton1" onSelect={this.onSelect}>
                            <MenuItem eventKey="1">Last 24 Hours</MenuItem>
                            <MenuItem eventKey="2">Last Week</MenuItem>
                            <MenuItem eventKey="3">Last Month</MenuItem>
                            <MenuItem eventKey="4">Last Year</MenuItem>
                            <MenuItem eventKey="5">All</MenuItem>
                        </DropdownButton>
                    </div>
                </span>}
                body={<span>
                    {this.state.isloading && <Spinner height={250} width={650}/>}
                    {!this.state.isloading &&
                    <SimpleBarChart data={this.state.data} />
                    }
                </span>}
            >

            </SPPanel>

        </div>);
    }
}

/*

*/

