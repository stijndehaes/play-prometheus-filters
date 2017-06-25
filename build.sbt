name := "play-prometheus-filters"
organization := "com.github.stijndehaes"

version := "0.1.0"

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
    makePomConfiguration ~= { _.copy(configurations = Some(Seq(Compile, Runtime, Optional))) },
    pomExtra :=
      <url>https://github.com/stijndehaes/playPrometheusFilters</url>
        <licenses>
          <license>
            <name>MIT License</name>
            <url>http://www.opensource.org/licenses/mit-license.php</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:stijndehaes/playPrometheusFilters.git</url>
          <connection>scm:git:git@github.com:stijndehaes/playPrometheusFilters.git</connection>
        </scm>
        <developers>
          <developer>
            <id>stijndehaes</id>u
            <name>Stijn De Haes</name>
            <timezone>+</timezone>
          </developer>
        </developers>

  )
scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "io.prometheus"             % "simpleclient"          % "0.0.23",
  "io.prometheus"             % "simpleclient_servlet"  % "0.0.23"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play"    %% "scalatestplus-play"         % "2.0.0"     % Test,
  "org.mockito"               % "mockito-core"                % "2.7.22"    % Test
)