!SLIDE

# 1. いんとろ

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
