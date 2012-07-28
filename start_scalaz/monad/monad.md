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
def appendAll[F[_]: Functor, A: Semigroup](fa: F[A], a: A) = fa.map(_ |+| a)
appendAll(List(1, 2, 3), 1) assert_=== List(2, 3, 4)
appendAll(1.some, 4) assert_=== Some(5)
```

!SLIDE

# FunctorLaw

## mapの性質

```scala
map(fa)(x => x) == fa
map(map(fa)(f))(g) == map(fa)(g compose f)
```

!SLIDE

# Pointed

## コンテナを構築する

```scala
Pointed[List].point(1) assert_=== List(1)
Pointed[Option].point(1) assert_=== Some(1)
```

!SLIDE

# 型の部分適用

```scala
assert(Functor[({ type F[A] = Either[String, A] })#F].map(Right(1))(_.succ) === Right(2))
assert(Pointed[({ type F[A] = Either[String, A] })#F].point(1) === Right(1))
```

!SLIDE

# Apply

## 持ち上げられた関数をコンテナに適用し、新しいコンテナを構築する

```scala
Option(0) <*> Option(Enum[Int].succ _) assert_=== Option(1)
List(1, 2, 3) <*> List(Enum[Int].pred _) assert_=== List(0, 1, 2)
```

!SLIDE

# Applicative

## ApplyとPointedを組み合わせたもの

### mapはap(<*>)とpointによって実装される

```scala
def inverseAll[F[_]: Applicative, A: Group](fa: F[A]) = fa <*> Pointed[F].point(Group[A].inverse _)
inverseAll(Option(1)) assert_=== Option(-1)
inverseAll(List(1, 2, 3)) assert_=== List(-1, -2, -3)
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
def append3[F[_]: Applicative, A: Semigroup](a: F[A], b: F[A], c: F[A]) = (a |@| b |@| c)(_ |+| _ |+| _)
append3(Option(1), Option(2), Option(3)) assert_=== Option(6)
append3(Option(1), None, Option(3)) assert_=== None
append3(List(1), List(1, 2), List(1, 2, 3)) assert_=== List(3, 4, 5, 4, 5, 6)
```

!SLIDE

# Bind

## 関数をコンテナに適用し、新しいコンテナを構築する

### apはbindとmapによって実装される

!SLIDE

# Monad
