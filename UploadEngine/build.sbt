name := "UploadEngine"

version := "1.0.0"

scalaVersion := "2.12.3"

lazy val akkaVersion = "2.5.20"
lazy val playVersion = "2.7.1"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

libraryDependencies += "tech.blueglacier" % "email-mime-parser" % "1.0.5"
libraryDependencies += "com.dsleng" % "com.dsleng.model" % "1.0"

//libraryDependencies += "de.heikoseeberger" %% "akka-http-play-json" % "1.25.2"
//libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.9"

//conflictWarning := ConflictWarning.disable

