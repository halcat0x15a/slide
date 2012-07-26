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
  implicit object RationalInstance extends Order[Rational] with Show[Rational] with Monoid[Rational] {
    def show(r: Rational) = r.toString.toList
    def zero = Rational(0, 1)
    def append(r1: Rational, r2: => Rational) = r1 + r2
    def order(r1: Rational, r2: Rational) = r1.n * r2.d -> r2.n * r1.d match {
      case (m, n) if m == n => Ordering.EQ
      case (m, n) if m < n => Ordering.LT
      case (m, n) if m > n => Ordering.GT
    }
  }
}

case class Person(name: String, age: Int, height: Int)

object Person {
  implicit object PersonInstance extends Show[Person] with Order[Person] {
    def show(p: Person) = p.toString.toList
    def order(p1: Person, p2: Person) =
      p1.age ?|? p2.age |+| p1.height ?|? p2.height
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
    def quote[A](a: A)(implicit s: Show[A]) = s.show(a).mkString("'", "", "'")
  }
  locally {
    def quote[A: Show](a: A) = implicitly[Show[A]].show(a).mkString("'", "", "'")
  }
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

  import Point._
  assert(Point(2, 3) === Point(2, 3))
  assert(Point(2, 3) =/= Point(3, 5))

  import Rational._
  assert(Rational(1, 2) === Rational(1, 2))
  assert(Rational(1, 2) < Rational(3, 4))
  assert(Rational(5, 2) >= Rational(5, 3))
  Rational(1, 2) ?|? Rational(1, 2) assert_=== Ordering.EQ

  mzero[Ordering] assert_=== Ordering.EQ
  (Ordering.EQ: Ordering) |+| Ordering.LT assert_=== Ordering.LT
  (Ordering.GT: Ordering) |+| Ordering.LT assert_=== Ordering.GT
  (Ordering.GT: Ordering) |+| Ordering.EQ assert_=== Ordering.GT

  val miku = Person("miku", 16, 158)
  val rin = Person("rin", 14, 152)
  val len = Person("len", 14, 156)
  import Person._
  List(miku, rin, len) sorted PersonInstance.toScalaOrdering assert_=== List(rin, len, miku)

  2.succ assert_=== 3
  'b'.pred assert_=== 'a'
  'a' -+- 2 assert_=== 'c'
  1 --- 3 assert_=== -2
  1 |-> 3 assert_=== List(1, 2, 3)
  'a' |--> (2, 'f') assert_=== List('a', 'c', 'e')

  locally {
    import java.util.Date
    object DateOrder extends Order[Date] {
      def order(x: Date, y: Date) = x -> y match {
	case (x, y) if x == y => EQ
	case (x, y) if x before y => LT
	case (x, y) if x after y => GT
      }
    }
    lazy val dateOrder = Order.order[Date]((x, y) => Ordering.fromInt(x compareTo y))
    case class Student(name: String, grade: Int, birthday: Date)
    implicit object StudentOrder extends Show[Student] with Order[Student] {
      def show(s: Student) = s.toString.toList
      def order(x: Student, y: Student) = x.grade ?|? y.grade |+| x.birthday ?|? y.birthday
    }
    val format = new java.text.SimpleDateFormat("MM dd")
    val akari = Student("akari", 1, format.parse("07 24"))
    val kyoko = Student("kyoko", 2, format.parse("03 28"))
    val yui = Student("yui", 2, format.parse("04 22"))
    val chinatsu = Student("chinatsu", 1, format.parse("11 06"))
    List(akari, kyoko, yui, chinatsu).sorted(StudentOrder.toScalaOrdering) assert_=== List(akari, chinatsu, kyoko, yui)
  }

  def triple[F[_]: Plus, A](fa: F[A]) = fa <+> fa <+> fa
  triple(Option(1)) assert_=== Option(1)
  triple(List(1)) assert_=== List(1, 1, 1)

  def appendAll[A: Semigroup, F[_]: Functor](fa: F[A], a: A) = fa.map(_ |+| a)
  appendAll(List(1, 2, 3), 1) assert_=== List(2, 3, 4)
  appendAll(1.some, 4) assert_=== Some(5)

  Pointed[List].point(1) assert_=== List(1)
  Pointed[Option].point(1) assert_=== Some(1)

  assert(Functor[({ type F[A] = Either[String, A] })#F].map(Right(1))(_.succ) === Right(2))
  assert(Pointed[({ type F[A] = Either[String, A] })#F].point(1) === Right(1))

  Option(0) <*> Option(Enum[Int].succ _) assert_=== Option(1)
  List(1, 2, 3) <*> List(Enum[Int].pred _) assert_=== List(0, 1, 2)

  def inverseAll[F[_]: Applicative, A: Group](fa: F[A]) = fa <*> Pointed[F].point(Group[A].inverse _)
  inverseAll(Option(1)) assert_=== Option(-1)
  inverseAll(List(1, 2, 3)) assert_=== List(-1, -2, -3)

  def append3[F[_]: Applicative, A: Semigroup](a: F[A], b: F[A], c: F[A]) = (a |@| b |@| c)(_ |+| _ |+| _)
  append3(Option(1), Option(2), Option(3)) assert_=== Option(6)
  append3(Option(1), None, Option(3)) assert_=== None
  append3(List(1), List(1, 2), List(1, 2, 3)) assert_=== List(3, 4, 5, 4, 5, 6)
}
