Global / onChangedBuildSource := ReloadOnSourceChanges

name := "play-prometheus-filters"
organization := "io.github.jyllands-posten"

version := "0.6.1-SNAPSHOT"

lazy val root = (project in file("."))

// All publishing configuration resides in sonatype.sbt
publishTo := sonatypePublishToBundle.value
credentials += Credentials(Path.userHome / ".sbt" / ".credentials.sonatype")

scalaVersion := "2.13.1"
crossScalaVersions := Seq(scalaVersion.value, "2.12.12")

val playVersion = "2.8.2"
val prometheusClientVersion = "0.8.1"

libraryDependencies ++= Seq(
  "io.prometheus"             % "simpleclient"          % prometheusClientVersion,
  "io.prometheus"             % "simpleclient_hotspot"  % prometheusClientVersion,
  "io.prometheus"             % "simpleclient_servlet"  % prometheusClientVersion,

  // Play libs. Are provided not to enforce a specific version.
  "com.typesafe.play"         %% "play"                 % playVersion % Provided,
  "com.typesafe.play"         %% "play-guice"           % playVersion % Provided
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play"    %% "scalatestplus-play"         % "5.1.0"     % Test,
  "org.mockito"               % "mockito-core"                % "3.2.4"    % Test
)
