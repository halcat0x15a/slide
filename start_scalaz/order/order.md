!SLIDE

# Order

!SLIDE

# Equal

## 等価性

```scala
case class Point(x: Int, y: Int)

object Point {
  implicit object PointInstance extends Equal[Point] {
    def equal(p1: Point, p2: Point) = p1 == p2
  }
}

import Point._
assert(Point(2, 3) === Point(2, 3))
assert(Point(2, 3) =/= Point(3, 5))
```

!SLIDE

# 型安全な演算

## Scalazの関数はジェネリクスを利用している

```scala
1 == "geso"
// compile error
// 1 === "geso"

1 + 1.5
// compile error
// 1 |+| 1.5
```

!SLIDE

# Order

## 順序

```scala
case class Rational(n: Int, d: Int)

object Rational {
  implicit object RationalInstance extends Order[Rational] {
    def order(r1: Rational, r2: Rational) = r1.n * r2.d -> r2.n * r1.d match {
      case (m, n) if m == n => Ordering.EQ
      case (m, n) if m < n => Ordering.LT
      case (m, n) if m > n => Ordering.GT
    }
  }
}

import Rational._
assert(Rational(1, 2) === Rational(1, 2))
assert(Rational(1, 2) < Rational(3, 4))
assert(Rational(5, 2) >= Rational(5, 3))
```

!SLIDE

# Ordering

## Javaのcompareが返す-1、0、1に対応します。

### Orderingはモノイドであるため、複数の比較結果を結合することができます。

```scala
mzero[Ordering] assert_=== Ordering.EQ
(Ordering.EQ: Ordering) |+| Ordering.LT assert_=== Ordering.LT
(Ordering.GT: Ordering) |+| Ordering.LT assert_=== Ordering.GT
(Ordering.GT: Ordering) |+| Ordering.EQ assert_=== Ordering.GT
```

!SLIDE

## Orderingを利用したsort

```scala
case class Person(name: String, age: Int, height: Int)

object Person {
  implicit object PersonInstance extends Show[Person] with Order[Person] {
    def show(p: Person) = p.toString.toList
    def order(p1: Person, p2: Person) =
      p1.age ?|? p2.age |+| p1.height ?|? p2.height
  }
}

val miku = Person("miku", 16, 158)
val rin = Person("rin", 14, 152)
val len = Person("len", 14, 156)
import Person._
List(miku, rin, len) sorted PersonInstance.toScalaOrdering assert_=== List(rin, len, miku)
```

!SLIDE

# 問題

* java.util.Dateに対するOrderのインスタンス
* 以下のクラスに対するOrderのインスタンス
    * gradeとbirthdayを用いる
    * ただしgrade重きを置く

```scala
case class Student(name: String, grade: Int, birthday: Date)

val format = new java.text.SimpleDateFormat("MM dd")
val akari = Student("akari", 1, format.parse("07 24"))
val kyoko = Student("kyoko", 2, format.parse("03 28"))
val yui = Student("yui", 2, format.parse("04 22"))
val chinatsu = Student("chinatsu", 1, format.parse("11 06"))
List(akari, kyoko, yui, chinatsu).sorted(StudentOrder.toScalaOrdering) assert_=== List(akari, chinatsu, kyoko, yui)
```

!SLIDE

# Enum

## Orderにsuccessorとpredecessorを加えたもの

```scala
2.succ assert_=== 3
'b'.pred assert_=== 'a'
'a' -+- 2 assert_=== 'c'
1 --- 3 assert_=== -2
1 |-> 3 assert_=== List(1, 2, 3)
'a' |--> (2, 'f') assert_=== List('a', 'c', 'e')
```
