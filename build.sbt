Global / onChangedBuildSource := ReloadOnSourceChanges

name := "play-prometheus-filters"
organization := "io.github.jyllands-posten"

version := "0.6.2-SNAPSHOT"

lazy val root = project in file(".")

// All publishing configuration resides in sonatype.sbt
publishTo := sonatypePublishToBundle.value
credentials += Credentials(Path.userHome / ".sbt" / ".credentials.sonatype")

scalaVersion := "2.13.8"
crossScalaVersions := Seq(scalaVersion.value, "2.12.15")

val playVersion = "3.0.0"
val prometheusClientVersion = "0.16.0"

libraryDependencies ++= Seq(
  "io.prometheus" % "simpleclient" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_hotspot" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_servlet" % prometheusClientVersion,

  // Play libs. Are provided not to enforce a specific version.
  "org.playframework" %% "play" % playVersion % Provided,
  "org.playframework" %% "play-guice" % playVersion % Provided,

  // This library makes some Scala 2.13 APIs available on Scala 2.11 and 2.12.
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
  "org.mockito" % "mockito-core" % "5.8.0" % Test
)
