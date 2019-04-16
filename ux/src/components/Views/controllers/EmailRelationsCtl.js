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
import { DataHelper } from 'DataManager'
import { DateHelper } from 'DataManager'
import { DataNode } from 'DataManager'
import { GraphHelper } from 'DataManager'
import { DRelationsView } from '../RelationsView'
import SPPanel from '../ui/SPPanel.js';
import { DPanelView } from '../PanelView';
import EmoService from '../../security/EmoService';
import ESService from '../../security/ESService';
import SearchView from '../../Views/SearchView';
import NotificationForm from '../../Views/ui/NotificationForm';
import ModalView from '../../Views/ModalView';

function sanitizeJSON(unsanitized) {
    return unsanitized.replace(/\\/g, "\\\\").replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t").replace(/\f/g, "\\f").replace(/"/g, "\\\"").replace(/'/g, "\\\'").replace(/\&/g, "\\&");
}
function spsanitizeJSON(unsanitized) {
    return unsanitized.replace(/\\/g, "\\\\").replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t").replace(/\f/g, "\\f").replace(/"/g, "\\\"").replace(/\&/g, "\\&");
}



export default class EmailRelationsCtl extends React.Component {
    constructor(props) {
        super(props);
        console.log("Email Relations Ctl in constructor")

        this.state = {
            dh: new DataHelper(),
            date: "01/01/2001",
            data: { nodes: [], links: [] },
            email: "s..shively@enron.com",//"john.lavorato@enron.com", //"s..shively@enron.com",
            range: "",
            node: "",
            level: 1,
            showRelations: false,
            showNotification: false,
            showPanel: false,
            noresults: false,
            contentShow: false
        };
    }
    onLevel = (target) => {
        console.log("Outputting from level here: ", target);
        this.state.level = target;
        var dt = new DateHelper(this.state.date);
        var range = dt.get24HourRange();
        var interval = "1h";
        this.state.data = { nodes: [], links: [] };
        this.state.cnt = 0;
        this.state.range = range;
        this.state.interval = interval;
        this.getData(this.state.email, this.state.level);
    }
    onSelect = (target) => {
        console.log("Outputting from here: ", target, this.state.date);
        var dt = new DateHelper(this.state.date);
        var range;
        var interval;
        if (target == 1) {
            range = dt.get24HourRange();
            interval = "1h";
        }
        if (target == 2) {
            range = dt.getWeekRange();
            interval = "1d";
        }
        if (target == 3) {
            range = dt.getMonthRange();
            interval = "1w";
        }
        if (target == 4) {
            range = dt.getYearRange();
            interval = "1M";
        }
        this.state.data = { nodes: [], links: [] };
        this.state.cnt = 0;
        this.state.range = range;
        this.state.interval = interval;
        this.getData(this.state.email, this.state.level);
        //var data = {nodes:[{id: "s@enron.com",group:1},{id:"p@enron.com",group:1}],links:[{source: "s@enron.com",target:"p@enron.com",value: 2}]}
        //this.setState({data:this.getTestData()});
    }

