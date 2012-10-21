!SLIDE

# 4. Making every call a tail cal

!SLIDE

## 最初に挙げた例を解決出来るか？

!SLIDE

```scala
val Zero = BigInt(0)
val One = BigInt(1)

lazy val factorial: BigInt => Trampoline[BigInt] = {
  case Zero | One => Done(One)
  case n => More(() => Done(n * factorial(n - 1).runT))
}
```

!SLIDE

```scala
scala> factorial(10000)
res0: Trampoline[BigInt] = More(<function0>)

scala> .runT
java.lang.StackOverflowError
        at scala.math.BigInt.bigInteger(BigInt.scala:117)
        at scala.math.BigInt.compare(BigInt.scala:182)
        at scala.math.BigInt.equals(BigInt.scala:178)
        at scala.math.BigInt.equals(BigInt.scala:126)
        at scala.runtime.BoxesRunTime.equalsNumNum(Unknown Source)
        at $anonfun$factorial$1.apply(Trampoline.scala:77)
        at $anonfun$factorial$1.apply(Trampoline.scala:76)
        at $anonfun$factorial$1$$anonfun$apply$3.apply(Trampoline.scala:78)
        at $anonfun$factorial$1$$anonfun$apply$3.apply(Trampoline.scala:78)
        at Trampoline$class.runT(Trampoline.scala:52)
        at More.runT(Trampoline.scala:59)
        at $anonfun$factorial$1$$anonfun$apply$3.apply(Trampoline.scala:78)
        .
        .
        .
```

!SLIDE

関数内でrunTを呼び出してしまっている

!SLIDE

# Trampoline Monad

!SLIDE

## 4.1 A Trampoline monad?

モナドにすることで解決を試みます

!SLIDE

# >>=

## 単純に実装すると

```scala
def flatMap[B](f: A => Trampoline[B]) =
  More(() => f(runT))
```

!SLIDE

しかし、flatMap内でrunTを呼び出してしまうと先ほどと同じ結果になってしまう。

!SLIDE

## 4.2 Building the monad right in

ここではTrampolineの構成子を追加します

!SLIDE

```scala
case class FlatMap[A, +B](sub: Trampoline[A], k: A => Trampoline[B])
  extends Trampoline[B]
```

!SLIDE

flatMap, mapは次のように定義できる

!SLIDE

```scala
def flatMap[B](f: A => Trampoline[B]): Trampoline[B] =
  this match {
    case a FlatMap g =>
      FlatMap(a, (x: Any) => g(x) flatMap f)
    case x => FlatMap(x, f)
  }
def map[B](f: A => B): Trampoline[B] =
  flatMap(a => Done(f(a)))
```

!SLIDE

構成子を追加したことでrunTに変更を加えなければならない

新しいrunTは次に示す、resumeメソッドによって定義される

!SLIDE

```scala
final def resume: Either[() => Trampoline[A], A] =
  this match {
    case Done(a) => Right(a)
    case More(k) => Left(k)
    case a FlatMap f => a match {
      case Done(a) => f(a).resume
      case More(k) => Left(() => k() flatMap f)
      case b FlatMap g => b.flatMap((x: Any) => g(x) flatMap f).resume
    }
  }
```

!SLIDE

resumeメソッドはFlatMapを適用して結果か次のステップを返す

!SLIDE

runTはresumeを利用して、以下の様に書くことが出来る

```scala
final def runT: A = resume match {
  case Right(a) => a
  case Left(k) => k().runT
}
```

!SLIDE

resume, runTは末尾で自身を呼び出しているので、このメソッドはコンパイラによって最適化される

!SLIDE

## 4.4 Stackless Scala

!SLIDE

flatMap, mapが定義されたことによって最初の例は次のようになる

!SLIDE

```scala
val Zero = BigInt(0)
val One = BigInt(1)
lazy val factorial: BigInt => Trampoline[BigInt] = {
  case Zero | One => Done(One)
  case n => More(() => factorial(n - 1)).map(n *)
}
```

!SLIDE

もう一つ例を示す

!SLIDE

### よくあるふぃぼなっち数の例

```scala
lazy val fib: Int => Int = {
  case n if n < 2 => n
  case n => fib(n - 1) + fib(n - 2)
}
```

!SLIDE

末尾で呼び出しているのは+めそっど

最適化はされない

!SLIDE

Trampoline Monadとfor式を用いると自然な形で記述することが出来る

!SLIDE

```scala
lazy val fib: Int => Trampoline[Int] = {
  case n if n < 2 => Done(n)
  case n => for {
    x <- More(() => fib(n - 1))
    y <- More(() => fib(n - 2))
  } yield x + y
}
```
