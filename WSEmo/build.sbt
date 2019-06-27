name := """WSEmo"""
organization := "com.dsleng"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"
lazy val sparkVersion = "2.4.3"

evictionWarningOptions in update := EvictionWarningOptions.default.withWarnTransitiveEvictions(false)

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion exclude("com.fasterxml.jackson.core", "jackson-databind")
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion

//libraryDependencies += "com.typesafe.play" %% "play" % "2.6.17"
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.0-M1"


libraryDependencies += "com.dsleng" % "com.dsleng.emo" % "1.0"
libraryDependencies += "com.google.guava" % "guava" % "19.0"


dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.1"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.1" % Test
dependencyOverrides += "com.google.guava" % "guava" % "27.1-jre"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.dsleng.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.dsleng.binders._"

libraryDependencies += specs2 % Test

resolvers += Resolver.mavenLocal
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"




