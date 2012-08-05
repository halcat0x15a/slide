!SLIDE

# Type Class

!SLIDE

# Scalazの例

## ある値を２倍する

```scala
def double[A](a: A)(implicit s: Semigroup[A]) = s.append(a, a)

double(2) assert_=== 4
double("2") assert_=== "22"
```

!SLIDE

# [Semigroup](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Semigroup)

## appendは抽象メソッド、２つの値を結合する関数

### Pointを例にインスタンスを定義する

```scala
case class Point(x: Int, y: Int) {
  def +(p: Point) = Point(x + p.x, y + p.y)
}

object Point {
  implicit object PointInstance extends Semigroup[Point] {
    def append(p1: Point, p2: => Point) = p1 + p2
  }
}
```

!SLIDE

### Semigroup[Point]のインスタンスが暗黙的に渡される

```scala
assert(double(Point(1, 2)) == Point(2, 4))
```

!SLIDE

# implicit parameter

## スコープ内のimplicit valueだけでなく、コンパニオンオブジェクトに定義されたimplicit valueも探索される

!SLIDE

# Law

## Lawは型クラス内にtraitとして定義されてる

### appendの定義はSemigroupLawを満たしていなければならない

* append(f1, append(f2, f3)) == append(append(f1, f2), f3)

!SLIDE

# [Show](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Show)

## 文字列へ変換する関数を定義する

### インスタンスを複数定義する時はmix-inや、複数のimplicit valueを定義する

```scala
object Point {
  implicit object PointInstance extends Show[Point] with Semigroup[Point] {
    def show(p: Point) = p.toString.toList
    def append(p1: Point, p2: => Point) = p1 + p2
  }
}
```

!SLIDE

# 演習

* Vectorに対するShowのインスタンス
* 以下のクラスに対するShowとSemigroupのインスタンス

```scala
case class Rational(n: Int, d: Int) {
  def +(r: Rational) = Rational(n * r.d + r.n * d, d * r.d)
  override def toString = s"$n/$d"
}

object vector {
  def VectorShow[A]: Show[Vector[A]]
}

import vector._
assert(implicitly[Show[Vector[Int]]].shows(Vector(1)) == "Vector(1)")
assert(implicitly[Show[Vector[String]]].shows(Vector("geso")) == "Vector(geso)")
assert(implicitly[Show[Rational]].shows(Rational(1, 2)) == "1/2")
assert(implicitly[Semigroup[Rational]].append(Rational(1, 2), Rational(1, 2)) == Rational(4, 4))
```

!SLIDE

# Context Bound

## 型クラスを利用するとき、明示的にimplicit parameterを書かないことが多い

### 次の関数は同じ動作をする

```scala
def quote[A](a: A)(implicit s: Show[A]) = s.show(a).mkString("'", "", "'")
def quote[A: Show](a: A) = implicitly[Show[A]].show(a).mkString("'", "", "'")

quote("geso") assert_=== "'geso'"
quote(List(1, 2, 3)) assert_=== "'[1,2,3]'"
```
