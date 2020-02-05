name := "play-prometheus-filters"
organization := "com.github.stijndehaes"

version := "0.6.0"

lazy val root = (project in file("."))
  .settings(
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    makePomConfiguration ~= { _.withConfigurations(Vector(Compile, Runtime, Optional)) },
    pomExtra :=
      <url>https://github.com/stijndehaes/play-prometheus-filters</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:stijndehaes/play-prometheus-filters.git</url>
        <connection>scm:git:git@github.com:stijndehaes/play-prometheus-filters.git</connection>
      </scm>
      <developers>
        <developer>
          <id>stijndehaes</id>
          <name>Stijn De Haes</name>
        </developer>
      </developers>
  )
scalaVersion := "2.13.1"

crossScalaVersions := Seq(scalaVersion.value, "2.12.8", "2.11.12")

val playVersion = "2.8.0"
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
  "org.scalatestplus.play"    %% "scalatestplus-play"         % "5.0.0"     % Test,
  "org.mockito"               % "mockito-core"                % "2.16.0"    % Test
)
