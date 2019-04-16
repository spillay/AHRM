import React, { Component } from 'react';
import StatWidget from './ui/StatWidget'

class TicketView extends Component {
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
        );
    }
}

export default TicketView;