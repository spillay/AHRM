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

const EmployeeData = {
  name: "John Smith",
  email: "john@enron.com",
  address: "141 Washington Street,Hoboken,NJ",
  tel: "+1 201 676 3456"
}

export default class EmployeeCard extends React.Component {
  constructor(props) {
    super(props);
    console.log("In Constructor of EmployeeCard")
    this.state = {
      info: this.getInfo()
    };
  }
  getInfo = () => {

    var EmployeeData = {
      email: this.props.email,
      address: "141 Washington Street,Hoboken,NJ",
      tel: "+1 201 676 3456"
    }
    return EmployeeData;
  }
  render() {
    return (
      <SPPanel panelCustomClass="infoPanel" bodyCustomClass="infoBody"
        header={<span>
          <i className="fa fa-info-circle" /> Employee Details
            </span>}
        body={<span>
         <div className="spContainer-fluid">
              <Row>
                <Col lg={8}>
                  <Row><Col><i className="fa fa-envelope" aria-hidden="true" />&nbsp;{this.props.email}</Col></Row>
                  <Row><Col><i className="fa fa-address-book-o" aria-hidden="true" />&nbsp;{this.state.info.address}</Col></Row>
                  <Row><Col><i className="fa fa-phone" aria-hidden="true"></i>&nbsp;{this.state.info.tel}</Col></Row>
                </Col>
                <Col lg={1}><br /><img src={userImage} /></Col>
              </Row>
            </div>
        </span>}
      >
      </SPPanel>
    )
  }
}
