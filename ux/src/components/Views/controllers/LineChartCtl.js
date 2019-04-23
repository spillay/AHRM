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
import SimpleLineChart from '../ui/SimpleLineChart';

const chkData = [
    { "name": "2000-12-31T00:00:00.000-05:00", "Contentment": 0, "Joy": 0, "Sadness": 0, "Unknown": 1 },
    { "name": "2001-01-01T00:00:00.000-05:00", "Contentment": 0, "Joy": 0, "Sadness": 0, "Unknown": 1 }]

export default class LineChartCtl extends React.Component {
    constructor(props) {
        super(props);
        this.log("show in constructor" + this.props.pivotDate)

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
            contentShow: false
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
    getEmotionValue = (it, emo) => {
        var emoValue = 0;
        it.prime.buckets.forEach(b => {
            //console.log(b, emo)
            if (b.key === emo) {
                console.log(b.key,b.doc_count)
                emoValue = b.doc_count
            }
        });
        console.log("emoValue",emoValue)
        return emoValue;
    }
    updateData = (resp,interval) => {
        this.log("Updating data");
        var data = JSON.parse(resp);
        var dh = new DataHelper();
        var dte = new DateHelper();
        var query = dh.getModel().getQuery("Prime", "Series");

        var ndata = dh.getData(data, query.dataposition);
        this.log(JSON.stringify(ndata));
        var graphData = [];
        var emoData = []
        emotionPalette.forEach(e => {
            var emo = {}
            emo["name"] = e.emotion
            emo["values"] = []
            emoData.push(emo)
        })
        ndata.forEach(it => {
            emotionPalette.forEach(e => {
                var pData = {}
                if (interval=="1h"){
                    pData["x"] = dte.getStr(it.key,"hh:mm")
                } else {
                    pData["x"] = dte.getStr(it.key,"MM-DD-YY hh:mm") //it.key_as_string;
                }
                pData["y"] = this.getEmotionValue(it, e.emotion)
                emoData.forEach(b => {
                    if (b["name"] === e.emotion) {
                        b["values"].push(pData)
                    }
                })
            });
        })
        this.log("data values+++++++++++++++++++++++++++++++++++++++++" + JSON.stringify(emoData), 10);
        emoData.forEach(e=>{
            graphData.push(e["values"])
        })
        if (graphData.length === 0) {
            this.setState({ data: [], contentShow: true });
        } else {
            this.setState({ data: graphData, contentShow: false });
        }
        //this.forceUpdate();
        this.log("completed");
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
        var query = dh.getModel().getQuery("Prime", "Series");
        dte.getSeries(query, interval, 6);

        if (range !== "none") {
            var timeFilter = dh.getTimeFilter(range);
            query.addFilterObj(timeFilter);
        }

        query.addFilter("must", "", "{\"term\": { \"model.to.keyword\": \"" + this.state.email + "\" }}");
        var res = query.getFullQuery();
        this.log(res);

        // var client = new es.Client(JSON.parse(dh.getServer()));

        // client.search(JSON.parse(res)).then((resp) => this.updateData(resp));

        var essvr = new ESService();
        essvr.getData(JSON.parse(res)).then((resp) => this.updateData(resp,interval));
    }

    render() {
        this.log("rerendering");
        this.log(this.state.data);
        return (<div>
            <SPPanel headerCustomClass="contentHeading" bodyCustomClass="contentBody" panelCustomClass="contentPanel"
                header={<span>
                    <i className="fa fa-clock-o fa-fw" /> Emotion Line Chart
                    <div className="pull-right btn-group">
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
                    <div> <SimpleLineChart data={this.state.data}  title="Emotional Trends" /></div>
                </span>}
            >
            </SPPanel>
            <ModalView show={this.state.contentShow} closeaction={this.handleContentClose} title="Line Chart">
                <div>No Data Found, please try a different date range</div>
            </ModalView>
        </div>);
    }
}

/*

*/

