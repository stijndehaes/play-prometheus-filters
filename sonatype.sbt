publishMavenStyle := true

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

homepage := Some(url("https://github.com/Jyllands-Posten/play-prometheus-filters"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/Jyllands-Posten/play-prometheus-filters.git"),
    "scm:git:git@github.com:Jyllands-Posten/play-prometheus-filters.git"
  )
)

developers := List(
  Developer(id="SoerenSilkjaer", name="Søren Valentin Silkjær", email="soren.hansen@jp.dk", url=url("https://github.com/SoerenSilkjaer"))
)