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

import { DRelationsView } from '../RelationsView'
import SPPanel from '../ui/SPPanel.js';
import { DPanelView } from '../PanelView';
import EmoService from '../../security/EmoService';
import ESService from '../../security/ESService';
import SearchView from '../../Views/SearchView';
import NotificationForm from '../../Views/ui/NotificationForm';
import ModalView from '../../Views/ModalView';
import GraphData from '../../BL/GraphData';
import DataHelper  from '../../SP/data/DataHelper';
import DateHelper  from '../../SP/data/DateHelper';

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
            level: "1",
            showRelations: false,
            showNotification: false,
            showPanel: false,
            noresults: false,
            contentShow: false,
            selected: "Select Level"
        };
    }
    onLevel = (target) => {
        console.log("Outputting from level here: ", target);
        this.state.level = target;
        this.state.selected = target;
        var dt = new DateHelper(this.state.date);
        var range = dt.get24HourRange();
        var interval = "1h";
        this.state.data = { nodes: [], links: [] };
        this.state.cnt = 0;
        this.state.range = range;
        this.state.interval = interval;
        this.getData(this.state.email, this.state.level,this.state.date,this.state.interval);
    }
    onSelect = (target) => {
        console.log("Outputting from here: ", target, this.state.date);
        var dt = new DateHelper(this.state.date);
        var range = dt.get24HourRange();
        var interval = "1h";
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
        this.state.interval = interval
        this.getData(this.state.email, this.state.level,this.state.date,this.state.interval);
        
    }

    getData = (email,level,date,interval) =>{
        var gd = new GraphData({"id":email,"name":email,"date":date,"interval":interval,"level":level});
        gd.process().then(res=>{
            console.log("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}",gd.getData())
            this.setState({ data: gd.getData(),showRelations: true });
            this.forceUpdate()
        })
    }
   
  
   
    
    searchSubmit = (email, date) => {
        this.setState({ showPanel: true, showNotification: true,showRelations: false, email: email, date: date },()=>{
            var interval = "1h";
            this.state.data = { nodes: [], links: [] };
            this.state.interval = interval;
            this.getData(this.state.email, this.state.level,this.state.date,this.state.interval);
        });
    }
    handleContentClose = (e) => {
        console.log("close")
        this.setState({ contentShow: false })
    }
    
    render() {
        console.log(this.props)
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
                          
                          
                        </div>

                    </span>}
                    body={<span>

                        {this.state.showRelations && <DRelationsView data={this.state.data} email={this.state.email} date={this.state.date} interval={this.state.interval} panelWidth={this.props.windowWidth} level={this.state.level}/>}
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