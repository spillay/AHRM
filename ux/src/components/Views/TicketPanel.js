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
    ListGroup, ListGroupItem
} from 'react-bootstrap';

import SPPanel from './ui/SPPanel.js';
import StatWidget from './ui/StatWidget';


export default class TicketPanel extends React.Component {
    constructor(props) {
        super(props);
        console.log("In Constructor of Ticket Panel")
        this.state = {
            info: ""
        };
    }

    render() {
        return (
            <div className="row">
                <div className="col-lg-3 col-md-6">
                    <StatWidget
                        style="panel-primary"
                        icon="fa fa-comments fa-5x"
                        count="26"
                        headerText="New Investigations"
                        footerText="View Details"
                        linkTo="/"
                    />
                </div>
                <div className="col-lg-3 col-md-6">
                    <StatWidget
                        style="panel-green"
                        icon="fa fa-tasks fa-5x"
                        count="12"
                        headerText="In Progress"
                        footerText="View Details"
                        linkTo="/"
                    />
                </div>
                <div className="col-lg-3 col-md-6">
                    <StatWidget
                        style="panel-yellow"
                        icon="fa fa-shopping-cart fa-5x"
                        count="124"
                        headerText="Tasks Assigned to Me"
                        footerText="View Details"
                        linkTo="/"
                    />
                </div>
                <div className="col-lg-3 col-md-6">
                    <StatWidget
                        style="panel-red"
                        icon="fa fa-support fa-5x"
                        count="13"
                        headerText="Completed"
                        footerText="View Details"
                        linkTo="/"
                    />
                </div>
            </div>
        )
    }
}