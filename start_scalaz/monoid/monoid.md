!SLIDE

# Semigroup

## 問題

### ある値をn回結合する関数timesを定義せよ（n > 0）

```scala
times(3, 3) assert_=== 9
times("x", 5) assert_=== "xxxxx"
```

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

```scala
mzero[String] assert_=== ""
mzero[Option[Int]] assert_=== None
2.5 multiply 4 assert_=== 10.0
import Rational._
mzero[Rational]
Rational(1, 2) |+| Rational(3, 4)
```
