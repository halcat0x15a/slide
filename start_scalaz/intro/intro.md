!SLIDE

# Start Scalaz!

!SLIDE

# 会場案内とか？

!SLIDE

# 発表者

* よしださんしろう([halcat0x15a](https://twitter.com/halcat0x15a))
* Scala, Clojure, Haskellとか
* Scalaは2.5年くらい書いてます
* 大学生になりました
* 夏休みです

!SLIDE

# 今日の内容

* Type Class
* Scalaz7

!SLIDE

* [apidoc](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/)

* build.sbt

```scala
scalaVersion := "2.10.0-M4"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0-SNAPSHOT"
)

scalacOptions += "-feature"
```

!SLIDE

* 発表中のツッコミは大歓迎
* わからないところがあったら聞いて下さい
* 適当に休憩をとります
