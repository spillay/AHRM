package com.dsleng.utils.responses.rest

import org.elasticsearch.client.{Response, ResponseListener, RestClient}

class SPResponseListener extends ResponseListener {
    def onFailure(e: Exception): Unit = {
        println(e);
    }
    def onSuccess(res: org.elasticsearch.client.Response): Unit = {
        println(res);
    }
}