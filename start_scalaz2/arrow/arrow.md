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

# Arrow
