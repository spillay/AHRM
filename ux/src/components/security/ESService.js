import jwt_decode from 'jwt-decode';

export default class ESService {
    constructor(server) {
        if (process.env.NODE_ENV === 'development'){
            this.domain = 'http://localhost:9000'
            //this.domain = 'http://149.28.59.50:9000'
        } else {
            this.domain = 'http://149.28.59.50:9000'
        }
        this.fetch = this.fetch.bind(this)
    }
    getData(query) {
        console.log(query)
        return this.fetch(`${this.domain}/api/query`, {
            method: 'POST',
            body: JSON.stringify( query )
        });
        // return this.fetch(`${this.domain}/api/query`, {
        //     method: 'POST',
        //     body: JSON.stringify( query )
        // }).then(res => {
        //     console.log("getData write",res)
        //     return Promise.resolve(res);
        // })
        //return Promise.resolve({token:"testtoken",expires:"01/01/2019"});
        /*
        console.log("window", window);
        if (typeof window !== 'undefined') {
            console.log("In elastic search section", this.server);
            var es = require('elasticsearch');
            var client = new es.Client(this.server);
            console.log(client, query);
            client.ping({
                // ping usually has a 3000ms timeout
                requestTimeout: 1000
            }, function (error) {
                if (error) {
                    console.trace('elasticsearch cluster is down!',error);
                } else {
                    console.log('All is well');
                }
            });
            return client.search(query);
        } else {
            console.log("no window  section");
            var thenable = {
                then: function (resolve) {
                    throw new TypeError('Throwing');
                    resolve('Resolving');
                }
            };
            return Promise.resolve(thenable);
        }
        */
    }
    getToken() {
        // Retrieves the user token from localStorage
        return localStorage.getItem('id_token')
    }
    loggedIn() {
        // Checks if there is a saved token and it's still valid
        const token = this.getToken()
        return !!token && !this.isTokenExpired(token) // handwaiving here
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
}
