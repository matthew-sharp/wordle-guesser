name := "wordle"

version := "0.3.1"

scalaVersion := "3.1.3"

enablePlugins(JavaAppPackaging)

Universal / javaOptions ++= Seq(
  "-J-Xmx12g"
)

Compile / run / mainClass  := Some("wordle.InteractiveApp")

lazy val dependencies = new {
  val catsCore = "org.typelevel" %% "cats-core" % "2.7.0"
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.12"
}

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
libraryDependencies ++= Seq(dependencies.catsCore, dependencies.catsEffect)