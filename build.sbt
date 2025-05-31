name := "wordle"

ThisBuild / version := "1.1.1"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .aggregate(core, weightList)

ThisBuild / libraryDependencies ++= commonDependencies

lazy val core = (project in file("core"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    libraryDependencies ++= Seq(
      dependencies.scalaTest,
      dependencies.scalaParallel,
      dependencies.lz4Java,
      dependencies.atto,
    ),
    Universal / javaOptions ++= Seq(
      "-J-Xmx2g"
    ),
    Universal / packageName := s"wordle-${version.value}",
    Compile / run / mainClass := Some("wordle.InteractiveApp"),
    assembly / mainClass := Some("wordle.InteractiveApp"),
  )

lazy val weightList = (project in file("weight-list"))
  .dependsOn(core)
  .settings(
    assembly / mainClass := Some("wordle.weightList.WeightsApp"),
  )


lazy val dependencies = new {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11" % "test"
  val scalaParallel = "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
  val catsCore = "org.typelevel" %% "cats-core" % "2.7.0"
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.12"
  val apacheCommonsCompress = "org.apache.commons" % "commons-compress" % "1.21"
  val lz4Java = "org.lz4" % "lz4-java" % "1.8.0"
  val atto = "org.tpolecat" %% "atto-core" % "0.9.5"
}

lazy val commonDependencies = Seq(
  dependencies.catsCore,
  dependencies.catsEffect,
)

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
