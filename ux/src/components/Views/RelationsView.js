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
import { SPGraph } from './d3/SPGraph.js';
import { SPAnimationGraph } from './d3/SPAnimationGraph.js';
import { withWindowDimensionsDOM } from './withWindowDimensions';
import ModalView from './ModalView'
var d3 = require("d3");



export default class RelationsView extends React.Component {
    constructor(props) {
        super(props);
        console.log("RelationsView Constr");
        this.state = {
            width: 1000,
            height: 1000,
            margin: 5,
            contentShow: false
        };
        console.log("Parent info--------------------------------", this.props);
    }
    handleContentClose = (e) => {
        this.setState({ contentShow: false })
    }
    componentDidMount() {
        console.log("In RelationsView didmount");
        var rangeObj = document.querySelector('input[type=range]');
        rangeObj.style.setProperty('--range-width', this.props.windowWidth)

    }
    componentDidUpdate() {
        console.log("In RelationsView did update");
        var d3Container = document.getElementById("ComponentPlace");
        d3.select(d3Container).selectAll("*").remove();
        this.state.graph = new SPAnimationGraph({
            'element': d3Container,
            'parent': this,
            'data': this.props.data,
            'width': this.props.windowWidth,
            'height': this.props.windowHeight,
            'margin': this.state.margin
        });
        var rangeObj = document.querySelector('input[type=range]');
        rangeObj.style.setProperty('--range-width', this.props.windowWidth)

    }
    componentWillUnmount(){
        console.log("RelationsView Unmount")
        this.state.graph.clean();
    }
    changeRange = (e) => {
        console.log(e, e.target.value);
        if (this.state.graph === undefined) {
            this.setState({ contentShow: true })
        } else {
            var output = document.querySelector('#output');
            console.log(output)
            output.setAttribute("value", "at " + e.target.value + "th Hour")
            this.state.graph.setPos(e.target.value);

        }
    }
    render() {
        console.log("RelationView render", this.props.windowWidth, " window width ", this.props.panelWidth, window.innerWidth, window.parent.innerWidth);
        //console.log(this.props.data);
        return (
            <div>

                <datalist id="range1">
                    <option value="0" label="0" />
                    <option value="1" label="1hr" />
                    <option value="2" label="2hr" />
                    <option value="3" label="3hr" />
                    <option value="4" label="4hr" />
                    <option value="5" label="5hr" />
                    <option value="6" label="6hr" />
                    <option value="7" label="7hr" />
                    <option value="8" label="8hr" />
                    <option value="9" label="9hr" />
                    <option value="10" label="10hr" />
                    <option value="11" label="11hr" />
                    <option value="12" label="12hr" />
                    <option value="13" label="13hr" />
                    <option value="14" label="14hr" />
                    <option value="15" label="15hr" />
                    <option value="16" label="16hr" />
                    <option value="17" label="17hr" />
                    <option value="18" label="18hr" />
                    <option value="19" label="19hr" />
                    <option value="20" label="20hr" />
                    <option value="21" label="21hr" />
                    <option value="22" label="22hr" />
                    <option value="23" label="23hr" />
                    <option value="24" label="24hr" />
                </datalist>

                <button id='slow' title='Run Layout in Slow Motion'>
                    <i className='fa fa-play'></i>
                </button>
                <button id='stop' title='Stop'>
                    <i className='fa fa-stop'></i>
                </button>
                <button id='reset' title='Reset Layout to Beginning'>
                    <i className='fa fa-undo'></i>
                </button>
                <div id="legendLinear" />
                <br />

                <input id="range-input-datalist" type="range" min="0" max="24" list="range1" onChange={this.changeRange} />
                <input id="output" type="text" name="output" readOnly />

                <div id="ComponentPlace" width={this.props.windowWidth} height={this.props.windowHeight}>
                </div>
                <ModalView show={this.state.contentShow} closeaction={this.handleContentClose} title="Notifications">
                    <div>
                        {this.state.action === "none" &&
                            <div>Please select a date range</div>}
                    </div>
                </ModalView>
            </div>
        )
    }
}
export const DRelationsView = withWindowDimensionsDOM(RelationsView);
                                    /*
    <button id='advance' title='Advance Layout One Increment'>
                    <i className='fa fa-step-forward'></i>
                </button>
                <button id='slow' title='Run Layout in Slow Motion'>
                    <i className='fa fa-play'></i>
                </button>
                <button id='play' title='Run Layout at Full Speed'>
                    <i className='fa fa-fast-forward'></i>
                </button>
                <button id='reset' title='Reset Layout to Beginning'>
                    <i className='fa fa-undo'></i>
                </button>
                */