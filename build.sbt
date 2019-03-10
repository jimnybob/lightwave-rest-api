name := "lightwave-rest-api"

version := "1.0"

lazy val `lightwave-rest-api` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(jdbc, ehcache, ws,/* specs2 % Test,*/ guice, 
  "org.typelevel" %% "cats-core" % "1.6.0",
  "org.scalatest"     %% "scalatest" % "3.0.6" % Test,
  "org.scalamock"     %% "scalamock-scalatest-support" % "3.6.0" % Test)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")  

      