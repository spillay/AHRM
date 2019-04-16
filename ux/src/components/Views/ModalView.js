import React from 'react';
import PropTypes from 'prop-types';

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


export default class ModalView extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <Modal show={this.props.show} data-keyboard="false" data-backdrop="static" bsSize="large" width="80%" height="80%"
                aria-labelledby="contained-modal-title-lg">
                <Modal.Header>
                    <Modal.Title>{this.props.title}</Modal.Title>
                    <button type="button" className="close" aria-label="Close" onClick={this.props.closeaction}>
                        <span aria-hidden="true">&times;</span>
                    </button>
                </Modal.Header>
                <Modal.Body>
                    {this.props.children}
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.props.closeaction}>Close</Button>
                </Modal.Footer>
            </Modal>
        )
    }
}
ModalView.propTypes = {
    show: PropTypes.bool,
    closeaction: PropTypes.func.isRequired,
    children: PropTypes.element.isRequired,
    title: PropTypes.string
};