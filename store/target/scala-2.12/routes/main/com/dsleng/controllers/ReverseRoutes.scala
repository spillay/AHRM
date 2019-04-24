// @GENERATOR:play-routes-compiler
// @SOURCE:/Data/2019/git/AHRM/store/conf/routes
// @DATE:Tue Apr 23 19:32:23 EDT 2019

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:26
package com.dsleng.controllers {

  // @LINE:50
  class ReverseCredentialsAuthController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:50
    def authenticate(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/auth/signin/credentials")
    }
  
  }

  // @LINE:26
  class ReverseApplicationController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:26
    def index(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api")
    }
  
  }

  // @LINE:34
  class ReverseBLController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:34
    def genEntropy(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/genentropy")
    }
  
    // @LINE:46
    def deception(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/deception")
    }
  
    // @LINE:40
    def emotion(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/emotion")
    }
  
    // @LINE:37
    def query(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/query")
    }
  
    // @LINE:43
    def entropy(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/entropy")
    }
  
  }


}
