import React from 'react';
import { BarChart } from 'react-easy-chart';

export default class SimpleBarChart extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <BarChart
                axisLabels={{ x: 'My x Axis', y: 'My y Axis' }}
                axes
                grid
                colorBars
                height={250}
                width={650}
                data={this.props.data}
            />
        );
    }
}


/*
 data={[
                    { x: 'A', y: 20 },
                    { x: 'B', y: 30 },
                    { x: 'C', y: 40 },
                    { x: 'D', y: 20 },
                    { x: 'E', y: 40 },
                    { x: 'F', y: 25 },
                    { x: 'G', y: 5, color: 'orange' }
                ]}
*/