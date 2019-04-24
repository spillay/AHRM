// @GENERATOR:play-routes-compiler
// @SOURCE:/Data/2019/git/AHRM/store/conf/routes
// @DATE:Tue Apr 23 19:32:23 EDT 2019

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:26
package com.dsleng.controllers.javascript {

  // @LINE:50
  class ReverseCredentialsAuthController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:50
    def authenticate: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.dsleng.controllers.CredentialsAuthController.authenticate",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/auth/signin/credentials"})
        }
      """
    )
  
  }

  // @LINE:26
  class ReverseApplicationController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:26
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.dsleng.controllers.ApplicationController.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api"})
        }
      """
    )
  
  }

  // @LINE:34
  class ReverseBLController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:34
    def genEntropy: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.dsleng.controllers.BLController.genEntropy",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/genentropy"})
        }
      """
    )
  
    // @LINE:46
    def deception: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.dsleng.controllers.BLController.deception",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/deception"})
        }
      """
    )
  
    // @LINE:40
    def emotion: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.dsleng.controllers.BLController.emotion",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/emotion"})
        }
      """
    )
  
    // @LINE:37
    def query: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.dsleng.controllers.BLController.query",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/query"})
        }
      """
    )
  
    // @LINE:43
    def entropy: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.dsleng.controllers.BLController.entropy",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/entropy"})
        }
      """
    )
  
  }


}
