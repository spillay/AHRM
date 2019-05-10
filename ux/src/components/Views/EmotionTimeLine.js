import React from 'react';

import SPPanel from './ui/SPPanel'
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
    ListGroup, ListGroupItem
} from 'react-bootstrap';
import { DataHelper } from 'DataManager';
import { DateHelper } from 'DataManager';
import EmotionCard from './ui/EmotionCard.js';
import EmailAnalysisView from './EmailAnalysisView.js';
import EmailView from './EmailView.js';
import ModalView from './ModalView';
import EmoService from '../../components/security/EmoService';
import ESService from '../../components/security/ESService';
import Spinner from './ui/Spinner';




export default class EmotionTimeLine extends React.Component {
    constructor(props) {
        super(props);
        this.log("EmotionTimeLine Constr");
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
            data: [],
            pivotDate: pivotDate,
            email: email,
            emailVisibility: false,
            emailData: "",
            nodata: false,
            isloading: true
        };
        if (this.props.load === true){
            var dt = new DateHelper(this.state.pivotDate);
            var range = dt.get24HourRange();
            var inboxRes = this.getToQuery(range);
            var sentRes = this.getFromQuery(range);
            this.executeQuery(inboxRes, sentRes, this.updateData);
        }
    }
    log = (mesg, type) => {
        //if (type === 1) {
            console.log(mesg)
        //}
    }
    handleContentClose = () => {
        this.log("content close");
        this.setState({ emailVisibility: false });
    }
    handleDataContentClose = () => {
        this.log("content close");
        this.setState({ nodata: false });
    }
    getToQuery = (range) => {
        var dh = new DataHelper();
        var model = dh.getModel();
        var query = model.getQuery("To", "SimpleFilter");
        // When we require all of the records
        if (range !== "none"){
            var timeFilter = dh.generateTimeFilter(range, "filter");
            query.addFilterObj(timeFilter);
        }
        var matchFilter = dh.generateMatchFilter("query", "must", "model.to.keyword", this.state.email);
        query.addFilterObj(matchFilter);
        var res = query.getFullQuery();
        return res;
    }
    getFromQuery = (range) => {
        var dh = new DataHelper();
        var model = dh.getModel();
        var query = model.getQuery("From", "SimpleFilter");
        if (range !== "none"){    
            var timeFilter = dh.generateTimeFilter(range, "filter");
            query.addFilterObj(timeFilter);
        }
        var matchFilter = dh.generateMatchFilter("query", "must", "model.from.keyword", this.state.email);
        query.addFilterObj(matchFilter);
        var res = query.getFullQuery();
        return res;
    }
    onSelect = (target) => {
        this.log("Outputting from here: ", target);
        this.setState({ isloading: true })
        var dt = new DateHelper(this.state.pivotDate);
        var range;
        if (target == 1) {
            range = dt.get24HourRange();
        }
        if (target == 2) {
            range = dt.getWeekRange();
        }
        if (target == 3) {
            range = dt.getMonthRange();
        }
        if (target == 4) {
            range = dt.getYearRange();
        }
        if (target == 5) {
            range = "none";
        }
        var inboxRes = this.getToQuery(range);
        var sentRes = this.getFromQuery(range);
        
        this.log(inboxRes,1);
        this.log(sentRes,1);
        this.executeQuery(inboxRes, sentRes, this.updateData);

    }

    executeQuery = (inboxRes, sentRes, updateFunc) => {
        this.log("executeQuery");
        var dh = new DataHelper();
        let promises = [];

        this.log(inboxRes);
        this.log(sentRes);
        var dataposition = dh.getQuery("From", "SimpleFilter").dataposition;
        var essvr = new ESService();
   

        promises.push(essvr.getData(JSON.parse(inboxRes)));
        promises.push(essvr.getData(JSON.parse(sentRes)));

        Promise.all(promises)
            .then((results) => {
                this.log("All done", results);
                let all = [];

                results.forEach(r => {
                    all = all.concat(dh.getData(JSON.parse(r), dataposition));

                })
                var cnt = 0;
                all.sort(function (a, b) {
                    var c = new Date(a._source.model.date);
                    var d = new Date(b._source.model.date);
                    return c - d;
                });
                let ndata = [];
                all.forEach(i => {
                    var it = dh.transform(i,this.state.email)
                    ndata.push(it);

                    this.log(cnt + " : " + JSON.stringify(i._source.model.date) + " : " +
                        JSON.stringify(i._source.model.to) + " : " + JSON.stringify(i._source.model.from) + " : " +
                        JSON.stringify(i._source.model.textContent) + " : " + JSON.stringify(i._source.prime));

                    cnt = cnt + 1;
                })
                this.setState({
                    data: ndata,
                    isloading: false
                })
                if (ndata.length===0){
                    this.setState({nodata:true})
                }
            })
            .catch((e) => {
                this.log(e);
            });
    }
    getSeries = (interval, start, end) => {
        var dh = new DataHelper();
        var query = dh.getQuery("Prime", "Series")
        query.addTimeSeriesParams("model.date", interval, "6", start, end);
        //query.addTimeSeriesParams("model.date", "1h", "6", '01/01/2000', '25/03/2001');
        return query.getFullQuery();
    }
    updateData = (resp) => {
        this.log("update data",1)
        var dh = new DataHelper();
        var query = dh.getQuery("Prime", "Series")
        var dataposition = query.dataposition;
        var data = JSON.parse(resp);
        this.log(data);
        dataposition.forEach(s => {
            data = data[s];
        });
        var esData = data;
        var graphData = [];

        esData.forEach(it => {
            //this.log(it)
            var jsonData = {};
            jsonData["name"] = it.key_as_string;
            it.prime.buckets.forEach(b => {
                //this.log(b.key);
                var key = b.key;
                var val = b.doc_count;
                jsonData[key] = val;
            })
            graphData.push(jsonData);

        })
        this.log("++++++++++++++++++++++++++++++++++++++++++++++")
        this.setState({
            data: graphData,
            isloading: false
        })
        this.log(graphData,1)
        this.log(graphData.length,1)
        if (graphData.length===0){
            this.setState({nodata:true})
        }
    }

    onCardSelect = (e) => {
        this.log("card select " + e.currentTarget.id);
        var fd = null;
        this.state.data.forEach(i => {
            if (i.id == e.currentTarget.id) {
                fd = i;
            }
        })
        if (fd != null) {
            this.setState({ emailData: fd, emailVisibility: true });
            //this.emailModal.show(fd);
            //this.emailModal.forceUpdate();
        }

    }
    componentDidUpdate() {
        this.log("update ------------------------> after card select state " + this.state.emailVisibility);
        var dhandler = new DataHelper();
        var pivotDate = dhandler.convertProcDate(this.props.pivotDate);
        this.log(this.props.email + " : " + this.state.email)
        this.log(pivotDate + " : " + this.state.pivotDate)
        if (this.props.email !== this.state.email || pivotDate !== this.state.pivotDate) {
            this.log("============================ updating all")
            var email = this.props.email;
            if (email.indexOf(",") > 0) {
                email = email.slice(0, email.indexOf(","));
            }
            this.log(email);
            this.setState({
                data: [],
                pivotDate: pivotDate,
                email: email,
                emailVisibility: false,
                emailData: ""
            });
        }
    }
    render() {
        this.log("render");
        var testdata = [{ "prime": "Contentment", "content": "hello all", "box": "sent", "date": "March,24,2001" },
        { "prime": "Unknown", "content": "hello all", "box": "inbox", "date": "March,24,2001" },
        { "prime": "Contentment", "content": "hello all", "box": "sent", "date": "March,24,2001" }
        ];
        var cardSelect = this.onCardSelect;
        return (

            <div>
                <SPPanel headerCustomClass="contentHeading" bodyCustomClass="contentBody" panelCustomClass="contentPanel"
                    header={<span>
                        <i className="fa fa-clock-o fa-fw" /> Emotion Timeline
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
                        {this.state.isloading && <Spinner height={180} width={280}/>}
                        {!this.state.isloading && 
                        <div>
                            <ul className="timeline">

                                {this.state.data.map(function (d, i) {
                                    return <EmotionCard key={i} prime={d.prime} content={d.content} box={d.box} date={d.date} id={d.id} onCardSelect={cardSelect} />;
                                })}


                            </ul>
                        </div>
                        }
                    </span>}
                >

                </SPPanel>
                <ModalView show={this.state.emailVisibility} closeaction={this.handleContentClose} title="Email Analysis">
                    <EmailAnalysisView data={this.state.emailData} />
                </ModalView>
                <ModalView show={this.state.nodata} closeaction={this.handleDataContentClose} title="Email Analysis">
                    <div>No data found, please select a different date range</div>
                </ModalView>
            </div>

        )
    }
}
/*
*/