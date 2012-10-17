!SLIDE

# 3. とらんぽりん

```scala
sealed trait Trampoline[+A] {
  final def runT: A =
    this match {
	case More(k) => k().runT
	case Done(v) => v
    }
}

case class Done[+A](a: A)
  extends Trampoline[A]

case class More[+A](k: () => Trampoline[A])
  extends Trampoline[A]
```

!SLIDE

```scala
lazy val even: Int => Trampoline[Boolean] = {
  case 0 => Done(true)
  case n => More(() => odd(n - 1))
}

lazy val odd: Int => Trampoline[Boolean] = {
  case 0 => Done(false)
  case n => More(() => even(n - 1))
}
```
