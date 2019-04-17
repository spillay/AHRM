// @GENERATOR:play-routes-compiler
// @SOURCE:/Data/2019/git/AHRM/store/conf/routes
// @DATE:Wed Apr 17 10:37:51 EDT 2019

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:20
package controllers {

  // @LINE:20
  class ReverseFrontendController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:20
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
  }

  // @LINE:64
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:64
    def versioned(file:Asset): Call = {
      implicit lazy val _rrc = new play.core.routing.ReverseRouteContext(Map(("path", "/public"))); _rrc
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[play.api.mvc.PathBindable[Asset]].unbind("file", file))
    }
  
  }


}
