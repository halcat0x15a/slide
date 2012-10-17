

!SLIDE

# 相互再帰

!SLIDE

# Trampoline

!SLIDE

# Trampoline Monad

```scala
val Zero = BigInt(0)
val One = BigInt(1)

lazy val factorial: BigInt => Free.Trampoline[BigInt] = {
  case Zero | One => Free.Return(One)
  case m => for {
    n <- Free.Suspend(() => factorial(m - 1))
  } yield m * n
}

factorial(10000).run
```

!SLIDE

# Free
