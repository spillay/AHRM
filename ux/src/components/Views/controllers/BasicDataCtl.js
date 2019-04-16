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
import {DataHelper} from 'DataManager'


export default class BasicDataCtl extends React.Component {
    constructor(props) {
        super(props);
        console.log("show in constructor" + this.props.show)
        
        this.state = {
            data: this.props.data,
            show: this.props.show,
            dh: new DataHelper()
        };
    }
    componentDidMount(){
        console.log("componentdimount");
        var model = this.state.dh.getModel();
        var email = "s..shively@enron.com";
        var emailAdd = "\"" + email + "\"";

        var outboxquery = model.getQuery("From", "Simple");
        outboxquery.addParams({ "VALUE": emailAdd });
        var sentRes = outboxquery.getFullQuery();
        console.log(sentRes);

    }
    render() {
        return (<div>BasicDataCtl</div>);
    }
}