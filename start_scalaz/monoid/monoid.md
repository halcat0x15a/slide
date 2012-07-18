!SLIDE

# Monoid

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
```

!SLIDE

# 恒等元の性質

## MonoidLaw

```scala
append(zero, a) == a
append(a, zero) == a
```

!SLIDE

```scala
mzero[String] assert_=== ""
mzero[Option[Int]] assert_=== None
2.5 multiply 4 assert_=== 10.0
import Rational._
mzero[Rational]
Rational(1, 2) |+| Rational(3, 4)
```

!SLIDE

# Plus, PlusEmpty

## 量化されたSemigroup, Monoid

### Plusは要素の性質を無視する

```scala
List(1, 2) |+| List(3, 4) assert_=== List(1, 2, 3, 4)
List(1, 2) <+> List(3, 4) assert_=== List(1, 2, 3, 4)
Option(1) |+| Option(1) assert_=== Option(2)
Option(1) <+> Option(1) assert_=== Option(1)
```
