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
import SPPanel from '../../../components/Views/ui/SPPanel.js';
import EmotionTimeLine from '../../../components/Views/EmotionTimeLine.js';
import NotificationForm from '../../../components/Views/ui/NotificationForm';


import EmployeeCard from '../../../components/Views/EmployeeCard.js';

import SearchView from '../../../components/Views/SearchView.js';
import LineChartCtl from '../../../components/Views/controllers/LineChartCtl';
import EntropyCtl from '../../../components/Views/controllers/EntropyCtl';
import SteadyCtl from '../../../components/Views/controllers/SteadyCtl';
import { DateHelper } from 'DataManager';
import AuthService from '../../../components/security/AuthService';
import withAuth from '../../../components/security/withAuth';
import withTour from '../../../components/tour/withTour';
import withNav from '../../../components/navigation/withNav';

const title = 'Employee Analysis';
// Steps to provide a guided tour of the component
const steps = [
  {
    target: '#org-area',
    content: 'This if my awesome feature!',
    placement: 'bottom',
  },
  {
    target: '#org-bar',
    content: 'This if my awesome feature!',
    placement: 'bottom',
  }
]

function FieldGroup({ id, label, help, ...props }) {
  return (
    <FormGroup controlId={id}>
      <ControlLabel>{label}</ControlLabel>
      <FormControl {...props} />
      {help && <HelpBlock>{help}</HelpBlock>}
    </FormGroup>
  );
}
function FieldGrid({ id, label, help, ...props }) {
  return (
    <FormGroup controlId={id}>
      <Grid>
        <Row>
          <Col>
            <ControlLabel>{label}</ControlLabel>
          </Col>
          <Col>
            <FormControl {...props} />
          </Col>
          <Col>
            {help && <HelpBlock>{help}</HelpBlock>}
          </Col>
        </Row>
      </Grid>
    </FormGroup>
  );
}

class EmployeeAnalysis extends React.Component {
  constructor(props) {
    super(props)
    var dh = new DateHelper("01/04/2001")
    var dte = new Date("01-04-2001");
    this.state = {
      pivotDate: dte.getTime(),
      date: "2001-01-01",
      email: "s..shively@enron.com",
      interval: "1h",
      show: false,
      show2: false,
      tshow: false,
      searchShow: true
    }
  }

  searchSubmit = (email, date) => {
    this.setState({ show: true, email: email, date: date });
    console.log(this.state.email);
    this.forceUpdate();
  }
  
  componentDidUpdate() {
    console.log("update")
    console.log(this.state.email)
  }
  render() {
    return (
      <div>
        <Grid fluid>
          <Row>
            <Col lg={8}>
              <Row>
                <Col lg={6}>
                  <SPPanel headerCustomClass="contentHeading" bodyCustomClass="smcontentBody" panelCustomClass="contentPanel"
                    header={<span><i className="fa fa-bar-chart-o fa-fw" /> Employee Analysis</span>}
                    body={<span><SearchView action={this.searchSubmit} email={this.state.email} date={this.state.date} /></span>}
                  >
                  </SPPanel>
                </Col>
                <Col lg={6}>
                  {this.state.show && <EntropyCtl  email={this.state.email} pivotDate={this.state.date} interval={this.state.interval} load={true} />}
                </Col>
              </Row>
              <Row>
                <Col lg={6}>
                  {this.state.show && <EmployeeCard email={this.state.email} />}
                </Col>
                <Col lg={6}>
                  {this.state.show && <NotificationForm />}
                </Col>
                
              </Row>
              <Row>
                <Col lg={12}>
                  {this.state.show && <SteadyCtl email={this.state.email} pivotDate={this.state.date} interval={this.state.interval} load={true} />}
                </Col>
              </Row>
              <Row>
                <Col lg={12}>
                  {this.state.show && <LineChartCtl email={this.state.email} pivotDate={this.state.date} interval={this.state.interval} load={true} />}
                </Col>
              </Row>
            
            </Col>
            <Col lg={4}>
              {this.state.show && <EmotionTimeLine email={this.state.email} pivotDate={this.state.date} interval={this.state.interval} load={true} />}
            </Col>

          </Row>
        </Grid>
      </div>
    );
  }
}
//export default withAuth(EmployeeAnalysis)
//export default EmployeeAnalysis
export default withAuth(withTour(withNav(EmployeeAnalysis, { title: 'Employee Analysis' }), steps));
