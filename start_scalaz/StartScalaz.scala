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
    def append(p1: Point, p2: => Point) = p1 + p2
    def equal(p1: Point, p2: Point) = p1 == p2
  }
}

case class Rational(n: Int, d: Int) {
  def +(r: Rational) = Rational(n * r.d + r.n * d, d * r.d)
}

object Rational {
  implicit object RationalInstance extends Equal[Rational] with Monoid[Rational] {
    def zero = Rational(0, 1)
    def append(r1: Rational, r2: => Rational) = r1 + r2
    def equal(r1: Rational, r2: Rational) = r1 == r2
  }
}

object StartScalaz extends App {
  def double[A: Semigroup](a: A) = a |+| a
  double(2) assert_=== 4
  double("2") assert_=== "22"
  import Point._
  double(Point(1, 2))

  def encode(n: Int) = Monoid.unfold[List, Int, Byte](n) {
    case 0 => None
    case i => Some((i % 2 toByte) -> i / 2)
  } reverse

  def encode[F[_]](n: Int)(implicit F: Pointed[F], G: Monoid[F[Byte]], H: Traverse[F]) = Monoid.unfold[F, Int, Byte](n) {
    case 0 => None
    case i => Some((i % 2 toByte) -> i / 2)
  } reverse

//  def innerProduct = 

//  def exterior

}
