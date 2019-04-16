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
  ListGroup, ListGroupItem, ToggleButton, ToggleButtonGroup, ButtonGroup, ButtonToolbar
} from 'react-bootstrap';

import SPPanel from './SPPanel.js'
import RadioGroup from './RadioGroup'
import ModalView from '../ModalView'

export default class NotificationForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      action: 'none',
      contentShow: false
    }
  }

  onClick = (e) => {
    console.log("click1", e);
    this.setState({ contentShow: true })
  }

  radioClick = (e) => {
    console.log("radioclick", e.target.value);
    this.setState({ action: e.target.value })
  }
  handleContentClose = (e) => {
    console.log("close")
    this.setState({ contentShow: false })
  }
  render() {
    return (
      <div>
        <Grid fluid>
          <Row>
            <Col lg={6}>
              <FormGroup>
                <ControlLabel>Action - Level 1</ControlLabel>
                <Col>
                  <Radio name="act1" value="HR" onClick={(e) => { this.radioClick(e) }} >
                    Refer to HR
                      </Radio>
                  <Radio name="act1" value="Legal" onClick={(e) => { this.radioClick(e) }}>
                    Refer to Legal
                      </Radio>
                  <Radio name="act1" value="open" onClick={(e) => { this.radioClick(e) }}>
                    Open Case
                      </Radio>
                </Col>
              </FormGroup>
              <Button bsStyle="primary" type="submit" onClick={this.onClick}>Submit</Button>
            </Col>
          </Row>
        </Grid>
        <ModalView show={this.state.contentShow} closeaction={this.handleContentClose} title="Notifications">
          <div>
            {this.state.action === "none" &&
              <div>Please select a relevant action</div>}
            {this.state.action === "HR" &&
              <div>A notification has been sent to the HR Department</div>}
            {this.state.action === "Legal" &&
              <div>A notification has been sent to the Legal Department</div>}
            {this.state.action === "open" &&
              <div>A case has been opened with the current employee and related info</div>}
          </div>
        </ModalView>
      </div>
    )
  }
}
/*
 <Col lg={6}>
              <FormGroup>
                <ControlLabel>Action - Level 2</ControlLabel>
                <Col>
                  <Radio>
                    Confirm Case
                      </Radio>
                  <Radio>
                    Reopen Case
                      </Radio>
                  <Radio>
                    Refer to Legal
                      </Radio>
                </Col>
              </FormGroup>
              <Button bsStyle="primary" type="submit" onClick={this.onClick1}>Submit</Button>
            </Col>
            */