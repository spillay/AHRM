import React, { Component } from 'react';
import { withRouter } from "react-router-dom";
import classNames from 'classnames';


class SideBar extends Component {

  constructor(props) {
    super(props);
    this.state = {
      uiElementsCollapsed: true,
      chartsElementsCollapsed: true,
      employeeElementsCollapsed: true,
      analyticsElementCollapsed: true,
      analyticsSimpleCollapsed: true,
      adminCollapsed: true,
      multiLevelDropdownCollapsed: true,
      thirdLevelDropdownCollapsed: true,
      samplePagesCollapsed: true,
    };
  }

  render() {
    return (
      <div className="navbar-default sidebar" style={{ marginLeft: '-20px' }} role="navigation">
        <div className="sidebar-nav navbar-collapse collapse">
          <ul className="nav in" id="side-menu">

           

            <li>
              <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/'); }} >
                <i className="fa fa-dashboard fa-fw" /> &nbsp;Dashboard
              </a>
            </li>


            <li className={classNames({ active: !this.state.employeeElementsCollapsed })}>
              <a
                href=""
                onClick={(e) => {
                  e.preventDefault();
                  this.setState({ employeeElementsCollapsed: !this.state.employeeElementsCollapsed });
                  return false;
                }}
              >
                <i className="fa fa-bar-chart-o fa-fw" /> &nbsp;Employees
                <span className="fa arrow" />
              </a>
              <ul
                className={
                  classNames({
                    'nav nav-second-level': true,
                    collapse: this.state.employeeElementsCollapsed,
                  })
                }
              >

                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/EmployeeAnalysis'); }} >
                    Employee Analysis
                  </a>
                </li>
                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/GroupAnalysis'); }} >
                    Group Analysis
                  </a>
                </li>
              </ul>
            </li>

            <li className={classNames({ active: !this.state.analyticsElementCollapsed })}>
              <a
                href=""
                onClick={(e) => {
                  e.preventDefault();
                  this.setState({ analyticsElementCollapsed: !this.state.analyticsElementCollapsed });
                  return false;
                }}
              >
                <i className="fa fa-bar-chart-o fa-fw" /> &nbsp;Organization Analytics
                <span className="fa arrow" />
              </a>
              <ul
                className={
                  classNames({
                    'nav nav-second-level': true,
                    collapse: this.state.analyticsElementCollapsed,
                  })
                }
              >
                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/DeptView'); }} >
                    Department Analysis
                  </a>
                </li>
                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/ProductView'); }} >
                    Product Analysis
                  </a>
                </li>
                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/LocationView'); }} >
                    Location Analysis
                  </a>
                </li>
              </ul>
            </li>



            <li className={classNames({ active: !this.state.analyticsSimpleCollapsed })}>
              <a
                href=""
                onClick={(e) => {
                  e.preventDefault();
                  this.setState({ analyticsSimpleCollapsed: !this.state.analyticsSimpleCollapsed });
                  return false;
                }}
              >
                <i className="fa fa-bar-chart-o fa-fw" /> &nbsp;Text Detection
                <span className="fa arrow" />
              </a>
              <ul
                className={
                  classNames({
                    'nav nav-second-level': true,
                    collapse: this.state.analyticsSimpleCollapsed,
                  })
                }
              >
                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/SimpleView'); }} >
                    Detect Emotion
                  </a>
                </li>
                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/DeceptionView'); }} >
                    Detect Deception
                  </a>
                </li>
              </ul>
            </li>


            <li className={classNames({ active: !this.state.adminCollapsed })}>
              <a
                href=""
                onClick={(e) => {
                  e.preventDefault();
                  this.setState({ adminCollapsed: !this.state.adminCollapsed });
                  return false;
                }}
              >
                <i className="fa fa-bar-chart-o fa-fw" /> &nbsp;Admin
                <span className="fa arrow" />
              </a>
              <ul
                className={
                  classNames({
                    'nav nav-second-level': true,
                    collapse: this.state.adminCollapsed,
                  })
                }
              >
                <li>
                  <a href="" onClick={(e) => { e.preventDefault(); this.props.history.push('/admin-email'); }} >
                    Architecture
                  </a>
                </li>
              </ul>
            </li>



          </ul>
        </div>
      </div>
    );
  }
}

export default withRouter(SideBar);

/*
 <li className="sidebar-search">
              <div className="input-group custom-search-form">
                <input type="text" className="form-control" placeholder="Search..." />
                <span className="input-group-btn">
                  <button className="btn btn-default" type="button">
                    <i className="fa fa-search" />
                  </button>
                </span>
              </div>
            </li>
*/