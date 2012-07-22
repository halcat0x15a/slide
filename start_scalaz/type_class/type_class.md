!SLIDE

# Type Class

!SLIDE

# Scalazの例

## ある値を２倍する

```scala
import scalaz, Scalaz

def double[A](a: A)(implicit s: Semigroup[A]) = s.append(a, a)

double(2) assert_=== 4
double("2") assert_=== "22"
```

!SLIDE

# Semigroup

## appendは抽象メソッド

### Pointを例にインスタンスを定義してみる

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

## Lowは型クラス内にtraitとして定義されてる

### appendの定義はSemigroupLawを満たしていなければならない

```scala
append(f1, append(f2, f3)) == append(append(f1, f2), f3)
```
