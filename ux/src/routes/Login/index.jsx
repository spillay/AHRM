

import React from 'react';
// import { Panel, Input, Button } from 'react-bootstrap';
import Button from 'react-bootstrap/lib/Button';
import Panel from 'react-bootstrap/lib/Panel';
import { FormControl, Checkbox } from 'react-bootstrap';
import AuthService from '../../components/security/AuthService';

const title = 'Log In';

class Login extends React.Component {
  constructor() {
    super();
    this.handleChange = this.handleChange.bind(this);
    this.handleFormSubmit = this.handleFormSubmit.bind(this);
    this.Auth = new AuthService();
  }
  handleFormSubmit(e) {
    e.preventDefault();
    this.Auth.login(this.state.username, this.state.password)
      .then(res => {
        this.props.history.go(-1)
      })
      .catch(err => {
        alert(err);
      })
  }

  handleChange(e) {
    this.setState(
      {
        [e.target.name]: e.target.value
      }
    )
  }
  render() {
    return (
      <div className="col-md-4 col-md-offset-4">
        <div className="text-center">
          <h1 className="login-brand-text">Data Analytics Tools</h1>
          <h3 className="text-muted">Created by <a href="http://jaasuz.com">Jaasuz.com</a> team</h3>
        </div>

        <Panel header={<h3>Please Sign In</h3>} className="login-panel">

          <form role="form" onSubmit={this.handleFormSubmit}>
            <fieldset>
              <div className="form-group">
                <FormControl
                  type="text"
                  className="form-control"
                  placeholder="Username"
                  name="username"
                  onChange={this.handleChange}
                />
              </div>

              <div className="form-group">
                <FormControl
                  className="form-control"
                  placeholder="Password"
                  type="password"
                  name="password"
                  onChange={this.handleChange}
                />
              </div>
              <Button type="submit" bsSize="large" bsStyle="success" block>Login</Button>  
            </fieldset>
          </form>
          

        </Panel>

      </div>

    );
  }
}


export default Login;