    getTestData = () => {
        var data = {
            "nodes": [
                { "id": "s..shively@enron.com", "group": 0 },
                { "id": "john.lavorato@enron.com", "group": 0 },
                { "id": "karen.buckley@enron.com", "group": 0 },
                { "id": "airam.arteaga@enron.com", "group": 0 },
                { "id": "alex.villarreal@enron.com", "group": 0 },
                { "id": "chris.gaskill@enron.com", "group": 0 },
                { "id": "deirdre.mccaffrey@enron.com", "group": 0 },
                { "id": "frank.hayden@enron.com", "group": 0 },
                { "id": "hunter-jessica@houston.rr.com", "group": 0 },
                { "id": "lisa.kinsey@enron.com", "group": 0 },
                { "id": "mike.roberts@enron.com", "group": 0 }
            ],
            "links": [
                { "source": "s..shively@enron.com", "target": "john.lavorato@enron.com", "value": 2 },
                { "source": "s..shively@enron.com", "target": "karen.buckley@enron.com", "value": 2 },
                { "source": "s..shively@enron.com", "target": "airam.arteaga@enron.com", "value": 1 },
                { "source": "s..shively@enron.com", "target": "alex.villarreal@enron.com", "value": 1 },
                { "source": "s..shively@enron.com", "target": "chris.gaskill@enron.com", "value": 1 },
                { "source": "s..shively@enron.com", "target": "deirdre.mccaffrey@enron.com", "value": 1 },
                { "source": "s..shively@enron.com", "target": "frank.hayden@enron.com", "value": 1 },
                { "source": "s..shively@enron.com", "target": "hunter-jessica@houston.rr.com", "value": 1 },
                { "source": "s..shively@enron.com", "target": "lisa.kinsey@enron.com", "value": 1 },
                { "source": "s..shively@enron.com", "target": "mike.roberts@enron.com", "value": 1 }
            ]
        };
        return data;
    }
    NotExist = (id) => {
        this.state.data.nodes.forEach(n => {
            //console.log("id:" + n.id + " other " +id);
            if (n.id == id) {
                console.log("false");
                return false;
            }
        })
        return true;
    }

    updateNode = (node, resp, level) => {
        console.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        console.log(resp);
        node.addChildNodes(resp);
        console.log("Done for " + node.id);
        this.display();
        //if (this.state.node.getLevel() < level) {
        // this.populateAllChildren(node, this.state.range);
        //}
    }
    checkLevel = (level) => {
        console.log("Level:");
        console.log(this.state.node.getLevel());
        if (this.state.node.getLevel() < level) {
            var cnode = this.state.node.getLevelParent();
            this.processChildren(cnode, level);
        }
        this.display();
    }
    populateChildren = (node, range, level) => {
        var dh = new DataHelper();

        var essvr = new ESService();
        essvr.getData(JSON.parse(node.getChildrenQuery(range))).then((resp) => { node.addChildNodes(resp); this.checkLevel(level); })
    }
    populateAllChildren = (node, range, level) => {
        var dh = new DataHelper();
        var essvr = new EmoService();
        node.getChildNodes().forEach(c => {
            essvr.getData(JSON.parse(c.getChildrenQuery(range))).then((resp) => { c.addChildNodes(resp); this.checkLevel(level); });
        })
    }
    getEmotionQuery = (email) => {
        var dte = new DateHelper(this.state.pivotDate);
        var dh = new DataHelper();
        var query = dh.getModel().getQuery("Prime", "Series");
        dte.getSeries(query, this.state.interval, 6);
        var timeFilter = this.state.dh.getTimeFilter(this.state.range);
        query.addFilterObj(timeFilter);
        query.addFilter("must", "", "{\"term\": { \"model.to.keyword\": \"" + email + "\" }}");
        var res = query.getFullQuery();
        return res;
    }
    processEmotions = (data) => {
        console.log("processing emotions", this.state.data);
        var dh = new DataHelper();
        var query = this.state.dh.getModel().getQuery("Prime", "Series");
        let promises = [];
        // var client = new es.Client(JSON.parse(dh.getServer()));

        var essvr = new ESService();

        data.nodes.forEach(n => {
            console.log(n.id);
            var q = this.getEmotionQuery(n.id);
            console.log(q);
            promises.push(essvr.getData(JSON.parse(q)));
        });

        Promise.all(promises)
            .then((results) => {
                console.log("All done", results);
                let all = [];
                var idx = 0;
                results.forEach(r => {
                    var ndata = dh.getData(JSON.parse(r), query.dataposition);
                    console.log(ndata);
                    var tdata = [];
                    ndata.forEach(t => {
                        console.log(t.prime.buckets[0].key);
                        tdata.push(t.prime.buckets[0].key);
                    })
                    data.nodes[idx]["emotions"] = tdata;
                    data.nodes[idx]["index"] = 0;
                    data.nodes[idx]["color"] = "gray";
                    idx = idx + 1;
                });
                this.setState({ data: data });
                this.display();
            }).catch((e) => {
                console.log(e);
            });

    }
    processChildren = (nodeList, level) => {
        console.log("executeQuery");
        var dh = new DataHelper();
        let promises = [];
        // var client = new es.Client(JSON.parse(dh.getServer()));
        var essvr = new ESService();

        
        nodeList.forEach(c => {
            console.log("::::::::::::::::::::::::::::::::::::::::::::::::::::",c.getChildrenQuery(this.state.range));
            promises.push(essvr.getData(JSON.parse(c.getChildrenQuery(this.state.range))));
        });

        Promise.all(promises)
            .then((results) => {
                console.log("All done", results);
                if (results.length > 0) {
                    let all = [];
                    results.forEach(r => {
                        console.log(r);
                    })
                    console.log("All done0");
                    var idx = 0;
                    nodeList.forEach(c => {
                        c.addChildNodes(JSON.parse(results[idx]));
                        idx = idx + 1;
                    });
                    console.log("All done1");
                    this.display();
                    //this.checkLevel(level);
                    console.log("Level:");
                    console.log(this.state.node.getLevel());
                    this.state.node.getLeafNodes().forEach(n => {
                        console.log(n.id);
                    })
                    if (this.state.node.getLevel() < level) {
                        this.processChildren(this.state.node.getLeafNodes(), level);
                        //var gh = new GraphHelper();
                        //var d = gh.getGraphData(this.state.node);
                        //this.setState({data:d});
                    } else {
                        var gh = new GraphHelper();
                        var d = gh.getGraphData(this.state.node);
                        this.processEmotions(d);
                        this.setState({ showRelations: true })
                        //this.display();
                    }
                } else {
                    this.setState({ contentShow: true,noresults: true })
                }
            })
            .catch((e) => {
                console.log(e);
            });
        console.log("end of execute children");
    }
    updateData = (resp, email, level) => {
        console.log("+++++++++++++++++++++++++++++++++++++++++++++++++++++" + level);
        var model = this.state.dh.getModel();
        var query = model.getQuery("To", "Terms");
        var data = JSON.parse(resp);
        console.log(data);
        var ndata = this.state.dh.getData(data, query.dataposition);
        console.log("level: " + level + " for " + email);
        console.log(ndata);

        var nodes = [];
        var links = [];

        var node = {};
        node["id"] = this.state.email;
        node["group"] = 1
        nodes.push(node);


        var group = level;
        if (this.NotExist(email)) { this.state.data.nodes.push({ id: email, group: level }); }
        ndata.forEach(it => {
            console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + it);
            var node = {};
            node["id"] = it.key;
            node["group"] = group;
            node["title"] = this.state.cnt;
            if (this.NotExist(it.key)) {
                this.state.data.nodes.push(node);
                this.state.cnt = this.state.cnt + 1;
            }

            var link = {};
            link["source"] = email;
            link["target"] = it.key;
            link["value"] = it.doc_count;
            this.state.data.links.push(link);

            //if (level != 0){
            var nemail = spsanitizeJSON(it.key);
            this.getData(nemail, level);
            //}
        })


