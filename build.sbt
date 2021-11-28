name := "de_take_home"

version := "0.1"

scalaVersion := "2.13.7"

val zioVersion = "1.0.12"
val catsVersion = "2.6.1"
val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-logging" % "0.5.14",

  "org.typelevel" %% "cats-core" % catsVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.typelevel" %% "cats-laws" % "2.7.0" % Test,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test
)

Compile / run / mainClass := Some("andy42.de.Mission1")