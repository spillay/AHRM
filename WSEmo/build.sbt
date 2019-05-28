name := """WSEmo"""
organization := "com.dsleng"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.12"

evictionWarningOptions in update := EvictionWarningOptions.default.withWarnTransitiveEvictions(false)

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.1" exclude("com.fasterxml.jackson.core", "jackson-databind")
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.1"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.3.1"
//libraryDependencies += "com.johnsnowlabs.nlp" %% "spark-nlp" % "1.6.0"
////libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.1"
libraryDependencies += "com.typesafe.play" %% "play" % "2.6.17"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.1"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.1" % Test


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.dsleng.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.dsleng.binders._"

libraryDependencies += specs2 % Test

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"




