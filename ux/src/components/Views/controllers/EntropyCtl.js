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
import { DataHelper } from 'DataManager';
import { DateHelper } from 'DataManager';
import SPLineChart from '../ui/SPLineChart.js';
import { emotionPalette } from '../../config/EmotionConfig.js';
import SPPanel from '../ui/SPPanel.js';
import EmoService from '../../security/EmoService';
import ESService from '../../security/ESService';
import ModalView from '../ModalView';
import ReactSpeedometer from "react-d3-speedometer";
import EntropyHandler from '../../BL/EntropyHandler';

const chkData = [
    { "name": "2000-12-31T00:00:00.000-05:00", "Contentment": 0, "Joy": 0, "Sadness": 0, "Unknown": 1 },
    { "name": "2001-01-01T00:00:00.000-05:00", "Contentment": 0, "Joy": 0, "Sadness": 0, "Unknown": 1 }]

export default class EntropyCtl extends React.Component {
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
            emailSent: 0
        };
        if (this.props.load === true) {
            var dte = new DateHelper(this.state.pivotDate);
            this.fetchData(dte.get24HourRange(), "1h");
        }
        this.log("after construct");
    }
    log = (mesg, type) => {
        //if (type === 10) {
            console.log(mesg)
        //}
    }
    handleContentClose = (e) => {
        this.setState({ contentShow: false })
    }
    componentDidUpdate() {
        this.log("update ------------------------> EntropyCtl");
        var dhandler = new DataHelper();
        var pivotDate = dhandler.convertProcDate(this.props.pivotDate);
        this.log(this.props.email + " : " + this.state.email)
        this.log(pivotDate + " : " + this.state.pivotDate)
        if (this.props.email !== this.state.email || pivotDate !== this.state.pivotDate) {
            this.log("============================ updating all for LineChartCtl")
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
            this.log("================================", e, norm, norm[e]);
            if (norm[e] !== undefined) {
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
        this.log(res,10)
        return res;
    }
    updateData = (resp) => {
        this.log("Updating data");
        var data = JSON.parse(resp);
        var dh = new DataHelper();
        var query = dh.getModel().getQuery("From", "SimpleFilter");

        var ndata = dh.getData(data, query.dataposition);
        this.log(JSON.stringify(ndata));
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
            var res = {}
            res["text"] = graphData;
            this.log(JSON.stringify(graphData),1)
            var essvr = new EmoService();
            var that = this
            var ent = new EntropyHandler(graphData)
            var handlerData = ent.processByDay(graphData)
            this.log(handlerData,1)
            essvr.getEntropy(handlerData).then((resp) => {
                that.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>res -" + res, 1)
                that.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + resp, 10)
                var rdata = JSON.parse(resp)
                that.log(rdata["norm_entropy"], 10)
                that.setState({ entropy: rdata["norm_entropy"] })
            }).catch(function (e) {
                that.log("Cannot connect to Server" + e, 1)
            });
            this.forceUpdate();
            this.log("completed");
        }

    }
    onSelect = (target) => {
        var dte = new DateHelper(this.state.pivotDate);
        if (target == 1) {
            this.fetchData(dte.get24HourRange(), "1h");
        }
        if (target == 2) {
            this.fetchData(dte.getWeekRange(), "1d");
        }
        if (target == 3) {
            this.fetchData(dte.getMonthRange(), "1w");
        }
        if (target == 4) {
            this.fetchData(dte.getYearRange(), "1M");
        }
        if (target == 5) {
            this.fetchData("none", "1M");
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
        this.log(this.state,this.props);
        return (<div>
            <SPPanel headerCustomClass="contentHeading" bodyCustomClass="contentNoScrollBody2" panelCustomClass="contentPanel"
                header={<span>
                    <i className="fa fa-clock-o fa-fw" /> Entropy
                    <div className="pull-right">
                        <DropdownButton title="Date Range" bsSize="xs" pullRight id="dropdownButton1" onSelect={this.onSelect}>
                            <MenuItem eventKey="1">Last 24 Hours</MenuItem>
                            <MenuItem eventKey="2">Last Week</MenuItem>
                            <MenuItem eventKey="3">Last Month</MenuItem>
                            <MenuItem eventKey="4">Last Year</MenuItem>
                            <MenuItem eventKey="5">All</MenuItem>
                        </DropdownButton>
                    </div>
                </span>}
                body={<span>
                    <div>Total Emails sent for the Period is: {this.state.emailSent}</div>
                    <div>
                        <ReactSpeedometer
                            maxValue={1}
                            value={this.state.entropy}
                            needleColor="black"
                            startColor="blue"
                            segments={10}
                            endColor="red"
                            currentValueText="Entropy Value: ${value}"
                            height={180}
                            width={250}
                        />
                    </div>

                </span>}
            >

            </SPPanel>

        </div>);
    }
}

/*

*/

