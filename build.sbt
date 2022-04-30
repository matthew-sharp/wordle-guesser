name := "wordle"

version := "0.1"

scalaVersion := "2.13.8"

enablePlugins(JavaAppPackaging)

Compile / mainClass := Some("wordle.wordle")
//mainClass in (Compile, run) := Some("wordle.WordleAutoApp")

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.11"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"