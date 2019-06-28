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
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.apache.spark" %% "spark-core" % "2.4.3",
  "org.apache.spark" %% "spark-sql" % "2.4.3"
)
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "tech.blueglacier" % "email-mime-parser" % "1.0.5"
libraryDependencies += "com.dsleng" % "com.dsleng.model" % "1.0"
libraryDependencies += "com.dsleng" % "com.dsleng.nlp" % "1.0"
libraryDependencies += "com.dsleng" % "com.dsleng.emo" % "1.0"

//libraryDependencies += "de.heikoseeberger" %% "akka-http-play-json" % "1.25.2"
//libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.9"

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2" classifier "models"

//libraryDependencies += "com.google.guava" % "guava" % "28.0-jre"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"


//conflictWarning := ConflictWarning.disable

