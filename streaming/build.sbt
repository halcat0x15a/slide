scalaVersion := "2.10.0"

resolvers += "mth.io" at "http://repo.mth.io/snapshots"

libraryDependencies += "machines" %% "machines" % "0.1-SNAPSHOT"

scalacOptions += "-feature"
