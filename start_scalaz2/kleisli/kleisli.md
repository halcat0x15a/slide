!SLIDE

# Kleisli

!SLIDE

# 演習

## 関数f,gを用いて関数hを定義せよ

```scala
lazy val f: String => Option[Int] = _.parseInt.toOption
lazy val g: Int => Option[String] = n => n =/= 0 option (1 / n shows)

lazy val h: String => Option[String]
```

!SLIDE

# Kleisliを用いると、Monadを利用した合成が出来る

!SLIDE

```scala
lazy val h: Kleisli[Option, String, String] =
  Kleisli(f) >=> Kleisli(g)
```
