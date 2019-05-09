import React, { Component } from 'react';
import loadImage from '../../../images/loading.gif'

class Spinner extends Component {
  constructor(props) {
    super(props);
  }
  render() {
    return (
      <img src={loadImage} width={this.props.width} height={this.props.height}/>
    )
  }
}
export default Spinner;