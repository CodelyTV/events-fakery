import com.typesafe.sbt.SbtNativePackager.Universal
import NativePackagerHelper._

scalaVersion := "2.12.12"
version := "0.1.0-SNAPSHOT"
name := "events-fakery"
organization := "com.codely"

val circeVersion = "0.14.5"

libraryDependencies ++= Seq(
  "org.apache.kafka"            % "kafka-clients"              % "3.4.0",
  "com.typesafe"                % "config"                     % "1.4.2",
  "com.github.scopt"           %% "scopt"                      % "4.1.0",
  "org.scalacheck"             %% "scalacheck"                 % "1.17.0",
  "io.circe"                   %% "circe-core"                 % circeVersion,
  "io.circe"                   %% "circe-generic"              % circeVersion,
  "io.circe"                   %% "circe-parser"               % circeVersion,
  "com.github.javafaker"        % "javafaker"                  % "1.0.2",
  "co.fs2"                     %% "fs2-core"                   % "3.10.2",
  "com.github.fd4s"            %% "fs2-kafka"                  % "3.5.1",
  "org.typelevel"              %% "cats-core"                  % "2.10.0",
  "org.typelevel"              %% "cats-effect"                % "3.2.9",
  "com.typesafe.scala-logging" %% "scala-logging"              % "3.9.5",
  "ch.qos.logback"              % "logback-classic"            % "1.2.10",
  "com.dimafeng"               %% "testcontainers-scala-kafka" % "0.39.0" % Test,
  "org.testcontainers"          % "testcontainers"             % "1.16.0" % Test,
  "org.scalatest"              %% "scalatest"                  % "3.2.18" % Test,
  "org.scalatest"              %% "scalatest-flatspec"         % "3.2.18" % Test
)

enablePlugins(JavaAppPackaging, DockerPlugin)

// openjdk:8-jdk-alpine
dockerUpdateLatest := true
(Docker / maintainer) := "cherrejim@gmail.com"

Universal / mappings ++=
  directory(
    "src/main/resources/samples/"
  )

dockerExposedVolumes += "/json"
