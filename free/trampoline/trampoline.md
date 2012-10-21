!SLIDE

# 3. Tampolines: Trading stack for heap

!SLIDE

# Trampoline

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

runTは再帰的に次のステップを呼び出し、結果を得る

!SLIDE

# Trampolineを用いた相互再帰

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

!SLIDE

```scala
scala> even(10000)
res0: Trampoline[Boolean] = More(<function0>)

scala> .runT
res1: Boolean = true

```
