name := """CommsMgr"""
organization := "com.dsleng"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
libraryDependencies += "com.typesafe.play" %% "play-iteratees" % "2.6.1"
libraryDependencies += ws
libraryDependencies += "javax.mail" % "mail" % "1.5.0-b01"
libraryDependencies += "org.apache.commons" % "commons-email" % "1.5"
libraryDependencies += "tech.blueglacier" % "email-mime-parser" % "1.0.2"
libraryDependencies += "commons-net" % "commons-net" % "3.6"
libraryDependencies += "org.scalanlp" % "breeze_2.12" % "0.13.2"
libraryDependencies += "org.elasticsearch" % "elasticsearch" % "7.0.1"
libraryDependencies += "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.0.1"
libraryDependencies += "com.pff" % "java-libpst" % "0.8.1"




// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.dsleng.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.dsleng.binders._"
PlayKeys.devSettings := Seq("play.server.http.port" -> "9000")
