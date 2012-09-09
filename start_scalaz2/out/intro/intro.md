!SLIDE

# Start Scalaz!!

!SLIDE

# 会場案内とか？

!SLIDE

# 発表者

* よしださんしろう([halcat0x15a](https://twitter.com/halcat0x15a))
* Scala, Clojure, Haskellとか
* Scalaは2.5年くらい書いてます
* Scalazは1年くらい
* 大学生になりました
* 夏休みです（今日まで）

!SLIDE

# 今日の内容

* Scalaz Data Types

!SLIDE

* [apidoc](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/)

* build.sbt

```scala
scalaVersion := "2.10.0-M7"

libraryDependencies <+= scalaVersion(v =>
  "org.scalaz" %  ("scalaz-core_" + v) % "7.0.0-M3"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation"
)

initialCommands in console := "import scalaz._, Scalaz._"
```

!SLIDE

* 発表中のツッコミは大歓迎
* わからないところがあったら聞いて下さい
* 適当に休憩をとります
