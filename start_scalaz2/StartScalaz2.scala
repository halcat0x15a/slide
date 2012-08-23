import language.postfixOps
import language.higherKinds

import scalaz._, Scalaz._

case class Book(title: String, price: Int)

case class Person(name: String, money: Int)

object Person {
  val money: Lens[Person, Int] = Lens.lensu((p, m) => p copy (money = m), _.money)
}

object StartScalaz2 extends App {

  locally {
    def a[A: Show: Equal](a: A, b: A): Unit = a assert_=== b
    def b[A: Show](a: A): String = a.shows
    def c[A: Equal](a: A, b: A): Boolean = a === b
    def d[A: Equal](a: A, b: A): Boolean = a =/= b
    def e[A: Semigroup](a: A, b: A): A = a |+| b
    def f[A: Monoid]: A = mzero[A]
    def g[F[_]: Functor, A, B](fa: F[A])(f: A => B): F[B] = fa map f
    def h[F[_]: Applicative, A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = (fa |@| fb)(f)
    def i[F[_]: Monad, A, B](fa: F[A])(f: A => F[B]): F[B] = fa >>= f
    def j[A, B](a: A)(f: A => B): B = a |> f
    def k[A](b: Boolean)(a: A): Option[A] = b option a
  }

  (2 set "" run) assert_=== Writer("", 2).run
  (2 set "" value) assert_=== 2
  (2 set "" written) assert_=== ""

  (for {
    a <- 2 set mzero[List[Int]]
    b <- a + 2 set a.point[List]
    c <- b * 2 set b.point[List]
    d <- c - 2 set c.point[List]
    e <- d / 2 set d.point[List]
  } yield e).run assert_=== List(2, 4, 8, 6) -> 3

  case class Vec(x: Int, y: Int)

  def move(v: Vec) = v set math.sqrt(v.x * v.x + v.y * v.y)

  (for {
    a <- move(Vec(3, 4))
    b <- move(Vec(5, 12))
    c <- move(Vec(7, 24))
  } yield (a.x + b.x + c.x, a.y + b.y + c.y)).run assert_=== 43.0 -> (15, 40)

  def buy(book: Book) = book.title set book.price

  (buy(Book("yuruyuri", 900)) |@|
   buy(Book("mathgirl", 1800)) |@|
   buy(Book("genshiken", 600)) |@|
   buy(Book("mudazumo", 700)))(_ :: _ :: _ :: _ :: Nil).run assert_===
  4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo")

  locally {
    def buy(book: Book) = book.price < 2000 option (book.title set book.price)

    (for {
      a <- buy(Book("yuruyuri", 900))
      b <- buy(Book("mathgirl", 1800))
      c <- buy(Book("genshiken", 600))
      d <- buy(Book("mudazumo", 700))
    } yield (for {
      e <- a
      f <- b
      g <- c
      h <- d
    } yield List(e, f, g, h)).run) assert_===
    Some(4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo"))

    (buy(Book("yuruyuri", 900)) |@|
     buy(Book("mathgirl", 1800)) |@|
     buy(Book("genshiken", 600)) |@|
     buy(Book("mudazumo", 700)))(
       _ |@| _ |@| _ |@| _ |> (_(_ :: _ :: _ :: _ :: Nil).run)) assert_===
    Some(4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo"))
  }

  locally {
    def buy(book: Book) = WriterT(book.price < 2000 option book.price -> book.title)

    (buy(Book("yuruyuri", 900)) |@|
     buy(Book("mathgirl", 1800)) |@|
     buy(Book("genshiken", 600)) |@|
     buy(Book("mudazumo", 700)))(_ :: _ :: _ :: _ :: Nil).run assert_===
    Some(4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo"))

    (buy(Book("yuriyuri", 900)) |@|
     buy(Book("programming in scala", 4800)) |@|
     buy(Book("ubunchu", 800)))(_ :: _ :: _ :: Nil).run assert_===
    None
  }

  locally {
    (for {
      person <- init[Person]
      _ <- modify[Person](_ copy (money = person.money + 100))
      person <- get
    } yield person.money) eval Person("Sanshiro", 1000) assert_=== 1100

    (for {
      money <- Person.money += 100
    } yield money) eval Person("Sanshiro", 1000) assert_=== 1100

    def buy(book: Book) = Person.money -= book.price

    (for {
      _ <- buy(Book("yuruyuri", 900))
      _ <- buy(Book("mathgirl", 1800))
      _ <- buy(Book("genshiken", 600))
      money <- buy(Book("mudazumo", 700))
    } yield money) eval Person("Sanshiro", 5000) assert_=== 1000

    def check = StateT[Option, Person, Unit](p => p.money >= 0 option p -> ())

    (for {
      _ <- buy(Book("yuruyuri", 900)).lift[Option]
      _ <- buy(Book("mathgirl", 1800)).lift[Option]
      _ <- buy(Book("genshiken", 600)).lift[Option]
      _ <- buy(Book("mudazumo", 700)).lift[Option]
      _ <- check
      person <- get.lift[Option]
    } yield person.money) eval Person("Sanshiro", 3000) assert_=== None
  }

  2.success[String] assert_=== Validation.success(2)
  "geso".failure[Int] assert_=== Validation.failure("geso")

  lazy val message: NumberFormatException => String = _.getMessage
  lazy val parseInt: String => Validation[String, Int] = message <-: _.parseInt

  parseInt("2") assert_=== 2.success
  parseInt("geso") assert_=== """For input string: "geso"""".failure


  locally {
    lazy val f: String => Option[Int] = _.parseInt.toOption
    lazy val g: Int => Option[String] = n => n =/= 0 option (1 / n shows)
    locally {
      lazy val h: String => Option[String] = _ |> f >>= g
    }
    locally {
      lazy val h: Kleisli[Option, String, String] = Kleisli(f) >=> Kleisli(g)
    }
    locally {
      lazy val h: Kleisli[Option, String, String] = Kleisli(f) >>> Kleisli(g)
    }
  }

  locally {
    lazy val f: String => Int = _.toInt
    lazy val g: Int => String = 1 / _ shows
    lazy val h: String => String = f andThen g
    locally {
      lazy val h: String => String = f >>> g
    } 
  }

  locally {
    lazy val f: Kleisli[Option, String, Int] = Kleisli(_.parseInt.toOption)
    lazy val g: Kleisli[Option, Int, String] = Kleisli(n => n =/= 0 option (1 / n shows))
    lazy val h: Kleisli[Option, String, String] = f >>> g
  }

  ArrId[Function1].id(2) assert_=== 2
  ArrId[({ type F[A, B] = Kleisli[Option, A, B] })#F].id(2) assert_=== Some(2)

}
