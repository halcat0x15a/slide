!SLIDE

# [Id](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Id$)

!SLIDE

## 恒等モナド

```scala
("geso": Id[String]) assert_=== "geso"
```

!SLIDE

# [IdOps](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.syntax.IdOps)

## 全ての型に対してシンタックスが供給される

!SLIDE

# some, none, left, right

## OptionやEitherのインスタンスを生成する

```scala
1.some assert_=== Some(1)
none[Int] assert_=== None
assert(1.right[String] === Right(1))
assert("geso".left[Int] === Left("geso"))
```

!SLIDE

# |>

## 関数に自身を適用する

```scala
1 |> (1 +) assert_=== 2
"geso" |> (_.size) assert_=== 4
0 |> Show[Int].shows assert_=== "0"
```
