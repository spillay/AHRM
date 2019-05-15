import React, { Component } from 'react';
import EntropyCtl from '../../../components/Views/controllers/EntropyCtl'
import DateHelper  from '../../../components/SP/data/DateHelper';
import withTour from '../../../components/tour/withTour';
import withNav from '../../../components/navigation/withNav';
import withAuth from '../../../components/security/withAuth';
//import ReactBootstrapSlider from 'react-bootstrap-slider';
import archiImage from '../../../images/architecture.png';
import SPPanel from '../../../components/Views/ui/SPPanel';

const title = 'Email Analysis';
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

class EmailData extends React.Component {
    constructor(props) {
        super(props)
        var dh = new DateHelper("01/01/2001")
        var dte = new Date("01-01-2001");
        var ticks = []
        var tickLabel = []
        for (var i = 0; i < 25; i++) {
            ticks.push(i)
            var label = i
            tickLabel.push(label)
        }
        this.state = {
            pivotDate: dte.getTime(),
            date: "2001-01-01",
            email: "s..shively@enron.com",
            interval: "1h",
            show: false,
            tshow: false,
            searchShow: true,
            min: 0,
            max: 24,
            currentValue: 0,
            step: 1,
            ticks: ticks,
            tickLabel: tickLabel
        }

    }
    changeValue = (e) => {
        console.log(e);
    }
    componentDidMount() {
        //this.component.initialSize(this.compContainer);

    }
    render() {
        return (
            <SPPanel headerCustomClass="contentHeading" bodyCustomClass="contentBody" panelCustomClass="contentPanel"
            header={<span>
                <i className="fa fa-clock-o fa-fw" /> Architecture Overview
            </span>}
            body={<span>
               <img src={archiImage} />
            </span>}
        >

        </SPPanel>
        )
    }
}
//export default withAuth(EmailData);
export default withAuth(withTour(withNav(EmailData, { title: 'Architecture' }), steps));
/*
  <div className="infoPanel">
                <div className="panel-heading">
                    <i className="fa fa-info-circle" /> Admin Analysis
                </div>
                <div className="infoBody">
                    <hr />
                    <datalist id="range1">
                        <option value="0" label="0" />
                        <option value="1" label="1hr" />
                        <option value="2" label="2hr" />
                        <option value="3" label="3hr" />
                        <option value="4" label="4hr" />
                        <option value="5" label="5hr" />
                        <option value="6" label="6hr" />
                        <option value="7" label="7hr" />
                        <option value="8" label="8hr" />
                        <option value="9" label="9hr" />
                        <option value="10" label="10hr" />
                        <option value="11" label="11hr" />
                        <option value="12" label="12hr" />
                        <option value="13" label="13hr" />
                        <option value="14" label="14hr" />
                        <option value="15" label="15hr" />
                        <option value="16" label="16hr" />
                        <option value="17" label="17hr" />
                        <option value="18" label="18hr" />
                        <option value="19" label="19hr" />
                        <option value="20" label="20hr" />
                        <option value="21" label="21hr" />
                        <option value="22" label="22hr" />
                        <option value="23" label="23hr" />
                        <option value="24" label="24hr" />
                    </datalist>
                    <div className="nrange">
                        <input type="range" min="0" max="24" list="range1" onChange={this.changeRange} />
                    </div>
                    <ul className="range-labels">
                        <li>0 Hr</li>
                        <li>1 Hr</li>
                        <li>2 Hr</li>
                        <li>3 Hr</li>
                        <li>4 Hr</li>
                        <li>5 Hr</li>
                        <li>6 Hr</li>
                        <li>7 Hr</li>
                        <li>8 Hr</li>
                        <li>9 Hr</li>
                        <li>10 Hr</li>
                        <li>11 Hr</li>
                        <li>12 Hr</li>
                        <li>13 Hr</li>
                        <li>14 Hr</li>
                        <li>15 Hr</li>
                        <li>16 Hr</li>
                        <li>17 Hr</li>
                        <li>18 Hr</li>
                        <li>19 Hr</li>
                        <li>20 Hr</li>
                        <li>21 Hr</li>
                        <li>22 Hr</li>
                        <li>23 Hr</li>
                        <li>24 Hr</li>
                    </ul>
                </div>
            </div>
*/