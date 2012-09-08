scalaVersion := "2.10.0-M7"

libraryDependencies <+= scalaVersion(v =>
  "org.scalaz" %  ("scalaz-core_" + v) % "7.0.0-M3"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation"
)

initialCommands in console := "import scalaz._, Scalaz._"
