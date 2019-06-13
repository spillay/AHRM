import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Link, Switch } from "react-router-dom";

import Home from '../routes/Home/';
import About from '../routes/About';
import Topics from '../routes/Topics';
import Header from '../components/navigation/Header';
import Login from '../routes/Login';
import EmployeeAnalysis from '../routes/Employee/Individual/';
import GroupAnalysis from '../routes/Employee/Group';
import DeptModel from '../routes/Analysis/DeptModel';
import LocationModel from '../routes/Analysis/LocationModel';
import ProductModel from '../routes/Analysis/ProductModel';
import SimpleModel from '../routes/SimpleModel';
import DeceptionModel from '../routes/Deception';
import EmailData from '../routes/Admin/emailData';
import TSNE from '../routes/Stats/TSNE';
import '../styles/css/main.css';

class App extends Component {
  render() {
    return (
      <Router>
        <div>
          <Switch>
            <Route exact path="/" component={Home} />
            <Route path="/Login" component={Login} />
            <Route path="/EmployeeAnalysis" component={EmployeeAnalysis} />
            <Route path="/GroupAnalysis" component={GroupAnalysis} />
            <Route path="/DeptView" component={DeptModel} />
            <Route path="/LocationView" component={LocationModel} />
            <Route path="/ProductView" component={ProductModel} />
            <Route path="/SimpleView" component={SimpleModel} />
            <Route path="/DeceptionView" component={DeceptionModel} />
            <Route path="/about" component={About} />
            <Route path="/topics" component={Topics} />
            <Route path="/admin-email" component={EmailData} />
            <Route path="/tsne" component={TSNE} />
          </Switch>
        </div>
      </Router>
    );
  }
}

export default App;
/*
          <ul>
            <li >
              <Link to="/">Home</Link>
            </li>
            <li>
              <Link to="/about">About</Link>
            </li>
            <li>
              <Link to="/topics">Topics</Link>
            </li>
          </ul>
*/