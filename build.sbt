name := "play-prometheus-filters"
organization := "com.github.stijndehaes"

version := "0.4.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
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
scalaVersion := "2.12.5"

crossScalaVersions := Seq(scalaVersion.value, "2.11.11")

libraryDependencies ++= Seq(
  guice,
  "io.prometheus"             % "simpleclient"          % "0.3.0",
  "io.prometheus"             % "simpleclient_servlet"  % "0.3.0"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play"    %% "scalatestplus-play"         % "3.1.2"     % Test,
  "org.mockito"               % "mockito-core"                % "2.16.0"    % Test
)
