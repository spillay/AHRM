import React from 'react';
import ReactDOM from 'react-dom';
import * as d3 from "d3";
//import { D3Pie } from './SPPieChart';
//import { SPBar } from './SPBar.js'

//import { ESDS } from '../js/sp/data/ds/ESDS.js'
import { View } from './View.js'
import { VisMgr } from './VisMgr'

export default class ComponentView extends React.Component {
    constructor(props) {
        super(props)
        console.log("ComponentView")
        this.state = {
            width: 0,
            height: props.height,
            margin: 0,
            vismgr: props.vismgr,
            parent: null
        };
    }

    initialSize = (parent) => {
        console.log("initialSize")
        this.parent = parent;
        this.updateDimensions();
    }

    updateDimensions = () => {
        console.log("update dimensions");
        console.log("innerWidth " + window.innerWidth);
        console.log("innerHeight " + window.innerHeight);
        /*
        if (window.innerWidth > 500 && window.innerWidth < 1000) {
            let update_width = window.innerWidth;
            let update_height = window.innerHeight;
            this.setState({ width: update_width, height: update_height });
        }
        if (window.innerWidth < 500) {
            this.setState({ width: 300, height: 300 });
        }
        if (window.innerWidth > 1000) {
            this.setState({ width: 500, height: 500 });
        }
        this.setState({ width: window.innerWidth, height: window.innerHeight });
        */
        /*
        else {
            let update_width = window.innerWidth;
            //let update_width = window.innerWidth - 100;
            //let update_height = Math.round(update_width / 4.4);
            let update_height = window.innerHeight;
            this.setState({ width: update_width, height: update_height });
        }
        */
        //this.setState({ width: 500, height: 200 });
        console.log("------------------------------------------" + this.parent)
        if ( this.parent !== null){
            console.log("width " + this.parent.offsetWidth + " height " + this.parent.offsetHeight);
            this.setState({ width: this.parent.offsetWidth, height: this.state.height });
            console.log("width " + this.state.width + " height " + this.state.height);
            this.draw(this.state.vismgr);
        }
    }
    redraw = (info) => {
        console.log("redraw ------>" + info);
        var vismgr = this.state.vismgr;
        if (vismgr !== null) {
            console.log("drawing");
            this.setState({ vismgr: vismgr });
            var reactContainer = document.getElementById("JSXComponent");
            var d3Container = document.getElementById("ComponentPlace");
            var rem = ReactDOM.unmountComponentAtNode(reactContainer);
            console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>remove: " + rem);
            vismgr.setContainer(this);
            vismgr.drawNext(d3Container, reactContainer, info);
        }
    }
    draw = (vismgr) => {
        console.log("in draw " + vismgr);
        if (vismgr !== null) {
            console.log("drawing");
            this.setState({ vismgr: vismgr });
            var reactContainer = document.getElementById("JSXComponent");
            var d3Container = document.getElementById("ComponentPlace");
            var rem = ReactDOM.unmountComponentAtNode(reactContainer);
            console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>remove: " + rem);
            vismgr.setContainer(this);
            vismgr.draw(d3Container, reactContainer);
        }
    }
    componentDidMount() {
        console.log("ComponentView Mount");
        //this.updateDimensions();
        //window.addEventListener("resize", this.updateDimensions);
    }

    componentWillUnmount() {
       // window.removeEventListener("resize", this.updateDimensions);
    }


    componentDidUpdate() {
        console.log("--------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>component update");
    }

    render() {
        var style2 = {
            backgroundColor: 'black',
            borderRadius: 4,
            borderWidth: 0.5,
            borderColor: '#d6d7da',
        };
        return (
            <div>
                <div id="JSXComponent">
                </div>
                <div id="ComponentPlace"  width={this.state.width} height={this.state.height}>

                </div>
            </div>
        );
    }
}