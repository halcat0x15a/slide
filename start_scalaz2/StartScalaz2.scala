import language.postfixOps
import language.higherKinds

import scalaz._, Scalaz._

object StartScalaz2 extends App {

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

  case class Person(money: Int)

  case class Book(title: String, price: Int)

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

}
