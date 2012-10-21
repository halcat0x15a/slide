!SLIDE

# 2. Background: Tail-call elimination in Scala

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

これは、varとwhileを使ったコードに機械的に変換出来る

!SLIDE

### コンパイルされたコードは以下と同等

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

> 末尾呼び出しならなんでも最適化されるのか？

!SLIDE

# 最適化されない例

!SLIDE

## 相互再帰

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
scala> even(100000)
java.lang.StackOverflowError
        at .even(<console>:13)
        at $anonfun$odd$1.apply$mcZI$sp(<console>:20)
        at $anonfun$even$1.apply$mcZI$sp(<console>:15)
        at $anonfun$odd$1.apply$mcZI$sp(<console>:20)
        at $anonfun$even$1.apply$mcZI$sp(<console>:15)
        at $anonfun$odd$1.apply$mcZI$sp(<console>:20)
        .
        .
        .
```

!SLIDE

これらの問題を解決するデータ構造が存在します
