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
import Donut from './ui/Donut';
import $ from 'jquery';
import EmoService from '../security/EmoService'

export default class DeceptionView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            data: "",
            textReadOnly: false,
            donutShow: false,
            res: ""
        };
    }
    textChange = (event) => {
        console.log("email changed");
        this.setState({ data: event.target.value });
    }
    pretextChange = (event)=>{
        this.setState({ data: event.target.value });
    }
    callWS = () => {
        //var url = "http://localhost:5000/web/get"
        var cdata = this.state.data;
        var that = this;
        var essvr = new EmoService();
        essvr.getDeception({ "text": cdata }).then(data => {
            console.log("Deception",data);      
            var dec = JSON.parse(data);
            var decRes = {};
            if (dec.results==="truth"){
                var sc = dec.score;
                decRes['Truth'] = sc/10;
                decRes['False'] = (10-sc)/10
            } else {
                var sc = dec.score;
                decRes['False'] = sc/10;
                decRes['Truth'] = (10-sc)/10
            }
            console.log(decRes);
            that.setState({ res: decRes, donutShow: true, textReadOnly: true }, () => {
                console.log("that state", that.state.res);
                //that.forceUpdate();
                //console.log(that.getDonutData())
            })
        },function(error) {
            alert(error)
            //this.setState({name: error});
        });
        /*
        console.log(cdata);
        var that = this;
        $.ajax(url, {
            type: "POST",
            data: JSON.stringify({ "text": cdata }),
            dataType : 'json',
            contentType: "application/json; charset=utf-8",
            beforeSend: function () {
                console.log("before send");
            },
            error: function () {
                alert("Error with Emotion WebService");
            },
            success: function (data) {
                console.log(data);
                that.setState({ res: data});
                that.setState({ donutShow: true, textReadOnly: true });
            }
        });
        */
    }
    onSubmit = (event) => {
        console.log("submit", event);
        event.preventDefault();
        console.log(this.state.data)

        //this.forceUpdate();
        this.callWS();
    }
    clearSubmit = (event) => {
        console.log("clear")
        this.setState({ donutShow: false, textReadOnly: false });
        this.forceUpdate();
    }
    getDonutData = () => {
        var dataset = [];
        dataset.push({ label: "Truth", value: this.state.res.Truth });
        dataset.push({ label: "False", value: this.state.res.False });
        console.log(dataset)
        return dataset;
    }
    render() {
        return (
            <Grid fluid>
                <Row>
                    <Col lg={12}>
                        <form onSubmit={this.onSubmit}>
                        <datalist id="mesg">
                                <option label="msg1">I just want to tell you that we're going to talk to you and find out where you live and we're going to spend the months people to your apartment and either have you gang right or gain murdered I don't know it depends on.  Which you really want but ultimately he's gonna be done by some other ones so therefore you can't really.  Say anything against it because you know Muslims have the right to rate in our culture is in their doctrines and the religious texts so you know what a Muslim's rape you okay we're gonna send the Munch Muslims here house once we find out where you live we're gonna post your information publicly on him internet we're gonna find out your mother's name your father's name where they live we're gonna harass them until you know they stop reading or whatever it's just it's such bullshit your bullshit liberal lunatic I mean you're so stupid you you can think for yourself you just you just repeat and Meredith that's been shoved down your throat kindergarten.  And you have nothing to show for it anyone you liberals actually have to show what the Democrat and Democrat policy wasn't actually have to show for it self.  We have destroyed in.  The stalemate years under a barber Hey you know.  Yeah you know you're going to be a liberal these why not you know Marxism is great instead because yeah because communism and socialism work and is definitely not failed everywhere else in the world.  My god my god you are so stupid.  And my anyway we already know you live in New York City I can pretty much find out where you live by a your first and last name going in asking around and trying to find out you know your mother's maiden name and that we're gonna post all that information on the internet publicly on every social media website we can and we are going to tell people to go harass you are going to tell people that nail you whatever the fuck they want me we're gonna tell people the milieu Paris whatever the fuck they want so you know you brought this upon yourself.  You didn't want an outrage if you want a backlash and why did you doc someone for having an opinion.  This is.  You're fuck retarded.  Well we're gonna find out where you live in so ho in.  Just exactly away from whatever.  Whatever.  The wrong side I don't know we can't tell if you're closest to the Bronx queens.  In the opposite direction.  Sure but we and then we can close anyway and we might kill you because we do that kind of thing earn.  But we might not because we want to see your face when you cry when you're.  Can now safely little bench on Twitter by Muslim.</option>
                            <option label="msg2">Car troubles are inconvenient at best, and are often accompanied by costs like call-out fees, towing charges and more.As vehicles get older, and warranties fall away, vehicles also become less reliable and more prone to expensive breakdowns.Luckily, Bidvest insurance offers a good range of Warranty products to help protect your most valuable asset, together with our unique benefits, which ensures that even when a Breakdown catches you off-guard, we got you covered.</option>
                            <option label="msg3">This is your last week to claim the Early Bird Special for Ultimate Partnering 2019! This year's will be the biggest event yet, so you don’t want to miss it!Remember, it’s not what you know, it’s who you know.We’re expecting over 1,000 attendees at this event - their ventures will range from simple startups to large businesses. There will be real estate investors, business owners, business leaders, private money partners, financers, developers... the list will go on and on…AND they will have one thing in common:They will have the focus, commitment and vision to understand the power of three days immersed in the leading edge of Networking, Partnering and Entrepreneurial Success. They wouldn't miss this life-changing event for anything. They'll travel from every corner of the United States, Canada, and even the world.Sign up for the event before the sale is over so you can partner with investors just like you at this HUGE 3-day networking event!</option>
                            <option label="msg4">In the event that you allow domain service for dsleng.info to expire on 05.10.2019, the listing will be automatically deleted from our servers within 3 business days. After expiration, we reserve the right to offer your listing to competing businesses or interested parties in the same category and state/region after 3 business days on an auction basis to keep your website service active.</option> 
                            <option label="msg5">A year after Airbnb launched, the company decided to migrate nearly all of its cloud computing functions to Amazon Web Services (AWS) because of service administration challenges experienced with its original provider.Nathan Blecharczyk, Co-founder & CTO of Airbnb says, Initially, the appeal of AWS was the ease of managing and customizing the stack. It was great to be able to ramp up more servers without having to contact anyone and without having minimum usage commitments.</option>
                        </datalist>
                        <br/>
                            <div className="form-group">
                                <label htmlFor="helper">Predefined Text Messages(Purely for Research Purposes)</label>
                                <input id="helper" list="mesg" onChange={this.pretextChange}/>
                               </div>
                            <div className="form-group">
                                <label htmlFor="text">Text Message</label>
                                <textarea className="form-control inputstl" rows="10" value={this.state.data}  onChange={this.textChange} readOnly={this.state.textReadOnly}></textarea>
                            </div>
                            <input type="submit" className="btn btn-primary" value="Submit" />
                            <Button className="btn btn-primary" onClick={this.clearSubmit}>Clear</Button>
                        </form>
                    </Col>
                </Row>
                <Row>
                    <Col lg={12}>
                        {this.state.donutShow && <Donut data={this.getDonutData()} color="#8884d8" innerRadius="80%" outerRadius="100%" legend={false} />}
                    </Col>
                </Row>

            </Grid>
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