// @GENERATOR:play-routes-compiler
// @SOURCE:/Data/2019/git/AHRM/store/conf/routes
// @DATE:Wed Apr 17 10:37:51 EDT 2019


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
