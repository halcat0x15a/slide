scalaVersion := "2.10.0-M7"

libraryDependencies ++= Seq(
  "org.scalaz" %  "scalaz-core" % "7.0.0-M3"  cross CrossVersion.full,
  "org.scalaz" %  "scalaz-effect" % "7.0.0-M3"  cross CrossVersion.full
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation"
)

initialCommands in console := "import scalaz._, Scalaz._"
