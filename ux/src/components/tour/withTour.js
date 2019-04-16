import React, { Component } from 'react';
import Joyride from 'react-joyride';

function withTour(WrappedComponent,steps) {
    return class extends Component {
        constructor(props) {
            super(props);
            this.state = {
                run: false,
                steps: steps
            };
            console.log("steps",steps)
        }
        componentDidMount() {
            this.setState({ run: false });
        }
        render() {
            return (<div>
                <Joyride
                    steps={this.state.steps}
                    run={this.state.run}
                    callback={this.callback}
                    debug={true}
                />
                <WrappedComponent {...this.props} />
            </div>)
        }
    }
}
export default withTour;