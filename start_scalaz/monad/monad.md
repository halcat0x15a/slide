!SLIDE

# Monad

!SLIDE

# Higher Kinds

## 型をパラメータとしてとる型

```scala
import scala.language.higherKinds
def triple[F[_]: Plus, A](fa: F[A]) = fa <+> fa <+> fa
triple(Option(1)) assert_=== Option(1)
triple(List(1)) assert_=== List(1, 1, 1)
```

!SLIDE

# [Functor](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Functor)

## map

### 関数をコンテナに適用する

```scala
object vector {
  implicit object VectorInstance extends Functor[Vector] {
    def map[A, B](v: Vector[A])(f: A => B) = v map f
  }
}

def fdouble[F[_]: Functor, A: Semigroup](fa: F[A]) = fa.map(a => a |+| a)
fdouble(List(1, 2, 3)) assert_=== List(2, 4, 6)
fdouble("geso".some) assert_=== Some("gesogeso")
import vector._
fdouble(Vector(1.2, 2.1)) assert_=== Vector(2.4, 4.2)
```

!SLIDE

# [FunctorLaw](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Functor$FunctorLaw)

## mapの性質

* map(fa)(x => x) == fa
* map(map(fa)(f))(g) == map(fa)(g compose f)

```scala
val fa = List(1, 2)
lazy val f: Int => Int = _ + 2
lazy val g: Int => Int = _ * 2
fa map (x => x) assert_=== fa
fa map f map g assert_=== (fa map g <<< f)
```

!SLIDE

# [Pointed](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Pointed)

## point

### コンテナを構築する

```scala
object vector {
  implicit object VectorInstance extends Pointed[Vector] {
    def map[A, B](v: Vector[A])(f: A => B) = v map f
    def point[A](a: => A) = Vector(a)
  }
}

Pointed[List].point(1) assert_=== List(1)
Pointed[Option].point(1) assert_=== Some(1)
import vector._
Pointed[Vector].point(1) assert_=== Vector(1)
```

!SLIDE

# 型の部分適用

```scala
assert(Functor[({ type F[A] = Either[String, A] })#F].map(Right(1))(_.succ) === Right(2))
assert(Pointed[({ type F[A] = Either[String, A] })#F].point(1) === Right(1))
```

!SLIDE

# [Apply](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Apply)

## ap

### 持ち上げられた関数をコンテナに適用し、新しいコンテナを構築する

```scala
object vector {
  implicit object VectorInstance extends Apply[Vector] {
    def map[A, B](v: Vector[A])(f: A => B) = v map f
    def ap[A, B](va: => Vector[A])(vab: => Vector[A => B]) = vab flatMap (va map _)
  }
}

Option(0) <*> Option(Enum[Int].succ _) assert_=== Option(1)
List(1, 2, 3) <*> PlusEmpty[List].empty[Int => Int] assert_=== Nil
import vector._
Vector(1, 2) <*> Vector(Enum[Int].succ _, Enum[Int].pred _) assert_===  Vector(2, 3, 0, 1)
```

!SLIDE

# [Applicative](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Applicative)

## ApplyとPointedを組み合わせたもの

```scala
object vector {
  implicit object VectorInstance extends Applicative[Vector] {
    def point[A](a: => A) = Vector(a)
    def ap[A, B](va: => Vector[A])(vab: => Vector[A => B]) = vab flatMap (va map _)
  }
}
```

!SLIDE

# [ApplicativeLaw](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Applicative$ApplicativeLaw)

* ap(fa)(point((a: A) => a)) == fa
* ap(ap(fa)(fab))(fbc) == ap(fa)(ap(fab)(ap(fbc)(point((bc: B => C) => (ab: A => B) => bc compose ab))))
* ap(point(a))(point(ab)) == point(ab(a))
* ap(point(a))(fab) == ap(fab)(point((f: A => B) => f(a)))

```scala
val a = 0
val fa = Option(a)
lazy val fab: Option[Int => String] = Option(_.toString)
lazy val fbc: Option[String => Int] = Option(_.size)
fa <*> ((a: Int) => a).point[Option] assert_=== fa
fa <*> fab <*> fbc assert_=== fa <*> (fab <*> (fbc <*> (((bc: String => Int) => (ab: Int => String) => bc compose ab).point[Option])))
a.point[Option] <*> fab assert_=== fab <*> ((f: Int => String) => f(a)).point[Option]
```

