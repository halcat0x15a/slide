scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
  "com.chuusai" %% "shapeless" % "2.1.0"
)

javacOptions += "-parameters"

scalacOptions += "-feature"
