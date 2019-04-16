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
import { emotionPalette } from '../../config/EmotionConfig.js';


export default class EmotionCard extends React.Component {
    constructor(props) {
        super(props);
    }

    getPos = () => {
        if (this.props.box === "sent") { return "timeline-panel"; }
        else { return "timeline-right"; }
    }
    getEmail = () =>{
        if (this.props.box === "sent") { return "fa fa-envelope-open"; }
        else { return "fa fa-envelope"; }
    }
    getColor = () => {
        var ec = "gray";
        var emo = this.props.prime;
        var emos = emotionPalette.filter(e=>e.emotion==emo);
        if(emos.length>0){
            ec =  emos[0].color;
        }
        var style = {
            //color: ec,
            backgroundColor: ec
        };
        return style;
    }
    render() {
        
        return (
            <li>
                <div className="timeline-badge" style={this.getColor()}><i className={this.getEmail()}/>
                </div>
                <div className={this.getPos()} id={this.props.id} onClick={this.props.onCardSelect}>
                    <div className="timeline-heading">
                        <h4 className="timeline-title">{this.props.prime}</h4>
                        <p>
                            <small className="text-muted">
                                <i className="fa fa-clock-o" /> {this.props.date}
                            </small>
                        </p>
                    </div>
                    <div className="timeline-body">
                        <p>{this.props.content}</p>
                    </div>
                </div>
            </li>
        )
    }
}
/*
 <div className="timeline-panel">
                        <div className="timeline-heading">
                            <h4 className="timeline-title">{this.props.prime}</h4>
                            <p>
                                <small className="text-muted">
                                    <i className="fa fa-clock-o" /> {this.props.date}
                                </small>
                            </p>
                        </div>
                        <div className="timeline-body">
                            <p>{this.props.content}</p>
                        </div>
                    </div>
<li>
                <div className="timeline-badge warning"><i className="fa fa-check" />
                </div>
                <div className={this.getPos()}>
                    <div className="timeline-heading">
                        <h4 className="timeline-title">{this.props.prime}</h4>
                        <p>
                            <small className="text-muted">
                                <i className="fa fa-clock-o" /> {this.props.date}
                            </small>
                        </p>
                    </div>
                    <div className="timeline-body">
                        <p>{this.props.content}</p>
                    </div>
                </div>
            </li>
*/