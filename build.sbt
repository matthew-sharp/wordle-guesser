name := "wordle"

version := "0.1"

scalaVersion := "2.13.8"

enablePlugins(JavaAppPackaging)

mainClass in (Compile, run) := Some("wordle.wordle")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"