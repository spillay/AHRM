import React, { PropTypes } from 'react';
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

import EmotionTimeLine from './EmotionTimeLine';
import LineChartCtl from './controllers/LineChartCtl';
import EmployeeCard from './EmployeeCard';
import NotificationForm from './ui/NotificationForm';
import EntropyCtl from './controllers/EntropyCtl';

const title = 'Employee View';


export default class EmployeeView extends React.Component {
  constructor(props) {
    super(props)
    console.log(this.props.emailData);
    const pivotDate = this.props.emailData.epochdate;
    console.log(pivotDate);
    var email = this.props.emailData.to;
    if (this.props.emailData.box == "inbox") {
      email = this.props.emailData.to;
    } else {
      email = this.props.emailData.from;
    }
    console.log(email);
    this.state = {
      pivotDate: pivotDate,
      email: email,
      show: true,
      tshow: false,
      timeline: this.props.timeline,
      linechart: this.props.linechart,
      interval: "1h"
    }
  }

  render() {
    return (
      <div>
        <Grid fluid>
          <Row>
            <Col lg={12}>
              <Row>
                <Col lg={12}>
                  {this.state.timeline && <EmotionTimeLine email={this.state.email} pivotDate={this.state.pivotDate} interval={this.state.interval} />}
                </Col>
                <Col lg={12}>
                  {this.state.linechart && <LineChartCtl email={this.state.email} pivotDate={this.state.pivotDate} interval={this.state.interval} />}
                </Col>
              </Row>
            </Col>
            <Col lg={3}>
              <Row>
                {this.state.tshow && <EmployeeCard />}
              </Row>
              <Row>
                {this.state.tshow && <NotificationForm />}
              </Row>
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}
