name := "SpotifyPlaylistAnalysis"

version := "0.1"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "requests" % "0.8.0",
  "com.lihaoyi" %% "ujson" % "2.0.0"
)

dependencyOverrides += "com.lihaoyi" %% "geny" % "1.0.0"
