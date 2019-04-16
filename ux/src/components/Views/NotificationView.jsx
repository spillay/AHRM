import React, { Component } from 'react';
import {
    MenuItem,
    DropdownButton,
    Panel, PageHeader, ListGroup, ListGroupItem, Button,
  } from 'react-bootstrap';

class NotificationView extends Component {
    render() {
        return (
            <Panel
                header={<span>
                    <i className="fa fa-bell fa-fw" /> Notifications Panel
</span>}
            >
                <ListGroup>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-comment fa-fw" /> New Comment
  <span className="pull-right text-muted small"><em>4 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-twitter fa-fw" /> 3 New Followers
  <span className="pull-right text-muted small"><em>12 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-envelope fa-fw" /> Message Sent
  <span className="pull-right text-muted small"><em>27 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-tasks fa-fw" /> New Task
  <span className="pull-right text-muted small"><em>43 minutes ago</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-upload fa-fw" /> Server Rebooted
  <span className="pull-right text-muted small"><em>11:32 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-bolt fa-fw" /> Server Crashed!
  <span className="pull-right text-muted small"><em>11:13 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-warning fa-fw" /> Server Not Responding
  <span className="pull-right text-muted small"><em>10:57 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-shopping-cart fa-fw" /> New Order Placed
  <span className="pull-right text-muted small"><em>9:49 AM</em></span>
                    </ListGroupItem>
                    <ListGroupItem href="" onClick={(e) => { e.preventDefault(); }}>
                        <i className="fa fa-money fa-fw" /> Payment Received
  <span className="pull-right text-muted small"><em>Yesterday</em></span>
                    </ListGroupItem>
                </ListGroup>
                <Button block>View All Alerts</Button>
            </Panel>
        );
    }
}

export default NotificationView;