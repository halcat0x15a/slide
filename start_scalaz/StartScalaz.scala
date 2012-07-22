import scala.language.postfixOps
import scala.language.higherKinds

import scalaz._
import Scalaz._

case class Point(x: Int, y: Int) {
  def +(p: Point) = Point(x + p.x, y + p.y)
  override def toString = s"($x, $y)"
}

object Point {
  val x: Lens[Point, Int] = Lens.lensu[Point, Int]((p, x) => p.copy(x = x), _.x)
  val y: Lens[Point, Int] = Lens.lensu[Point, Int]((p, y) => p.copy(y = y), _.y)
  implicit object PointInstance extends Show[Point] with Equal[Point] with Semigroup[Point] {
    def show(p: Point) = p.toString.toList
    def equal(p1: Point, p2: Point) = p1 == p2
    def append(p1: Point, p2: => Point) = p1 + p2
  }
}

case class Rational(n: Int, d: Int) {
  def +(r: Rational) = Rational(n * r.d + r.n * d, d * r.d)
  override def toString = s"$n/$d"
}

object Rational {
  implicit object RationalInstance extends Equal[Rational] with Show[Rational] with Monoid[Rational] {
    def show(r: Rational) = r.toString.toList
    def equal(r1: Rational, r2: Rational) = r1 == r2
    def zero = Rational(0, 1)
    def append(r1: Rational, r2: => Rational) = r1 + r2
  }
}

object StartScalaz extends App {
  def double[A: Semigroup](a: A) = a |+| a
  double(2) assert_=== 4
  double("2") assert_=== "22"
  import Point._
  double(Point(1, 2))

  locally {
    def double[A: Semigroup](a: A) = Semigroup[A].append(a, a)
  }

  locally {
    def double[A: Semigroup](a: A) = ToSemigroupOps(a) |+| a
  }

  def quote[A: Show](a: A) = a.show.mkString("'", "", "'")

  locally {
    def quote[A: Show](a: A) = Show[A].show(a).mkString("'", "", "'")
  }

  mzero[Int] assert_=== 0
  mzero[Option[String]] assert_=== None
  3 multiply 5 assert_=== 15
  "geso" multiply 2 assert_=== "gesogeso"
  Monoid.replicate[List, Int](0)(3, 1 +) assert_=== List(0, 1, 2)
  Monoid.unfold[List, List[Int], Int](List(1, 2, 3)) {
    case Nil => None
    case x :: xs => Some(x * 2 -> xs)
  } assert_=== List(2, 4, 6)

  lazy val encode: Int => List[Int] = {
    case 0 => Nil
    case i => i % 2 :: encode(i / 2)
  }

  def mencode(n: Int) = Monoid.unfold[List, Int, Int](n) {
    case 0 => None
    case i => Some(i % 2 -> i / 2)
  }

  lazy val evens = Stream.from(0).filter(_ % 2 == 0).take _

  def mevens(n: Int) = Monoid.replicate[List, Int](0)(n, 2 +)

  mevens(5) assert_=== List(0, 2, 4, 6, 8)
  mencode(13) assert_=== List(1, 0, 1, 1)

  locally {
    def zero[A: Group](a: A) = a |+| a.inverse
  }

  def zero[A: Group](a: A) = a |-| a

}
