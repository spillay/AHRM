import React, { Component } from 'react';
import StatWidget from './ui/StatWidget'
import TSNE from '../BL/TSNE';
import { SPTGraph } from '../Views/d3/SPTGraph';
import Button from 'react-bootstrap/lib/Button';
var d3 = require("d3");

class TSNEView extends Component {
    constructor(props) {
        super(props);
        console.log("TSNEView in constructor" + this.props.show)
        this.state = {
            'costThreshold': 10.5
        }
        // var dists = [[1.0, 0.1, 0.2], [0.1, 1.0, 0.3], [0.2, 0.1, 1.0]];
        // var t = new  TSNE()
        // t.init(dists)

        // for(var k = 0; k < 500; k++) {
        //     t.step(); // every time you call this, solution gets better
        // }
        // console.log(t.getSolution());
        // this.state = {
        //     data: t.getDisplayData()
        // };

    }
    graph = (data, dists) => {
        // have both data and dists
        if (dists !== "undefined") {
            var d3Container = document.getElementById("ComponentPlace");
            d3.select(d3Container).selectAll("*").remove();
            console.log(d3Container.getBoundingClientRect())
            this.state.graph = new SPTGraph({
                'element': d3Container,
                'parent': this,
                'data': data,
                'dists': dists,
                'costThreshold': this.state.costThreshold,
                'width': 960,
                'height': 500,
                'margin': 40
            })
            console.log(d3Container.getBoundingClientRect())
        }
    }
    getDistData = () => {
        const scalepop = d3.scaleSqrt().domain([0, 100000]).range([0.2, 34]);
        const scalecountry = d3.scaleOrdinal(d3.schemeCategory10);
        var that = this;
        d3.csv('/data/cities.csv').then(function (cdata) {
            //console.log(cdata)
            const data = cdata
                .sort((a, b) => d3.descending(+a[2015], +b[2015]))
                .map((d, i) => {
                    return {
                        lon: +d.Longitude,
                        lat: +d.Latitude,
                        name: d['Urban Agglomeration'],
                        r: scalepop(+d[2015]),
                        color: scalecountry(+d['Country Code'])
                    };
                })
                .slice(0, 800);
            //.slice(0, 800);
            //console.log(ndata)
            const dists = data.map(d => {
                return data.map(e => {
                    //console.log(d,e,d3.geoDistance([d.lon,d.lat], [e.lon,e.lat]))
                    return d3.geoDistance([d.lon, d.lat], [e.lon, e.lat])
                })
            });
            console.log(data);
            that.graph(data, dists)
        })

    }
    handleChange = (e) => {
        console.log(e, e.target.value);
        this.state.costThreshold = e.target.value;
    }
    handleSubmit = (e) => {
        console.log("button clicked", e, e.target.id)
        var d3Container = document.getElementById("ComponentPlace");
        d3.select(d3Container).selectAll("*").remove();
        this.getDistData()
        
        document.getElementById("stop").disabled = false;
        //document.getElementById("pause").disabled = false;
        // var dists = [[1.0, 0.1, 0.2], [0.1, 1.0, 0.3], [0.2, 0.1, 1.0]];
        // var d3Container = document.getElementById("ComponentPlace");
        // d3.select(d3Container).selectAll("*").remove();
        // this.state.graph = new SPTGraph({
        //     'element': d3Container,
        //     'parent': this,
        //     'data': dists,
        //     'distance': true,
        //     'width': 960,
        //     'height': 500,
        //     'margin': 40
        // })
    }
    render() {
        return (
            <div>
              {/* <div dangerouslySetInnerHTML={{ __html: this.state.data }} /> */}
                {/* <Button type="submit" id="dist" onClick={this.onClick} bsSize="large" bsStyle="success" block>Start</Button> */}
                    <label>
                        Cost Threshold:<input type="text"  onChange={this.handleChange} defaultValue={10.5}/>
                    </label>
                    <button id="dist" onClick={this.handleSubmit}><i className='fa fa-play'></i></button>
                    
               
                {/* <button id='pause' title='pause' disabled>
                    <i className='fa fa-pause'></i>
                </button>
                <button id='resume' title='Resume' disabled>
                    <i className='fa fa-resume'></i>
                </button> */}

                <button id='stop' title='Stop' disabled>
                    <i className='fa fa-stop'></i>
                </button>
                <br></br>
                <label>Cost Value:</label>
                <input type="text" id="costOuput" readOnly></input>
                <div id="ComponentPlace" width={960} height={500} margin={40}></div>
            </div>
        );
    }
}

export default TSNEView;