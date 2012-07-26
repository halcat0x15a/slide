!SLIDE

# Monad

!SLIDE

# Higher Kinds

## 型をパラメータとしてとる型

```scala
def addAll[F[Int] <: Seq[Int]](fi: F[Int], i: Int) = fi.map(1 +)
addAll[List](List(1, 2, 3), 1)
addAll[Vector](Vector(4, 5, 6), 2)
```

!SLIDE

# Functor

## map

### 要素に関数を適用する

```scala
def addAll[F[_]: Functor, A: Semigroup](fa: F[A], a: A) = fa.map(_ |+| a)
addAll(List(1, 2, 3), 1) assert_=== List(2, 3, 4)
addAll(1.some, 4) assert_=== Some(5)
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

# Apply

!SLIDE

# Applicative

!SLIDE

# Bind

!SLIDE

# Monad
