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

# Functor

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
fdouble(Vector(1.2, 2.1)) assert_=== Vector(2.4, 4.2
```

!SLIDE

# FunctorLaw

## mapの性質

* map(fa)(x => x) == fa
* map(map(fa)(f))(g) == map(fa)(g compose f)

```scala
Option(100).map(x => x) assert_=== Option(100)
List(1, 2, 3) map (
```

!SLIDE

# Pointed

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

# Apply

## ap

### 持ち上げられた関数をコンテナに適用し、新しいコンテナを構築する

```scala
object vector {
  implicit object VectorInstance extends Apply[Vector] {
    def map[A, B](v: Vector[A])(f: A => B) = v map f
    def ap[A, B](fa: => Vector[A])(f: => Vector[A => B]) = fa flatMap (a => f map (_(a)))
  }
}

Option(0) <*> Option(Enum[Int].succ _) assert_=== Option(1)
List(1, 2, 3) <*> PlusEmpty[List].empty[Int => Int] assert_=== Nil
import vector._
Vector(1, 2) <*> Vector(Enum[Int].succ _, Enum[Int].pred _) assert_=== Vector(2, 0, 3, 1)
```

!SLIDE

# Applicative

## ApplyとPointedを組み合わせたもの

```scala
object vector {
  implicit object VectorInstance extends Applicative[Vector] {
    def point[A](a: => A) = Vector(a)
    def ap[A, B](fa: => Vector[A])(f: => Vector[A => B]) = fa flatMap (a => f map (_(a)))
  }
}
```

!SLIDE

# ApplicativeLaw

```scala
ap(fa)(point((a: A) => a)) == fa
ap(ap(fa)(fab))(fbc) == ap(fa)(ap(fab)(ap(fbc)(point((bc: B => C) => (ab: A => B) => bc compose ab))))
ap(point(a))(point(ab)) == point(ab(a))
ap(point(a))(f) == ap(f)(point((f: A => B) => f(a)))
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

# Bind

## bind

### 関数をコンテナに適用し、新しいコンテナを構築する

```scala
def append3[F[_]: Bind, A: Semigroup](fa: F[A], fb: F[A], fc: F[A]) =
  for {
    a <- fa
    b <- fb
    c <- fc
  } yield a |+| b |+| c
append3(Option(1), Option(2), Option(3)) assert_=== Option(6)
append3(Option(1), None, Option(3)) assert_=== None
append3(List(1), List(1, 2), List(1, 2, 3)) assert_=== List(3, 4, 5, 4, 5, 6)
```

!SLIDE

# for式



!SLIDE

# Monad

## ApplicativeとBindを組み合わせたもの

!SLIDE

# MonadLaw
