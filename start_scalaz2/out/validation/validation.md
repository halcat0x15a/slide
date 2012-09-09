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
import Validation._

def parseInt(s: String): String \?/ Int = try {
  s.toInt.success
} catch {
  case e: Throwable => e.getMessage.failure
}

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

# 演習

* キーとマップをとり、Validationで返す関数get
* マップからユーザーを構築する関数user

```scala
case class User(id: String, age: Int)
implicit lazy val ShowUser = Show.showA[User]
implicit lazy val EqualUser = Equal.equalA[User]

def get[K, V](k: K)(m: Map[K, V]): String \?/ V

def user(m: Map[String, String]): NonEmptyList[String] \?/ User

user(Map("id" -> "halcat0x15a", "age" -> "19")) assert_=== User("halcat0x15a", 19).success
user(Map("id" -> "halcat0x15a")) assert_=== NonEmptyList("key not found: age").failure
user(Map("age" -> "geso")) assert_===
  NonEmptyList("key not found: id", """For input string: "geso"""").failure
```
