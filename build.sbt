name := "gitbucket-ganttchart-plugin"
organization := "io.github.gitbucket"
version := "1.2.0"
scalaVersion := "2.13.3"
gitbucketVersion := "4.35.0"

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)