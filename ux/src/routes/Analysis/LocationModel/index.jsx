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

import SPPanel from '../../../components/Views/ui/SPPanel';
import DataGrid from '../../../components/SP/DataGrid.js';

import { View } from '../../../components/SP/View.js'
import { VisMgr } from '../../../components/SP/VisMgr.js'
import { Vis } from '../../../components/SP/Vis.js'
import ComponentView from '../../../components/SP/ComponentView';

import { dashConfig } from '../dashConfig.js';
//import {ComponentView} from '@spillay/vismanager';
//import {BlankView} from 'notificationsys';
import withTour from '../../../components/tour/withTour';
import withNav from '../../../components/navigation/withNav';
import withAuth from '../../../components/security/withAuth';

const title = 'Location Analysis';
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

class LocationModel extends React.Component {
    constructor(props) {
        super(props);
        console.log("In Constructor of LocationModel")
        var baseView = new View();
        var cf = JSON.parse(dashConfig.LocationView);
        var vm1 = new VisMgr(cf);
        console.log(vm1);
        baseView.addManager(vm1);
        var mgr = baseView.getManager(0);
        this.state = {
            'view': baseView,
            'mgr': mgr,
            'componentTitle': 'Please Select a Component to View'
        };
    }

    componentDidMount() {
        this.component.initialSize(this.compContainer);
    }
    render() {
        return (
            <div className="infoPanel">
                <div className="panel-heading">
                    <i className="fa fa-info-circle" /> Location Analysis
                    </div>

                <div className="infoBody" ref={(input) => { this.compContainer = input; }}>
                    <ComponentView ref={(comp) => { this.component = comp; }} height="450" vismgr={this.state.mgr}></ComponentView>
                </div>
            </div>
        )
    }
}
export default withAuth(withTour(withNav(LocationModel,{title:'Location Analysis'}), steps));