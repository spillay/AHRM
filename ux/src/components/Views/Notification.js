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

import SPPanel from './ui/SPPanel.js'
import userImage from '../../images/user-male-sm.png'


export default class Notification extends React.Component {
    constructor(props) {
        super(props);
        console.log("In Constructor of Notification")
        this.state = {
            info: ""
        };
    }

    render() {
        return (
            <Panel
                header={<span>
                    <i className="fa fa-bell fa-fw" /> Notifications Panel
      </span>}
            >
                <ListGroup>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-tasks fa-fw" /> New Analysis Posted
          <span className="pull-right text-muted small"><em>4 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-bolt fa-fw" /> Case #5467 Closed
          <span className="pull-right text-muted small"><em>12 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-upload fa-fw" /> Message Sent to HR
          <span className="pull-right text-muted small"><em>27 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-tasks fa-fw" /> New Analysis Posted
          <span className="pull-right text-muted small"><em>43 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-upload fa-fw" /> Message sent to Legal
          <span className="pull-right text-muted small"><em>11:32 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-bolt fa-fw" /> Server Posted Notification with High Entropy
          <span className="pull-right text-muted small"><em>11:13 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-warning fa-fw" /> Server Posted Notification with Group Analysis
          <span className="pull-right text-muted small"><em>10:57 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-money fa-fw" /> New Case Opened #6001
          <span className="pull-right text-muted small"><em>9:49 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-money fa-fw" /> New Case Opened #5800
          <span className="pull-right text-muted small"><em>Yesterday</em></span>
                    </ListGroupItem>
                </ListGroup>
                <Button block>View All Alerts</Button>
            </Panel>
        )
    }
}