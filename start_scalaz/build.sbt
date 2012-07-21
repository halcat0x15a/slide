scalaVersion := "2.10.0-M4"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0-SNAPSHOT"
)

scalacOptions += "-feature"

initialCommands in console := "import scalaz._, Scalaz._"
