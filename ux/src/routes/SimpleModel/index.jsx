import React, { Component } from 'react';
import TextView from '../../components/Views/TextView';
import withTour from '../../components/tour/withTour';
import withNav from '../../components/navigation/withNav';
import withAuth from '../../components/security/withAuth';

const title = 'Text Analysis';
// Steps to provide a guided tour of the component
const steps = [
  {
    target: '#org-area',
    content: 'This if my awesome feature!',
    placement: 'bottom',
  },
  {
    target: '#org-bar',
    content: 'This if my awesome feature!',
    placement: 'bottom',
  }
]

class SimpleModel extends React.Component {
    constructor(props) {
        super(props);
        console.log("In Constructor of SimpleModel")

    }

    componentDidMount() {
        //this.component.initialSize(this.compContainer);
    }
    render() {
        return (
            <div className="infoPanel">
                <div className="panel-heading">
                    <i className="fa fa-info-circle" /> Text Analysis
                </div>
                <div className="infoBody">
                    <TextView />
                </div>
            </div>
        )
    }
}
export default withAuth(withTour(withNav(SimpleModel,{title:'Simple Text Analysis'}), steps));