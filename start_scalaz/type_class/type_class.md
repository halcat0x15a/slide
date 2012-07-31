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

# Semigroup

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
import Point._
double(Point(1, 2))
```

!SLIDE

# Law

## Lawは型クラス内にtraitとして定義されてる

### appendの定義はSemigroupLawを満たしていなければならない

```scala
append(f1, append(f2, f3)) == append(append(f1, f2), f3)
```

!SLIDE

# Show

## 文字列へ変換する関数を定義する

### 複数のインスタンスはmix-inや、別のimplicit valueを定義することで実現する

```scala
object Point {
  implicit object PointInstance extends Show[Point] with Semigroup[Point] {
    def show(p: Point) = p.toString.toList
    def append(p1: Point, p2: => Point) = p1 + p2
  }
}
```

!SLIDE

# Context Bound

## 型クラスを利用するとき、明示的にimplicit parameterを書かないことが多い

### 次の関数は同じ動作をする

```scala
def quote[A](a: A)(implicit s: Show[A]) = s.show(a).mkString("'", "", "'")
def quote[A: Show](a: A) = implicitly[Show[A]].show(a).mkString("'", "", "'")
```
