import React, { Component } from 'react';
import Header from './Header';
import { PageHeader } from 'react-bootstrap';
import Notification from '../Views/Notification';
import TicketPanel from '../Views/TicketPanel';

function withNav(WrappedComponent,props) {
    return class extends Component {
        render() {
            return (
                <div>
                    <Header />
                    <div id="page-wrapper" className="page-wrapper">
                        <div className="row">
                            <div className="col-lg-12">
                                <PageHeader>{props.title}</PageHeader>
                            </div>
                        </div>
                        <TicketPanel />
                        <div className="row">
                            <div className="col-lg-12">
                                <WrappedComponent {...this.props} />
                            </div>
                           
                           
                        </div>
                    </div>
                </div>
            )
        }
    }
}
export default withNav;
/*
<div>
                    <Header />
                    <div id="page-wrapper" className="page-wrapper">
                        <div className="row">
                            <div className="col-lg-12">
                                <PageHeader>{props.title}</PageHeader>
                            </div>
                        </div>
                        <TicketPanel />
                        <div className="row">
                            <div className="col-lg-8">
                                <WrappedComponent {...this.props} />
                            </div>
                            <div className="col-lg-4">
                                <Notification />
                            </div>
                           
                        </div>
                    </div>
                </div>
                */