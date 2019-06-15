

import React from 'react';
import withTour from '../../../components/tour/withTour';
import withNav from '../../../components/navigation/withNav';
import withAuth from '../../../components/security/withAuth';
import TSNEView from '../../../components/Views/TSNEView';

const title = 'TSNE  View';

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

class TSNEPage extends React.Component {
  constructor() {
    super();
    //this.Auth = new AuthService();
  }

  render() {
    return (
      <div className="infoPanel">
        <div className="panel-heading">
          <i className="fa fa-info-circle" /> T-SNE
      </div>
        <div className="infoBody">
          <TSNEView />
        </div>
      </div>
    );
  }
}


//export default TSNEPage;
//export default withAuth(withTour(withNav(TSNEPage,{title:'T-SNE Analysis'}), steps));
export default withTour(withNav(TSNEPage,{title:'T-SNE Analysis'}), steps);
