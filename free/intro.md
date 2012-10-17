!SLIDE

# いんとろ

!SLIDE

# StackOverflowError

## みなさん経験ありますね？

!SLIDE

# 例

```scala
val Zero = BigInt(0)
val One = BigInt(1)

lazy val factorial: BigInt => BigInt = {
  case Zero | One => One
  case n => n + factorial(n - 1)
}
```

!SLIDE

```scala
scala> factorial(10000)
java.lang.StackOverflowError
        at scala.math.BigInt$.maxCached(BigInt.scala:22)
        at scala.math.BigInt$.apply(BigInt.scala:39)
        at scala.math.BigInt$.int2bigInt(BigInt.scala:102)
        at $anonfun$factorial$1.apply(<console>:17)
        at $anonfun$factorial$1.apply(<console>:15)
        at $anonfun$factorial$1.apply(<console>:17)
        at $anonfun$factorial$1.apply(<console>:15)
        at $anonfun$factorial$1.apply(<console>:17)
        .
        .
        .
```

!SLIDE

# TCE in Scala

!SLIDE

# 末尾で自身を呼び出す関数

```scala
def foldl[A, B](as: List[A], b: B, f: (B, A) => B): B =
  as match {
    case Nil => b
    case x :: xs => foldl(xs, f(b, x), f)
  }
```

!SLIDE

# コンパイルされたコードは以下と同等

```scala
def foldl[A, B](as: List[A], b: B, f: (B, A) => B): B = {
  var z = b
  var az = as
  while (true) {
    az match {
	case Nil => return z
	case x :: xs => {
	  z = f(z, x)
	  az = xs
	}
    }
  }
  z
}
```

!SLIDE

# 末尾での関数を呼び出しでも最適化されない例

```scala
lazy val even: Int => Boolean = {
  case 0 => true
  case n => odd(n - 1)
}

lazy val odd: Int => Boolean = {
  case 0 => false
  case n => even(n - 1)
}
```

!SLIDE

```scala
scala> EvenOdd.even(10000)
java.lang.StackOverflowError
        at EvenOdd$.odd(FreeMonad.scala:127)
        at EvenOdd$$anonfun$even$1.apply$mcZI$sp(FreeMonad.scala:124)
        at EvenOdd$$anonfun$odd$1.apply$mcZI$sp(FreeMonad.scala:129)
        at EvenOdd$$anonfun$even$1.apply$mcZI$sp(FreeMonad.scala:124)
        at EvenOdd$$anonfun$odd$1.apply$mcZI$sp(FreeMonad.scala:129)
        at EvenOdd$$anonfun$even$1.apply$mcZI$sp(FreeMonad.scala:124)
        .
        .
        .
```

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