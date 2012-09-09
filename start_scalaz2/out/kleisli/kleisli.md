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

# Kleisliを用いるとMonadを利用した合成が出来る

!SLIDE

```scala
lazy val h: Kleisli[Option, String, String] =
  Kleisli(f) >=> Kleisli(g)
```

!SLIDE

# Reader

## I => O

```scala
def get(s: String): Reader[Map[String, String], String] =
  Reader(m => m(s))

(for {
  id <- get("id")
  age <- get("age")
} yield User(id, age.toInt)) run 
  Map(
    "id" -> "halcat0x15a",
    "age" -> "19"
  ) assert_=== User("halcat0x15a", 19)
```

!SLIDE

# Reader = Kleisli Id

## KleisliはReaderのモナド変換子版

!SLIDE

# 演習

* 先の例の結果をOptionで返すように変更せよ

```scala
def get(s: String): Kleisli[Option, Map[String, String], String]

(for {
  id <- get("id")
  age <- get("age")
} yield User(id, age.toInt)) run 
  Map("age" -> "19") assert_=== None
```
