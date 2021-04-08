Global / onChangedBuildSource := ReloadOnSourceChanges

name := "play-prometheus-filters"
organization := "io.github.jyllands-posten"

version := "0.6.2-SNAPSHOT"

lazy val root = (project in file("."))

// All publishing configuration resides in sonatype.sbt
publishTo := sonatypePublishToBundle.value
credentials += Credentials(Path.userHome / ".sbt" / ".credentials.sonatype")

scalaVersion := "2.13.5"
crossScalaVersions := Seq(scalaVersion.value, "2.12.12")

val playVersion = "2.8.8"
val prometheusClientVersion = "0.9.0"

libraryDependencies ++= Seq(
  "io.prometheus" % "simpleclient" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_hotspot" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_servlet" % prometheusClientVersion,

  // Play libs. Are provided not to enforce a specific version.
  "com.typesafe.play" %% "play" % playVersion % Provided,
  "com.typesafe.play" %% "play-guice" % playVersion % Provided,

  // This library makes some Scala 2.13 APIs available on Scala 2.11 and 2.12.
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.3"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.7.0" % Test,
  "org.mockito" % "mockito-core" % "3.9.0" % Test
)
