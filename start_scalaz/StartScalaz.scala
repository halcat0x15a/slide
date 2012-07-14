import scalaz._
import Scalaz._

case class Point(x: Int, y: Int) {
  def +(p: Point) = Point(x + p.x, y + p.y)
}

object Point {
  implicit object PointInstance extends Semigroup[Point] {
    def append(p1: Point, p2: => Point) = p1 + p2
  }
}

object Double {
  def double[A: Semigroup](a: A) = a |+| a
}

case class Rational(n: Int, d: Int) {
  def +(r: Rational) = Rational(n * r.d + r.n * d, d * r.d)
}

object Rational {
  implicit object RationalInstance extends Monoid[Rational] {
    def zero = Rational(0, 1)
    def append(r1: Rational, r2: => Rational) = r1 + r2
  }
}

object StartScalaz extends App {
  def double[A](a: A)(implicit s: Semigroup[A]) = s.append(a, a)
  double(2) assert_=== 4
  double("2") assert_=== "22"
  import Point._
  double(Point(1, 2))

  mzero[String] assert_=== ""
  mzero[Option[Int]] assert_=== None
  2.5 multiply 4 assert_=== 10.0
  import Rational._
  Rational(1, 2) |+| Rational(3, 4)
  mzero[Rational]
}
