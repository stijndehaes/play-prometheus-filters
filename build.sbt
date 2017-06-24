name := """play-prometheus-play.prometheus.filters"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "io.prometheus"             % "simpleclient"          % "0.0.23",
  "io.prometheus"             % "simpleclient_servlet"  % "0.0.23"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play"    %% "scalatestplus-play"         % "2.0.0"     % Test,
  "org.mockito"               % "mockito-core"                % "2.7.22"    % Test
)