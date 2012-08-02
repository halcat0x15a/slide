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
  def unary_- = copy(n = -n)
  override def toString = s"$n/$d"
}

object Rational {
  implicit object RationalInstance extends Enum[Rational] with Show[Rational] with Group[Rational] {
    def show(r: Rational) = r.toString.toList
    def zero = Rational(0, 1)
    def append(r1: Rational, r2: => Rational) = r1 + r2
    def inverse(r: Rational) = -r
    def order(r1: Rational, r2: Rational) = r1.n * r2.d -> r2.n * r1.d match {
      case (m, n) if m == n => Ordering.EQ
      case (m, n) if m < n => Ordering.LT
      case (m, n) if m > n => Ordering.GT
    }
    def succ(r: Rational) = r.copy(n = r.n + r.d)
    def pred(r: Rational) = r.copy(n = r.n - r.d)
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

object vector {
  implicit def VectorShow[A] = Show.showA[Vector[A]]
  implicit def VectorEqual[A] = Equal.equalA[Vector[A]]
  implicit object VectorInstance extends MonadPlus[Vector] {
    def empty[A] = Vector.empty[A]
    def plus[A](v1: Vector[A], v2: => Vector[A]) = v1 ++ v2
    def point[A](a: => A) = Vector(a)
    def bind[A, B](v: Vector[A])(f: A => Vector[B]) = v flatMap f
  }
}

case class User(id: String, pass: String)

object User {
  implicit object UserInstance extends Show[User] with Equal[User] {
    def show(u: User) = u.toString.toList
    def equal(u1: User, u2: User) = u1 == u2
  }
}

object StartScalaz extends App {
  def double[A: Semigroup](a: A) = a |+| a
  double(2) assert_=== 4
  double("2") assert_=== "22"
  assert(double(Point(1, 2)) == Point(2, 4))

  import vector._
  assert(implicitly[Show[Vector[Int]]].shows(Vector(1)) == "Vector(1)")
  assert(implicitly[Show[Vector[String]]].shows(Vector("geso")) == "Vector(geso)")
  assert(implicitly[Show[Rational]].shows(Rational(1, 2)) == "1/2")
  assert(implicitly[Semigroup[Rational]].append(Rational(1, 2), Rational(1, 2)) == Rational(4, 4))

  locally {
    val f1 = "foo"
    val f2 = "bar"
    val f3 = "baz"
    f1 |+| (f2 |+| f3) assert_=== f1 |+| f2 |+| f3
  }

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
  mzero[Rational] assert_=== Rational(0, 1)

  locally {
    val a = 1
    mzero[Int] |+| a assert_=== a
    a |+| mzero[Int] assert_=== a
  }

  3 multiply 5 assert_=== 15
  "geso" multiply 2 assert_=== "gesogeso"
  Rational(1, 2) multiply 3 assert_=== Rational(12, 8)

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

  1 |-| 1 assert_=== 0
  1.2 |-| 2.1 assert_=== -0.9000000000000001

  List(1, 2) |+| List(3, 4) assert_=== List(1, 2, 3, 4)
  List(1, 2) <+> List(3, 4) assert_=== List(1, 2, 3, 4)
  Option(1) |+| Option(1) assert_=== Option(2)
  Option(1) <+> Option(1) assert_=== Option(1)

  import vector._
  Vector(1, 2) <+> Vector(3, 4) assert_=== Vector(1, 2, 3, 4)

  assert(Point(2, 3) === Point(2, 3))
  assert(Point(2, 3) =/= Point(3, 5))

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
  List(miku, rin, len) sorted Order[Person].toScalaOrdering assert_=== List(rin, len, miku)

  2.succ assert_=== 3
  Rational(1, 2).succ assert_=== Rational(3, 2)
  'b'.pred assert_=== 'a'
  Rational(1, 2).pred assert_=== Rational(-1, 2)

  'a' -+- 2 assert_=== 'c'
  1 --- 3 assert_=== -2

  1 |-> 3 assert_=== List(1, 2, 3)
  'a' |--> (2, 'f') assert_=== List('a', 'c', 'e')

  locally {
    import java.util.Date
    object DateOrder extends Order[Date] {
      def order(x: Date, y: Date) = x -> y match {
	case (x, y) if x == y => Ordering.EQ
	case (x, y) if x before y => Ordering.LT
	case (x, y) if x after y => Ordering.GT
      }
    }
    lazy val dateOrder = Order.order[Date]((x, y) => Ordering.fromInt(x compareTo y))
    case class Student(name: String, grade: Int, birthday: Date)
    implicit object StudentOrder extends Show[Student] with Order[Student] {
      def show(s: Student) = s.toString.toList
      def order(x: Student, y: Student) = x.grade ?|? y.grade |+| x.birthday ?|? y.birthday
    }
    val format = new java.text.SimpleDateFormat("yyyy MM dd")
    val akari = Student("akari", 1, format.parse("1995 07 24"))
    val kyoko = Student("kyoko", 2, format.parse("1997 03 28"))
    val yui = Student("yui", 2, format.parse("1996 04 22"))
    val chinatsu = Student("chinatsu", 1, format.parse("1995 11 06"))
    List(akari, kyoko, yui, chinatsu).sorted(StudentOrder.toScalaOrdering) assert_=== List(akari, chinatsu, yui, kyoko)
  }

  def triple[F[_]: Plus, A](fa: F[A]) = fa <+> fa <+> fa
  triple(Option(1)) assert_=== Option(1)
  triple(List(1)) assert_=== List(1, 1, 1)

  def fdouble[F[_]: Functor, A: Semigroup](fa: F[A]) = fa.map(a => a |+| a)
  fdouble(List(1, 2, 3)) assert_=== List(2, 4, 6)
  fdouble("geso".some) assert_=== Some("gesogeso")
  import vector._
  fdouble(Vector(1.2, 2.1)) assert_=== Vector(2.4, 4.2)

  locally {
    val fa = List(1, 2)
    lazy val f: Int => Int = _ + 2
    lazy val g: Int => Int = _ * 2
    fa map (x => x) assert_=== fa
    fa map f map g assert_=== (fa map g <<< f)
  }

  Pointed[List].point(1) assert_=== List(1)
  Pointed[Option].point(1) assert_=== Some(1)
  import vector._
  Pointed[Vector].point(1) assert_=== Vector(1)

  assert(Functor[({ type F[A] = Either[String, A] })#F].map(Right(1))(_.succ) === Right(2))
  assert(Pointed[({ type F[A] = Either[String, A] })#F].point(1) === Right(1))

  Option(0) <*> Option(Enum[Int].succ _) assert_=== Option(1)
  List(1, 2, 3) <*> PlusEmpty[List].empty[Int => Int] assert_=== Nil
  import vector._
  Vector(1, 2) <*> Vector(Enum[Int].succ _, Enum[Int].pred _) assert_=== Vector(2, 3, 0, 1)

  locally {
    val a = 0
    val fa = Option(a)
    lazy val fab: Option[Int => String] = Option(_.toString)
    lazy val fbc: Option[String => Int] = Option(_.size)
    fa <*> ((a: Int) => a).point[Option] assert_=== fa
    fa <*> fab <*> fbc assert_=== fa <*> (fab <*> (fbc <*> (((bc: String => Int) => (ab: Int => String) => bc compose ab).point[Option])))
    a.point[Option] <*> fab assert_=== fab <*> ((f: Int => String) => f(a)).point[Option]
  }

  def append3[F[_]: Apply, A: Semigroup](fa: F[A], fb: F[A], fc: F[A]) = (fa |@| fb |@| fc)(_ |+| _ |+| _)
  append3(Option(1), Option(2), Option(3)) assert_=== Option(6)
  append3(Option(1), None, Option(3)) assert_=== None
  append3(List(1), List(1, 2), List(1, 2, 3)) assert_=== List(3, 4, 5, 4, 5, 6)

  def user(m: Map[String, String]) = (m.get("id") |@| m.get("pass"))(User.apply)

  user(Map("id" -> "halcat0x15a", "pass" -> "gesogeso")) assert_=== Some(User("halcat0x15a", "gesogeso"))
  user(Map.empty) assert_=== None

  locally {
    def append3[F[_]: Bind, A: Semigroup](fa: F[A], fb: F[A], fc: F[A]) =
      for {
	a <- fa
	b <- fb
	c <- fc
      } yield a |+| b |+| c
    append3(Option(1), Option(2), Option(3)) assert_=== Option(6)
    append3(Option(1), None, Option(3)) assert_=== None
    import vector._
    append3(Vector(1), Vector(1, 2), Vector(1, 2, 3)) assert_=== Vector(3, 4, 5, 4, 5, 6)
  }

  (for (a <- List(1, 2)) yield a + 1) assert_=== List(1, 2).map(a => a + 1)
  (for (a <- Option(1); b <- Option(2)) yield a + b) assert_=== Option(1).flatMap(a => Option(2).map(b => a + b))
  (for (a <- List(1, 2) if a % 2 == 0) yield a) assert_=== List(1, 2).filter(a => a % 2 == 0)

  locally {
    import scala.util.control.Exception._
    val a = 1
    val fa = Option(a)
    lazy val f: Int => Option[String] = _.toString |> Option.apply
    lazy val g: String => Option[Int] = allCatch opt _.toInt
    (fa >>= (_.point[Option])) assert_=== fa
    (a.point[Option] >>= f) assert_=== f(a)
    (fa >>= f >>= g) assert_=== (fa >>= (a => f(a) >>= g))
  }

  def odds[F[_]: MonadPlus](f: F[Int]) = f filter (_ % 2 === 0)
  odds(List(1, 2, 3)) assert_=== List(2)
  odds(Option(1)) assert_=== None
  import vector._
  odds(Vector(1, 2, 3)) assert_=== Vector(2)

  def triple[FA](fa: FA)(implicit F: Unapply[Plus, FA]) = F.TC.plus(F.TC.plus(F(fa), F(fa)), F(fa))
}
