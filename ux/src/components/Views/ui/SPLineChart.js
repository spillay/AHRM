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
import {
    LineChart, Tooltip,
    Line, XAxis, YAxis, Area,
    CartesianGrid, AreaChart, Bar, BarChart,
    ResponsiveContainer
} from 'recharts';
import { emotionPalette } from '../../config/EmotionConfig.js';
import Color from 'color';

const origdata = [
    { name: 'Page A', uv: 4000, pv: 2400, amt: 2400, value: 600 },
    { name: 'Page B', uv: 3000, pv: 1398, amt: 2210, value: 300 },
    { name: 'Page C', uv: 2000, pv: 9800, amt: 2290, value: 500 },
    { name: 'Page D', uv: 2780, pv: 3908, amt: 2000, value: 400 },
    { name: 'Page E', uv: 1890, pv: 4800, amt: 2181, value: 200 },
    { name: 'Page F', uv: 2390, pv: 3800, amt: 2500, value: 700 },
    { name: 'Page G', uv: 3490, pv: 4300, amt: 2100, value: 100 },
];


export default class SPLineChart extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <div>
                <ResponsiveContainer width="100%" aspect={2}>
                    <LineChart data={this.props.data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                        <CartesianGrid stroke="#ccc" />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        {emotionPalette.map(function (d) {
                            return (<Line type="monotone" key={d.emotion} dataKey={d.emotion} stroke={Color(d.color)} />);
                        })}
                    </LineChart>
                </ResponsiveContainer>
            </div>
        )
    }
}
/*
 
*/