!SLIDE

# Applicative Style

## ApplicativeBuilderを用いて計算を構築する

```scala
def append3[F[_]: Apply, A: Semigroup](fa: F[A], fb: F[A], fc: F[A]) =
  (fa |@| fb |@| fc)(_ |+| _ |+| _)
append3(Option(1), Option(2), Option(3)) assert_=== Option(6)
append3(Option(1), None, Option(3)) assert_=== None
append3(List(1), List(1, 2), List(1, 2, 3)) assert_=== List(3, 4, 5, 4, 5, 6)
```

!SLIDE

# 演習

* Map[String, String]から"id"と"pass"をキーとして値を取り出しUserを構築する

```scala
case class User(id: String, pass: String)
def user(m: Map[String, String]): Option[User]

user(Map("id" -> "halcat0x15a", "pass" -> "gesogeso")) assert_=== Some(User("halcat0x15a", "gesogeso"))
user(Map.empty) assert_=== None
```

!SLIDE

# [Bind](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Bind)

## bind

### 関数をコンテナに適用し、新しいコンテナを構築する

```scala
object vector {
  implicit object VectorInstance extends Bind[Vector] {
    def map[A, B](v: Vector[A])(f: A => B) = v map f
    def bind[A, B](v: Vector[A])(f: A => Vector[B]) = v flatMap f
  }
}

def append3[F[_]: Bind, A: Semigroup](fa: F[A], fb: F[A], fc: F[A]) =
  for {
    a <- fa
    b <- fb
    c <- fc
  } yield a |+| b |+| c
append3(Option(1), Option(2), Option(3)) assert_=== Option(6)
append3(Option(1), None, Option(3)) assert_=== None
import vector._
append3(Vector(1), Vector(1, 2), Vector(1, 2, 3)) assert_=== Vector(3, 4, 5, 4, 5, 6)
```

!SLIDE

# for式

## map, flatMap, filterに変換される

```scala
(for (a <- List(1, 2)) yield a + 1) assert_=== List(1, 2).map(a => a + 1)
(for (a <- Option(1); b <- Option(2)) yield a + b) assert_=== Option(1).flatMap(a => Option(2).map(b => a + b))
(for (a <- List(1, 2) if a % 2 == 0) yield a) assert_=== List(1, 2).filter(a => a % 2 == 0)
```

!SLIDE

# [Monad](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Monad)

## ApplicativeとBindを組み合わせたもの

```scala
object vector {
  implicit object VectorInstance extends Monad[Vector] {
    def point[A](a: => A) = Vector(a)
    def bind[A, B](v: Vector[A])(f: A => Vector[B]) = v flatMap f
  }
}
```

!SLIDE

# [MonadLaw](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Monad$MonadLaw)

* bind(fa)(point(_: A)) == fa
* bind(point(a))(f) == f(a)
* bind(bind(fa)(f))(g) == bind(fa)((a: A) => bind(f(a))(g))

```scala
import scala.util.control.Exception._
val a = 1
val fa = Option(a)
lazy val f: Int => Option[String] = _.toString |> Option.apply
lazy val g: String => Option[Int] = allCatch opt _.toInt
(fa >>= (_.point[Option])) assert_=== fa
(a.point[Option] >>= f) assert_=== f(a)
(fa >>= f >>= g) assert_=== (fa >>= (a => f(a) >>= g))
```

!SLIDE

# [ApplicativePlus](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.ApplicativePlus)

## ApplicativeとPlusEmptyを組み合わせたもの

!SLIDE

# [MonadPlus](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.MonadPlus)

## MonadとApplicativePlusを組み合わせたもの

### filterが定義される

```scala
object vector {
  implicit object VectorInstance extends MonadPlus[Vector] {
    def empty[A] = Vector.empty[A]
    def plus[A](v1: Vector[A], v2: => Vector[A]) = v1 ++ v2
    def point[A](a: => A) = Vector(a)
    def bind[A, B](v: Vector[A])(f: A => Vector[B]) = v flatMap f
  }
}

def evens[F[_]: MonadPlus](f: F[Int]) = f filter (_ % 2 === 0)
evens(List(1, 2, 3)) assert_=== List(2)
evens(Option(1)) assert_=== None
import vector._
evens(Vector(1, 2, 3)) assert_=== Vector(2)
```
