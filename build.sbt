name := "wordle"

version := "0.3.1"

scalaVersion := "2.13.8"

enablePlugins(JavaAppPackaging)

javaOptions in Universal ++= Seq(
  "-J-Xmx16g"
)

mainClass in (Compile, run) := Some("wordle.wordle")

lazy val dependencies = new {
  val catsCore = "org.typelevel" %% "cats-core" % "2.7.0"
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.11"
}

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
libraryDependencies ++= Seq(dependencies.catsCore, dependencies.catsEffect)