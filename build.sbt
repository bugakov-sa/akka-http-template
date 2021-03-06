name := "akka-http-template"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  //multithreading
  "com.typesafe.akka" %% "akka-actor" % "2.4.10",
  //logging
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  //http
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11"
)

packAutoSettings
packResourceDir += (baseDirectory.value / "web" -> "web")
packResourceDir += (baseDirectory.value / "conf" -> "conf")