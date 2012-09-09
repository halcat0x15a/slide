!SLIDE

# Arrow

!SLIDE

# Category

## 恒等射と合成射

```scala
lazy val f: String => Int = _.toInt
lazy val g: Int => String = 1 / _ shows
lazy val h: String => String = f andThen g
```

!SLIDE

# Compose

## >>>

```scala
lazy val h: String => String = f >>> g

lazy val f: Kleisli[Option, String, Int] = Kleisli(_.parseInt.toOption)
lazy val g: Kleisli[Option, Int, String] = Kleisli(n => n =/= 0 option (1 / n shows))
lazy val h: Kleisli[Option, String, String] = f >>> g
```

!SLIDE

# ArrId

```scala
ArrId[Function1].id(2) assert_=== 2
ArrId[({ type F[A, B] = Kleisli[Option, A, B] })#F].id(2) assert_=== Some(2)
```

!SLIDE

* Arrow
* Choice
* Split

!SLIDE

# Arrow

## FizzBuzz

```scala
def mod(n: Int, s: String): Int => Option[String] =
  _ % n === 0 option s
lazy val fold: ((Option[String], Option[String])) => Option[String] =
  _.fold(_ |+| _)
lazy val default: ((Option[String], Int)) => String =
  _.fold(_ | _.shows)
lazy val fizzbuzz: Int => String =
  ((mod(3, "Fizz") &&& mod(5, "Buzz")) >>> fold &&& identity) >>> default

fizzbuzz(2) assert_=== "2"
fizzbuzz(3) assert_=== "Fizz"
fizzbuzz(5) assert_=== "Buzz"
fizzbuzz(15) assert_=== "FizzBuzz"
```

!SLIDE

# ArrowOps

```scala
[F[_, _], A, B, C](f: F[A, B], g: F[A, C]): F[A, (B, C)] = f &&& g
[F[_, _], A, B, C, D](f: F[A, B], g: F[C, D]): F[(A, C), (B, D)] = f *** g
```
