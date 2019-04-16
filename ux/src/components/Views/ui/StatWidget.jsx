import React, { Component } from 'react';
import { Panel } from 'react-bootstrap';
import { Link } from "react-router-dom";
import PropTypes from 'prop-types';

class StatWidget extends Component { // eslint-disable-line
    static propTypes = {
        style: PropTypes.string,
        count: PropTypes.string,
        headerText: PropTypes.string,
        icon: PropTypes.string,
        footerText: PropTypes.string,
    }
    render() {
        return (
            <Panel  className="stat" className={this.props.style}>
                <Panel.Heading>
                    <div className="row">
                        <div className="col-xs-3">
                            <i
                                className={this.props.icon}
                            />
                        </div>
                        <div className="col-xs-9 text-right">
                            <div className="huge">
                                {
                                    this.props.count
                                }
                            </div>
                            <div>
                                {
                                    this.props.headerText
                                }
                            </div>
                        </div>
                    </div>
                </Panel.Heading>
                <Panel.Footer>
                    <Link
                        to={
                            this.props.linkTo // eslint-disable-line
                        }
                    >
                        <span className="pull-left">
                            {
                                this.props.footerText
                            }
                        </span>
                        <span className="pull-right"><i className="fa fa-arrow-circle-right" /></span>
                        <div className="clearfix" />
                    </Link>
                </Panel.Footer>
            </Panel>
        );
    }
}

export default StatWidget;
