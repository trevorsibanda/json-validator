name := """json-schema-validator"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  //jdbc,
  cache,
  ws,
  evolutions,	
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.8.11.2",
  "com.github.fge" % "json-schema-validator" % "2.2.6"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
