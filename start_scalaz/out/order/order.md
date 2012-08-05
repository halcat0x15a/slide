!SLIDE

# Order

!SLIDE

# [Equal](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Equal)

## 等価性

```scala
object Point {
  implicit object PointInstance extends Equal[Point] {
    def equal(p1: Point, p2: Point) = p1 == p2
  }
}

assert(Point(2, 3) === Point(2, 3))
assert(Point(2, 3) =/= Point(3, 5))
```

!SLIDE

# 型安全な演算

## Scalazの関数はジェネリクスを利用している

```scala
1 == "geso"
/* 1 === "geso" */ // compile error

1 + 1.5
/* 1 |+| 1.5 */ // compile error
```

!SLIDE

# [Order](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Order)

## 順序

```scala
object Rational {
  implicit object RationalInstance extends Order[Rational] {
    def order(r1: Rational, r2: Rational) = r1.n * r2.d -> r2.n * r1.d match {
      case (m, n) if m == n => Ordering.EQ
      case (m, n) if m < n => Ordering.LT
      case (m, n) if m > n => Ordering.GT
    }
  }
}

assert(Rational(1, 2) === Rational(1, 2))
assert(Rational(1, 2) < Rational(3, 4))
assert(Rational(5, 2) >= Rational(5, 3))
```

!SLIDE

# [Ordering](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Ordering)

## Javaのcompareが返す-1、0、1に対応する

### Orderingはモノイドであるため、複数の比較結果を結合することができる

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
List(miku, rin, len) sorted Order[Person].toScalaOrdering assert_=== List(rin, len, miku)
```

!SLIDE

# 演習

* java.util.Dateに対するOrderのインスタンス
* 以下のクラスに対するOrderのインスタンス
    * gradeとbirthdayを用いる
    * ただしgrade重きを置く

```scala
case class Student(name: String, grade: Int, birthday: Date)

val format = new java.text.SimpleDateFormat("yyyy MM dd")
val akari = Student("akari", 1, format.parse("1995 07 24"))
val kyoko = Student("kyoko", 2, format.parse("1995 03 28"))
val yui = Student("yui", 2, format.parse("1994 04 22"))
val chinatsu = Student("chinatsu", 1, format.parse("1995 11 06"))
List(akari, kyoko, yui, chinatsu) sorted Order[Student].toScalaOrdering assert_=== List(akari, chinatsu, yui, kyoko)
```

!SLIDE

# [Enum](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Enum)

## Orderにsuccessorとpredecessorを加えたもの

```scala
object Rational {
  implicit object RationalInstance extends Enum[Rational] {
    def order(r1: Rational, r2: Rational) = r1.n * r2.d -> r2.n * r1.d match {
      case (m, n) if m == n => Ordering.EQ
      case (m, n) if m < n => Ordering.LT
      case (m, n) if m > n => Ordering.GT
    }
    def succ(r: Rational) = r.copy(n = r.n + r.d)
    def pred(r: Rational) = r.copy(n = r.n - r.d)
  }
}

1.succ assert_=== 2
Rational(1, 2).succ assert_=== Rational(3, 2)
'b'.pred assert_=== 'a'
Rational(1, 2).pred assert_=== Rational(-1, 2)
```
