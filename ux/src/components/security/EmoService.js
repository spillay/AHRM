import jwt_decode from 'jwt-decode';

export default class EmoService {
    constructor(server) {
        //console.log("EmoService Constructor")
        //this.server =  '{"host":"http://192.168.1.5:9200","log":"trace"}'
        //this.domain = process.env.SERVER
        //this.domain = 'http://192.168.1.5:9000'
        //this.server =  '{"host":"http://149.28.59.50:9200","log":"trace"}'
        //this.server = server || '{"host":"' + process.env.ES + http://192.168.1.5:9200'","log":"trace"}'
        // this.server = "{'host':'sp008.hopto.org:9200','log':'trace'}"
        if (process.env.NODE_ENV === 'development'){
            //this.domain = 'http://localhost:9000'
            this.domain = 'http://149.28.59.50:9000'
        } else {
            this.domain = 'http://149.28.59.50:9000'
        }
        this.fetch = this.fetch.bind(this)
        this.getProfile = this.getProfile.bind(this)
    }
    getEmotions(msg) {
        return this.fetch(`${this.domain}/api/emotion`, {
            method: 'POST',
            body: JSON.stringify( msg )
        });
    }
    getEntropy(msg) {
        return this.fetch(`${this.domain}/api/entropy`, {
            method: 'POST',
            body: JSON.stringify( msg )
        });
    }
    getDeception(msg) {
        return this.fetch(`${this.domain}/api/deception`, {
            method: 'POST',
            body: JSON.stringify( msg )
        });
    }
    loggedIn() {
        // Checks if there is a saved token and it's still valid
        const token = this.getToken()
        return !!token && !this.isTokenExpired(token) // handwaiving here
    }

    isTokenExpired(token) {
        try {
            const decoded = jwt_decode(token);
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
        return jwt_decode(this.getToken());
    }


    fetch(url, options) {
        // performs api calls sending the required authentication headers
        //console.log(this.getToken())
        const headers = {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }

        if (this.loggedIn()) {
            headers['Authorization'] = 'Bearer ' + this.getToken()
            headers['X-Auth-Token'] = this.getToken()
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
