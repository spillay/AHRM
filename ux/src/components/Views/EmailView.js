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
    ListGroup, ListGroupItem,
    Modal
} from 'react-bootstrap';



export default class EmailView extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        console.log("state render" + this.props.data.id)
        return (
            <Form>
                <FormGroup controlId="from">
                    <ControlLabel>From:</ControlLabel>
                    <FormControl
                        type="text"
                        value={this.props.data.from}
                        readOnly
                    />
                    <HelpBlock>This is the address from which the message was sent.</HelpBlock>
                </FormGroup>
                <FormGroup controlId="to">
                    <ControlLabel>To:</ControlLabel>
                    <FormControl
                        type="text"
                        value={this.props.data.to}
                        readOnly
                    />
                    <HelpBlock>This is the address to which the message was sent.</HelpBlock>
                </FormGroup>
                <FormGroup controlId="date">
                    <ControlLabel>Date:</ControlLabel>
                    <FormControl
                        type="text"
                        value={this.props.data.date}
                        readOnly
                    />
                    <HelpBlock>This is the date the message was sent.</HelpBlock>
                </FormGroup>
                <FormGroup controlId="msg">
                    <ControlLabel>Message:</ControlLabel>
                    <div className="col-sm-5 col-lg-12">
                        <textarea className="form-control inputstl" rows="5" value={this.props.data.longContent} readOnly></textarea>
                    </div>
                    <HelpBlock>This is the content of the message.</HelpBlock>
                </FormGroup>
                {this.props.children}
            </Form>
        )
    }
}
/*
     <div className="form-group">
                            <label htmlFor="Email1msg" className="col-sm-2 control-label">Message:</label>
                            <div className="col-sm-5 col-lg-12">
                                <textarea className="form-control inputstl" rows="5" value={this.props.data.longContent}></textarea>
                            </div>
                        </div>
 */