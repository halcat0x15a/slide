!SLIDE

# 前回の復習

!SLIDE

# Type Class

* [Context Bound](http://halcat0x15a.github.com/slide/start_scalaz/out/#14)
* [implicit parameterによるインスタンスの供給](http://halcat0x15a.github.com/slide/start_scalaz/out/#7)
* [implicit conversionによるシンタックスの供給](http://halcat0x15a.github.com/slide/start_scalaz/out/#21)

```scala
def double[A: Semigroup](a: A) = a |+| a
```

!SLIDE

# 重要な型クラスと関数

!SLIDE

## 表明

### よく例に使用する

```scala
[A: Show: Equal](a: A, b: A): Unit = a assert_=== b
```

!SLIDE

## 文字列への変換

```scala
[A: Show](a: A): String = a.shows
```

!SLIDE

## 比較

```scala
[A: Equal](a: A, b: A): Boolean = a === b
[A: Equal](a: A, b: A): Boolean = a =/= b
```

!SLIDE

## 結合演算

```scala
[A: Semigroup](a: A, b: A): A = a |+| b
```

!SLIDE

## 単位元

```scala
[A: Monoid]: A = mzero[A]
```

!SLIDE

## map

```scala
[F[_]: Functor, A, B](fa: F[A])(f: A => B): F[B] = fa map f
```

!SLIDE

## [Applicative Style](http://halcat0x15a.github.com/slide/start_scalaz/out/#58)

```scala
[F[_]: Applicative, A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = (fa |@| fb)(f)
```

!SLIDE

## flatMap

```scala
[F[_]: Monad, A, B](fa: F[A])(f: A => F[B]): F[B] = fa >>= f
```

!SLIDE

## 関数を適用する

```scala
[A, B](a: A)(f: A => B): B = a |> f
```

!SLIDE

## ガード

```scala
[A](b: Boolean)(a: A): Option[A] = b option a
```

!SLIDE

# パッケージと命名規則

[前回資料参照](http://halcat0x15a.github.com/slide/start_scalaz/out/#16)
