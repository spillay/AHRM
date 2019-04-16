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
import EmailView from './EmailView';
import Donut from './ui/Donut';
import DictCtl from './controllers/DictCtl'

export default class EmailAnalysisView extends React.Component {
    constructor(props) {
        super(props);
    }

    getDonutData = () => {
        var dataset = [];
        var norm = this.props.data.norm;
        console.log(norm);
        if (norm != undefined && norm != "") {
            var nObj = JSON.parse(norm);
            Object.keys(nObj).map((key) => {
                console.log(key);
                console.log(nObj[key]);
                dataset.push({ label: key, value: nObj[key] });
            })
        }
        return dataset;
    }
    onAnalyze = (e) => {
        this.forceUpdate();
    }
    render() {
        console.log(this.props.data);
        return (
            <EmailView data={this.props.data}>
                <Grid fluid>
                    <Row>
                        <Col lg={6}>
                            <Donut data={this.getDonutData()} color="#8884d8" innerRadius="80%" outerRadius="100%" legend={false} />
                        </Col>
                    </Row>
                    <Row>
                        <Button  onClick={this.onAnalyze}><i className="fa fa-thumbs-down"></i></Button>
                        <Button className="btn btn-secondary"><i className="fa fa-thumbs-up"></i></Button>
                    </Row>
                </Grid>
            </EmailView>
        )
    }
}
/*
    <Button className="btn btn-primary" onClick={this.onAnalyze}><i className="fas fa-thumbs-down"></i>Analyze</Button>
                        <Button className="btn btn-secondary">Save for Approval</Button>
 */