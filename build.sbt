lazy val common_project = Seq(
  organization := "prg22",
  version := "0.1-SNAPSHOT",

  run / fork := true,
  run / connectInput := true,
  Global / cancelable := true,
  )

lazy val scala_project = common_project ++ Seq(
  scalaVersion := "2.13.6",   // コンパイルに使う scalac のバージョン
  scalacOptions := Seq("-feature", "-unchecked", "-deprecation"),
  # run / javaOptions += "-Xms256M -Xmx2G",
  Compile / scalaSource := baseDirectory.value / "src",
  )

lazy val root = (project in file(".")).settings(scala_project)
