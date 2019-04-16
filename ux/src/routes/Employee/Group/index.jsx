import React, { PropTypes } from 'react';
import EmailRelationsCtl from '../../../components/Views/controllers/EmailRelationsCtl';
import withTour from '../../../components/tour/withTour';
import withNav from '../../../components/navigation/withNav';
import withAuth from '../../../components/security/withAuth';

const title = 'Group Analysis';
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

class GroupAnalysis extends React.Component {
    constructor(props) {
        super(props);
        console.log("In Constructor of GroupAnalysis")

    }

    componentDidMount() {
        //this.component.initialSize(this.compContainer);
    }
    render() {
        return (
            <div className="infoPanel">
                <div className="panel-heading">
                    <i className="fa fa-info-circle" /> Group Analysis
                </div>
                <div className="infoBody">
                    <EmailRelationsCtl />
                </div>
            </div>
        )
    }
}
export default withAuth(withTour(withNav(GroupAnalysis,{title:'Group Analysis'}), steps));