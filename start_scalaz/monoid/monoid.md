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
```

!SLIDE

# 恒等元の性質

## MonoidLaw

```scala
append(zero, a) == a
append(a, zero) == a
```

!SLIDE

# 主なメソッド、関数

* mzero
* multiply
* Monoid.replicate
* Monoid.unfold

```scala
mzero[Int] assert_=== 0
mzero[Option[String]] assert_=== None
3 multiply 5 assert_=== 15
"geso" multiply 2 assert_=== "gesogeso"
Monoid.replicate[List, Int](0)(3, 1 +) assert_=== List(0, 1, 2)
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
def zero[A: Group](a: A) = a |+| a.inverse
```

``scala
def zero[A: Group](a: A) = a |-| a
```

!SLIDE

# Plus, PlusEmpty

## 量化されたSemigroup, Monoid

### Plusは要素の性質に依存しない

!SLIDE

## <+>

```scala
List(1, 2) |+| List(3, 4) assert_=== List(1, 2, 3, 4)
List(1, 2) <+> List(3, 4) assert_=== List(1, 2, 3, 4)
Option(1) |+| Option(1) assert_=== Option(2)
Option(1) <+> Option(1) assert_=== Option(1)
```