        if (level == 0) {
            console.log(JSON.stringify(this.state.data));
            this.forceUpdate();
        }
    }
    getQuery = (email) => {
        var dh = new DataHelper();
        var model = dh.getModel();
        var query = model.getQuery("To", "Terms");

        var timeFilter = dh.generateTimeFilter(this.state.range, "filter");
        query.addFilterObj(timeFilter);

        var matchFilter = dh.generateMatchFilter("query", "must", "model.from.keyword", email);
        query.addFilterObj(matchFilter);


        var res = query.getFullQuery();
        console.log(res);
        return res;
    }
    getData = (email, level) => {
        console.log("=================================================", email, this.state.range)
        var nemail = spsanitizeJSON(email);
        this.state.node = new DataNode(nemail, nemail, 0);
        //this.display();
        var nodeList = [];
        nodeList.push(this.state.node);
        console.log("length >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",nodeList.length)
        this.processChildren(nodeList, level);
        //this.display();
        console.log("length >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",nodeList.length)
        
        //var emailList = [];
        //emailList.push(nemail);
        //this.processChildren(nemail, level);
        //this.display();
    }
    display = () => {
        console.log("displaying", this.state.node.getNodeData());
        console.log(JSON.stringify(this.state.data));
    }
    searchSubmit = (email, date) => {
        this.setState({ showPanel: true, showNotification: true,showRelations: false, email: email, date: date },()=>{
            var dt = new DateHelper(this.state.date);
            var range = dt.get24HourRange();
            var interval = "1h";
            this.state.data = { nodes: [], links: [] };
            this.state.cnt = 0;
            this.state.range = range;
            this.state.interval = interval;
            this.getData(this.state.email, this.state.level);
        });
    }
    handleContentClose = (e) => {
        console.log("close")
        this.setState({ contentShow: false })
    }
    render() {
        return (
            <div>
                <Grid fluid>
                    <Row>
                        <Col lg={6}>
                            <SearchView action={this.searchSubmit} email={this.state.email} date={this.state.date} />
                        </Col>
                        <Col lg={6}>
                            {this.state.showNotification && <NotificationForm />}
                        </Col>
                    </Row>
                </Grid>

                {this.state.showPanel && <DPanelView headerCustomClass="contentHeading" bodyCustomClass="contentNoScrollBody" panelCustomClass="contentPanel"
                    header={<span>
                        <i className="fa fa-clock-o fa-fw" /> Email Connections
    <div className="pull-right">
                          
                            <DropdownButton title="Select Level" bsSize="xs" pullRight id="dropdownButton1" onSelect={this.onLevel}>
                                <MenuItem eventKey="1">1</MenuItem>
                                <MenuItem eventKey="2">2</MenuItem>
                                <MenuItem eventKey="3">3</MenuItem>
                                <MenuItem eventKey="4">4</MenuItem>
                                <MenuItem eventKey="5">5</MenuItem>
                            </DropdownButton>
                        </div>

                    </span>}
                    body={<span>

                        {this.state.showRelations && <DRelationsView data={this.state.data} panelWidth={this.props.windowWidth} />}
                    </span>}
                >
                </DPanelView>}
                <ModalView show={this.state.contentShow} closeaction={this.handleContentClose} title="Group Analysis">
                    <div>
                        {this.state.noresults &&
                            <div>No results found for the current selection</div>}
                    </div>
                </ModalView>
            </div>
        );
    }
}
/*
  <DropdownButton title="Date Range" bsSize="xs" pullRight id="dropdownButton1" onSelect={this.onSelect}>
                                <MenuItem eventKey="1">Last 24 Hours</MenuItem>
                                <MenuItem eventKey="2">Last Week</MenuItem>
                                <MenuItem eventKey="3">Last Month</MenuItem>
                                <MenuItem eventKey="4">Last Year</MenuItem>
                            </DropdownButton>
   <SPPanel headerCustomClass="contentHeading" bodyCustomClass="contentBody" panelCustomClass="contentPanel" 
                header={<span>
                    <i className="fa fa-clock-o fa-fw" /> Email Connections
        <div className="pull-right">
                        <DropdownButton title="Date Range" bsSize="xs" pullRight id="dropdownButton1" onSelect={this.onSelect}>
                            <MenuItem eventKey="1">Last 24 Hours</MenuItem>
                            <MenuItem eventKey="2">Last Week</MenuItem>
                            <MenuItem eventKey="3">Last Month</MenuItem>
                            <MenuItem eventKey="4">Last Year</MenuItem>
                        </DropdownButton>
                        <DropdownButton title="Select Level" bsSize="xs" pullRight id="dropdownButton1" onSelect={this.onLevel}>
                            <MenuItem eventKey="1">1</MenuItem>
                            <MenuItem eventKey="2">2</MenuItem>
                            <MenuItem eventKey="3">3</MenuItem>
                            <MenuItem eventKey="4">4</MenuItem>
                            <MenuItem eventKey="5">5</MenuItem>
                        </DropdownButton>
                    </div>
                </span>}
                body={<span>
                    <div>
                        <ul className="timeline">
                        <RelationsView data={this.state.data}/>
                        </ul>
                    </div>
                </span>}
            >

            </SPPanel>

*/