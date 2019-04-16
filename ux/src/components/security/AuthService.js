import decode from 'jwt-decode';
export default class AuthService {
    constructor(domain) {
        //this.domain = 'http://149.28.59.50:9000'
        //this.domain = 'http://192.168.1.5:9000'
        //this.domain = domain || 'http://localhost:9000'
        if (process.env.NODE_ENV === 'development'){
            //this.domain = 'http://localhost:9000'
            this.domain = 'http://149.28.59.50:9000'
        } else {
            this.domain = 'http://149.28.59.50:9000'
        }
        console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",this.domain)
        console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",process.env.NODE_ENV)
        this.fetch = this.fetch.bind(this)
        this.login = this.login.bind(this)
        this.getProfile = this.getProfile.bind(this)
    }

    login(username, password) {
        // Get a token
        return this.fetch(`${this.domain}/api/auth/signin/credentials`, {
            method: 'POST',
            body: JSON.stringify( {identifier: username,  password: password} )
        }).then(res => {
            this.setToken(res.token)
            return Promise.resolve(res);
        })
    }

    loggedIn() {
        // Checks if there is a saved token and it's still valid
        const token = this.getToken()
        return !!token && !this.isTokenExpired(token) // handwaiving here
    }

    isTokenExpired(token) {
        try {
            const decoded = decode(token);
            if (decoded.exp < Date.now() / 1000) {
                return true;
            }
            else
                return false;
        }
        catch (err) {
            return false;
        }
    }

    setToken(idToken) {
        // Saves user token to localStorage
        localStorage.setItem('id_token', idToken)
    }

    getToken() {
        // Retrieves the user token from localStorage
        return localStorage.getItem('id_token')
    }

    logout() {
        // Clear user token and profile data from localStorage
        localStorage.removeItem('id_token');
    }

    getProfile() {
        return decode(this.getToken());
    }


    fetch(url, options) {
        // performs api calls sending the required authentication headers
        const headers = {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*'
        }

        if (this.loggedIn()) {
            headers['Authorization'] = 'Bearer ' + this.getToken()
        }

        return fetch(url, {
            headers,
            ...options
        })
            .then(this._checkStatus)
            .then(response => response.json())
    }

    _checkStatus(response) {
        // raises an error in case response status is not a success
        if (response.status >= 200 && response.status < 300) {
            return response
        } else {
            var error = new Error(response.statusText)
            error.response = response
            throw error
        }
    }
}