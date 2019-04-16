import React, { Component } from 'react';
import DeceptionView from '../../components/Views/DeceptionView';
import withTour from '../../components/tour/withTour';
import withNav from '../../components/navigation/withNav';
import withAuth from '../../components/security/withAuth';

const title = 'Deception Analysis';
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

class DeceptionModel extends React.Component {
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
                    <i className="fa fa-info-circle" /> Deception Analysis
                </div>
                <div className="infoBody">
                    <DeceptionView />
                </div>
            </div>
        )
    }
}
export default withAuth(withTour(withNav(DeceptionModel,{title:'Deception Analysis'}), steps));