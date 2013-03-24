!SLIDE

# Stream Processing Libraries!

[@halcat0x15a](https://twitter.com/halcat0x15a)

!SLIDE

# Iteratee

現在Scalaで広く使われている

* Play Framework
* Slick
* Akka
* Scalaz

!SLIDE

# 利点

* メモリ効率
* データの供給、処理、変換の分離

!SLIDE

# 欠点

書くのがめんどい

!SLIDE

# New Streaming IO

* Haskell
    * Machines
    * Conduit
* Scala
    * scala-machines
    * scala-conduit
    * scalaz.stream

!SLIDE

# 共通点

モナドとプリミティブ

```scala
for {
  input <- await[String] // 読み込み
  _ <- emit(input) // 書き込み
} yield ()
```

!SLIDE

# scala-machines

副作用が完全に分離されている

* Plan
* Driver
* Procedure

!SLIDE

# Plan

```scala
def append[A: Semigroup] = for {
  e1 <- await[A]
  e2 <- await[A]
  _ <- emit(e1 |+| e2)
} yield ()
```

!SLIDE

# Driver

```scala
def lines = new Driver[IO, String => Any] {
  val M = Monad[IO]
  def apply(k: String => Any) = readLn map k map Some.apply
}
```

!SLIDE

# Procedure

```scala
new Procedure[IO, String] {
  type K = String => Any
  def machine = append[String].compile
  def withDriver[R](f: Driver[IO, K] => IO[R]) = f(lines)
} foreach putStrLn
```

!SLIDE

# 変換

```scala
def count = for {
  s <- await[String]
  _ <- emit(s.size)
} yield ()

def show = for {
  i <- await[Int]
  _ <- emit(i.shows)
} yield ()

def machine = count.compile andThen show.compile
```

!SLIDE

# TwitterのUser Streams

[Gist](https://gist.github.com/halcat0x15a/5172645)

!SLIDE

# 雑感

* Iterateeより書きやすい(？)
* DriverとProcedureが大きくなる
* Planを構築するときに副作用が書けない
