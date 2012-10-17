scalaVersion := "2.10.0-M7"

libraryDependencies += 
  "org.scalaz" %  "scalaz-core" % "7.0.0-M3"  cross CrossVersion.full

scalacOptions ++= Seq(
  "-feature",
  "-deprecation"
)

initialCommands in console := "import scalaz._, Scalaz._"
