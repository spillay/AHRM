

import React from 'react';
// import { Panel, Input, Button } from 'react-bootstrap';
import Button from 'react-bootstrap/lib/Button';
import Panel from 'react-bootstrap/lib/Panel';
import { FormControl, Checkbox } from 'react-bootstrap';
//import AuthService from '../../../components/security/AuthService';
import TSNEView from '../../../components/Views/TSNEView';

const title = 'TSNE Experimental View';

class TSNE extends React.Component {
  constructor() {
    super();
    //this.Auth = new AuthService();
  }
  
  render() {
    return (
      <div className="col-md-4 col-md-offset-4">
        <div className="text-center">
          <h1 className="login-brand-text">Data Analytics Tools</h1>
          <h3 className="text-muted">Created by <a href="http://jaasuz.com">Jaasuz.com</a> team</h3>
        </div>
        <TSNEView/>
        

      </div>

    );
  }
}


export default TSNE;
