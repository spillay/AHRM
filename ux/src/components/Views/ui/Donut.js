import React, { Component } from 'react';
import DonutChart from 'react-donut-chart';

class Donut extends Component {
  constructor(props) {
    super(props);
    var results = false;
    if (this.props.data.length > 0){
      results = true;
    }
    this.state = {
      data: this.props.data,
      show: results
    };

  }
  componentDidUpdate(){
    console.log("Donut Update")
    var results = false;
    if (this.props.data.length > 0){
      results = true;
    }
    this.state = {
      data: this.props.data,
      show: results
    };
  }
  render() {
    console.log("Pie Data>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", this.props.data,this.state.show)
    return (
      <div>
        {(this.props.data.length>0) && <DonutChart data={this.props.data} height={450} width={250} legend={this.props.legend}></DonutChart>}
      </div>
    )
  }
}
export default Donut;

/*
import { PieChart, Pie, Sector, ResponsiveContainer } from 'recharts';

const renderActiveShape = (props) => {
  const RADIAN = Math.PI / 180;
  const {
    cx, cy, midAngle, innerRadius, outerRadius, startAngle, endAngle, // eslint-disable-line
    fill, payload, percent, value // eslint-disable-line
  } = props;
  const sin = Math.sin(-RADIAN * midAngle);
  const cos = Math.cos(-RADIAN * midAngle);
  const sx = cx + ((outerRadius + 5) * cos);
  const sy = cy + ((outerRadius + 5) * sin);
  const mx = cx + ((outerRadius + 10) * cos);
  const my = cy + ((outerRadius + 10) * sin);
  const ex = mx + ((cos >= 0 ? 1 : -1) * 11);
  const ey = my;
  const textAnchor = cos >= 0 ? 'start' : 'end';

  return (
    <g>
      <text x={cx} y={cy} dy={8} textAnchor="middle" fill={fill}>{payload.name}</text>
      <Sector
        cx={cx}
        cy={cy}
        innerRadius={innerRadius}
        outerRadius={outerRadius}
        startAngle={startAngle}
        endAngle={endAngle}
        fill={fill}
      />
      <Sector
        cx={cx}
        cy={cy}
        startAngle={startAngle}
        endAngle={endAngle}
        innerRadius={outerRadius + 6}
        outerRadius={outerRadius + 10}
        fill={fill}
      />
      <path d={`M${sx},${sy}L${mx},${my}L${ex},${ey}`} stroke={fill} fill="none" />
      <circle cx={ex} cy={ey} r={2} fill={fill} stroke="none" />
      <text
        x={ex + ((cos >= 0 ? 1 : -1) * 12)}
        y={ey}
        textAnchor={textAnchor}
        fill="#333"
      >
        {`Value ${value}`}
      </text>
      <text x={ex + ((cos >= 0 ? 1 : -1) * 12)} y={ey} dy={18} textAnchor={textAnchor} fill="red">
        {`(Rate ${(percent * 100).toFixed(2)}%)`}
      </text>
    </g>
  );
};


class Donut extends Component {
  // static propTypes ={
  //   data: React.PropTypes.array,
  //   innerRadius: React.PropTypes.oneOfType([
  //     React.PropTypes.string,
  //     React.PropTypes.number,
  //   ]),
  //   outerRadius: React.PropTypes.oneOfType([
  //     React.PropTypes.string,
  //     React.PropTypes.number,
  //   ]),
  //   color: React.PropTypes.string,
  // }
  constructor(props) {
    super(props);
    this.state = {
      activeIndex: 0,
    };
    this.onPieEnter = this.onPieEnter.bind(this);
    console.log("Pie Data>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", this.props.data)
  }

  onPieEnter(data, index) {
    console.log("onPieEnter",data,index)
    this.setState({
      activeIndex: index,
    });
  }

  render() {
    return (
      <ResponsiveContainer width="100%" aspect={2} >
        <PieChart
          margin={{ top: 30, right: 30, left: 10, bottom: 10 }}
          onMouseEnter={(data, index) => { this.onPieEnter(data, index); }}
        >
          <Pie
            data={this.props.data}
            innerRadius={this.props.innerRadius}
            outerRadius={this.props.outerRadius}
            fill={this.props.color}
            dataKey="value"
          />
        </PieChart>
      </ResponsiveContainer>
    );
  }

}
export default Donut;
*/