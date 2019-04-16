import React from 'react';
import AuthService from './AuthService';


export default function withAuth(AuthComponent) {
    const Auth = new AuthService('http://localhost:8080');
    return class AuthWrapped extends React.Component {
        constructor(props) {
            super(props);
            this.state = {
             user: ""
            }
            //console.log("AuthWrapped",props)
        }
        componentWillMount() {
            //console.log("withAuth component",this.props.history)
            if (!Auth.loggedIn()) {
                this.props.history.push('/login')
                //this.props.history.replace('/login')
            }
            else {
                try {
                    const profile = Auth.getProfile()
                    this.setState({
                        user: profile
                    })
                }
                catch(err){
                    Auth.logout()
                    this.props.history.push('/login')
                    //this.props.history.replace('/login')
                }
            }
        }
        render() {
            if (this.state.user!="") {
                return (
                    <AuthComponent user={this.state.user} />
                )
            }
            else {
                return null
            }

        }
    };
}
/*
 if (this.state.user!="") {
                return (
                    <AuthComponent history={this.props.history} user={this.state.user} />
                )
            }
            else {
                return null
            }
*/
