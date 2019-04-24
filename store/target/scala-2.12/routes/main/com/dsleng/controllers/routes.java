// @GENERATOR:play-routes-compiler
// @SOURCE:/Data/2019/git/AHRM/store/conf/routes
// @DATE:Tue Apr 23 19:32:23 EDT 2019

package com.dsleng.controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final com.dsleng.controllers.ReverseCredentialsAuthController CredentialsAuthController = new com.dsleng.controllers.ReverseCredentialsAuthController(RoutesPrefix.byNamePrefix());
  public static final com.dsleng.controllers.ReverseApplicationController ApplicationController = new com.dsleng.controllers.ReverseApplicationController(RoutesPrefix.byNamePrefix());
  public static final com.dsleng.controllers.ReverseBLController BLController = new com.dsleng.controllers.ReverseBLController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final com.dsleng.controllers.javascript.ReverseCredentialsAuthController CredentialsAuthController = new com.dsleng.controllers.javascript.ReverseCredentialsAuthController(RoutesPrefix.byNamePrefix());
    public static final com.dsleng.controllers.javascript.ReverseApplicationController ApplicationController = new com.dsleng.controllers.javascript.ReverseApplicationController(RoutesPrefix.byNamePrefix());
    public static final com.dsleng.controllers.javascript.ReverseBLController BLController = new com.dsleng.controllers.javascript.ReverseBLController(RoutesPrefix.byNamePrefix());
  }

}
