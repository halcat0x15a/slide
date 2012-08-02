!SLIDE

# Monoid

!SLIDE

## Semigroupに恒等元を加えたもの

### Rationalの例

```scala
case class Rational(n: Int, d: Int) {
  def +(r: Rational) = Rational(n * r.d + r.n * d, d * r.d)
}

object Rational {
  implicit object RationalInstance extends Monoid[Rational] {
    def zero = Rational(0, 1)
    def append(r1: Rational, r2: => Rational) = r1 + r2
  }
}

mzero[Int] assert_=== 0
mzero[Option[String]] assert_=== None
mzero[Rational] assert_=== Rational(0, 1)
```

!SLIDE

# MonoidLaw

## 恒等元の性質

* append(zero, a) == a
* append(a, zero) == a

```scala
val a = 1
mzero[Int] |+| a assert_=== a
a |+| mzero[String] assert_=== a
```

!SLIDE

# multiply

## 任意の回数結合する

```scala
3 multiply 5 assert_=== 15
"geso" multiply 2 assert_=== "gesogeso"
Rational(1, 2) multiply 3 assert_=== Rational(1, 8)
```

!SLIDE

# Monoid.replicate

## 任意の回数繰り返し関数を適用し、結果を集める

```scala
Monoid.replicate[List, Int](0)(3, 1 +) assert_=== List(0, 1, 2)
```

!SLIDE

# Monoid.unfold

## Noneを返すまで繰り返し関数を適用し、結果を集める

```scala
Monoid.unfold[List, List[Int], Int](List(1, 2, 3)) {
  case Nil => None
  case x :: xs => Some(x * 2 -> xs)
} assert_=== List(2, 4, 6)
```

!SLIDE

# 問題

* replicateを用いて偶数列からn個取得する関数evens
* unfoldを用いて10進数から2進数へ変換する関数encode

```scala
def evens(n: Int): List[Int]
def encode(n: Int): List[Int]
evens(5) assert_=== List(0, 2, 4, 6, 8)
encode(13) assert_=== List(1, 0, 1, 1)
```

!SLIDE

# Group

## 逆元を持つMonoid

```scala
object Rational {
  implicit object RationalInstance extends Order[Rational] with Show[Rational] with Group[Rational] {
    def zero = Rational(0, 1)
    def append(r1: Rational, r2: => Rational) = r1 + r2
    def inverse(r: Rational) = Rational(-r.n, r.d)
  }
}

1.inverse assert_=== -1
import Rational._
Rational(1, 2).inverse assert_=== Rational(-1, 2)
```

!SLIDE

# |-|

## 逆元と結合する

```scala
1 |-| 1 assert_=== 0
1.2 |-| 2.1 assert_=== -0.9000000000000001
```

!SLIDE

# Plus, PlusEmpty

## 量化されたSemigroup, Monoid

### Plusは要素の性質に依存しない

```scala
List(1, 2) |+| List(3, 4) assert_=== List(1, 2, 3, 4)
List(1, 2) <+> List(3, 4) assert_=== List(1, 2, 3, 4)
Option(1) |+| Option(1) assert_=== Option(2)
Option(1) <+> Option(1) assert_=== Option(1)

object vector {
  implicit object VectorInstance extends PlusEmpty[Vector] {
    def empty[A] = Vector.empty[A]
    def plus[A](v1: Vector[A], v2: => Vector[A]) = v1 ++ v2
  }
}
import vector._
assert(Vector(1, 2) <+> Vector(3, 4) == Vector(1, 2, 3, 4))
```
