name := "wordle"

version := "0.3.1"

scalaVersion := "3.1.3"

enablePlugins(JavaAppPackaging)

Universal / javaOptions ++= Seq(
  "-J-Xmx6g"
)

Compile / run / mainClass  := Some("wordle.InteractiveApp")

lazy val dependencies = new {
  val catsCore = "org.typelevel" %% "cats-core" % "2.7.0"
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.12"
  val apacheCommonsCompress = "org.apache.commons" % "commons-compress" % "1.21"
  val lz4Java = "org.lz4" % "lz4-java" % "1.8.0"
}

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
libraryDependencies ++= Seq(dependencies.catsCore, dependencies.catsEffect)
libraryDependencies += dependencies.lz4Java

scalacOptions ++= Seq(
  "-Xfatal-warnings",

  // Linting options
  "-unchecked",
  "-deprecation",
)

val filterConsoleScalacOptions = { options: Seq[String] =>
  options.filterNot(Set(
    "-Xfatal-warnings",
    "-Werror",
    "-Wdead-code",
    "-Wunused:imports",
    "-Ywarn-unused:imports",
    "-Ywarn-unused-import",
    "-Ywarn-dead-code",
  ))
}

Compile / console / scalacOptions ~= filterConsoleScalacOptions
Test / console / scalacOptions ~= filterConsoleScalacOptions