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
import moment from 'moment';
import ModalView from './ModalView';


export default class SearchView extends React.Component {
    constructor(props) {
        super(props);
        console.log("show in constructor" + this.props.show)
        this.state = {
            email: "",
            date: "",
            action: this.props.onSubmit,
            showError: false
        };
    }

    emailChange = (event) => {
        console.log("email changed");
        this.setState({ email: event.target.value });
    }
    dateChange = (event) => {
        console.log("date change", event.target.value);
        var d = new moment(event.target.value);
        // var dt = d.getDate();
        // var mn = d.getMonth();
        // console.log("month",mn)
        // mn++;
        // var yy = d.getFullYear();
        // var ndate = mn + "-" + dt + "-" + yy;
        console.log(d)
        var ndate = d.format("MM-DD-YYYY");
        this.setState({ date: ndate });
    }
    onSubmit = (event) => {
        console.log("submit", event);
        event.preventDefault();
        console.log(this.state.email)
        console.log(this.state.date)
        if ( this.state.email !== "" && this.state.date !== ""){
           
            this.props.action(this.state.email, this.state.date)
        } else {
            this.setState({ showError: true });
        }
    }
    handleContentClose = (e) => {
        this.setState({ showError: false })
    }
    render() {
        return (
            <div>
                <form onSubmit={this.onSubmit}>
                    <datalist id="days">
                        <option label="Email Data">2019-03-12</option>
                        <option label="Email Data">2001-01-01</option>
                        <option label="Email Data">2002-01-01</option>
                        <option label="Email Data">1999-12-27</option>
                    </datalist>
                    <datalist id="people">
                        <option>s..shively@enron.com</option>
                        <option>john.lavorato@enron.com</option>
                        <option>karen.buckley@enron.com</option>
                        <option>airam.arteaga@enron.com</option>
                        <option>alex.villarreal@enron.com</option>
                        <option>chris.gaskill@enron.com</option>
                        <option>deirdre.mccaffrey@enron.com</option>
                        <option>frank.hayden@enron.com</option>
                        <option>lisa.kinsey@enron.com</option>
                        <option>mike.roberts@enron.com</option>
                        <option>from@example.com</option>
                    </datalist>
                    <div className="form-group">
                        <label htmlFor="email">Email address</label>
                        <input className="form-control" id="email" defaultValue={this.state.email} onChange={this.emailChange} list="people" />
                    </div>
                    <div className="form-group">
                        <label htmlFor="datefield">Date to Analyze</label>
                        <input type="date" className="form-control" id="datefield" defaultValue={this.state.date} onChange={this.dateChange} list="days" />
                    </div>
                    <input type="submit" className="btn btn-primary" value="Submit" />
                </form>
                <ModalView show={this.state.showError} closeaction={this.handleContentClose} title="Notifications">
                    <div>
                       Please enter an email and a valid date
                    </div>
                </ModalView>
            </div>
        )
    }
}
/*
 <form onSubmit={this.state.action}>
            <div className="form-group">
                <label htmlFor="email">Email address</label>
                <input type="text" className="form-control" id="email" aria-describedby="emailHelp"  defaultValue={this.state.email}  onChange={this.handleChange} />
                <small id="emailHelp" className="form-text text-muted">Email address used for Analysis.</small>
            </div>
            <div className="form-group">
                <label htmlFor="datefield">Date to Analyze</label>
                <input type="date" className="form-control" id="datefield" defaultValue={this.state.date}  onChange={this.dateChange}/>
            </div>
            <button type="submit" className="btn btn-primary">Submit</button>
        </form>
*/