scalaVersion := "2.10.0-M6"

resolvers += "Maven Repository" at "http://repo1.maven.org/maven2"

libraryDependencies ++= Seq(
  "org.scalaz" % "scalaz-core_2.10.0-M6" % "7.0.0-M2"
)

scalacOptions += "-feature"

initialCommands in console := "import scalaz._, Scalaz._"
