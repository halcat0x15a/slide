!SLIDE

# Validation

!SLIDE

# 成功、失敗を表す

```scala
2.success[String] assert_=== Validation.success(2)
"geso".failure[Int] assert_=== Validation.failure("geso")
```

!SLIDE

# 型で表せる

## Scalaには検査例外はない

!SLIDE

# parse

```scala
lazy val message: NumberFormatException => String = _.getMessage
lazy val parseInt: String => Validation[String, Int] = message <-: _.parseInt

parseInt("2") assert_=== 2.success
parseInt("geso") assert_=== """For input string: "geso"""".failure
```

!SLIDE

# Applicative Style

## Errorを蓄積する

```scala
(parseInt("2").toValidationNEL |@|
  parseInt("2").toValidationNEL)(_ * _) assert_===
    4.success

(parseInt("foo").toValidationNEL |@|
  parseInt("bar").toValidationNEL)(_ * _) assert_===
    NonEmptyList(
	"""For input string: "foo"""",
	"""For input string: "bar""""
    ).failure
```

!SLIDE

# Monad

