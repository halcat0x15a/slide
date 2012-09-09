import language.postfixOps
import language.higherKinds

import scalaz._, Scalaz._

case class Book(title: String, price: Int)

case class Person(name: String, property: List[String], money: Int)

object Person {
  val property: Lens[Person, List[String]] = Lens.lensu((p, pr) => p copy (property = pr), _.property)
  val money: Lens[Person, Int] = Lens.lensg(p => m => p copy (money = m), _.money)
}

case class User(id: String, age: Int)

object User {
  implicit lazy val ShowUser = Show.showA[User]
  implicit lazy val EqualUser = Equal.equalA[User]
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

  (2 set "geso" run) assert_=== "geso" -> 2
  (2 set "geso" value) assert_=== 2
  (2 set "geso" written) assert_=== "geso"
  "geso".tell.written assert_=== "geso"

  locally {
    (for {
      a <- 2 set mzero[List[Int]]
      b <- a + 2 set a.point[List]
      c <- b * 2 set b.point[List]
      d <- c - 2 set c.point[List]
      e <- d / 2 set d.point[List]
    } yield e).run assert_=== List(2, 4, 8, 6) -> 3
  }

  (for {
    _ <- "start;".tell
    a <- 2 set "a = 2;"
    b = a + 2
    _ <- s"a + 2 = $b;".tell
    _ <- "end;".tell
  } yield b).run assert_===
    "start;a = 2;a + 2 = 4;end;" -> 4

  case class Vec(x: Int, y: Int)

  def move(v: Vec) = v set math.sqrt(v.x * v.x + v.y * v.y)

  (for {
    a <- move(Vec(3, 4))
    b <- move(Vec(5, 12))
    c <- move(Vec(7, 24))
  } yield (a.x + b.x + c.x, a.y + b.y + c.y)).run assert_=== 43.0 -> (15, 40)

  locally {
    def buy(book: Book) = book.title set book.price

    (buy(Book("yuruyuri", 900)) |@|
      buy(Book("mathgirl", 1800)) |@|
      buy(Book("genshiken", 600)) |@|
      buy(Book("mudazumo", 700)))(_ :: _ :: _ :: _ :: Nil).run assert_===
	4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo")
  }

  locally {
    def buy(book: Book): Option[Writer[Int, String]] =
      book.price < 2000 option (book.title set book.price)

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
    def buy(book: Book): WriterT[Option, Int, String] =
      WriterT(book.price < 2000 option book.price -> book.title)

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

  def buy(thing: String, price: Int): State[Person, Unit] =
    for {
      _ <- Person.property %= (thing :: _)
      _ <- Person.money -= price
    } yield ()

  def buy(book: Book): State[Person, Unit] =
    buy(book.title, book.price)

  locally {
    (for {
      person <- init[Person]
      _ <- modify[Person](_ copy (money = person.money + 100))
      person <- get
    } yield person.money) eval Person("Sanshiro", Nil, 1000) assert_=== 1100

    (for {
      money <- Person.money += 100
    } yield money) eval Person("Sanshiro", Nil, 1000) assert_=== 1100

    (for {
      _ <- buy("apple", 80)
      _ <- buy("apple", 80)
      _ <- buy("orange", 60)
      _ <- buy("orange", 60)
      _ <- buy("orange", 60)
      takashi <- get
    } yield takashi.money) eval Person("takashi", Nil, 400) assert_=== 60

    (for {
      _ <- Person.property := List("orange")
      property <- Person.property %= ("apple" :: _)
      money <- Person.money -= 80
    } yield property -> money) eval
    Person("takashi", Nil, 100) assert_=== List("apple", "orange") -> 20

    (for {
      _ <- buy(Book("yuruyuri", 900))
      _ <- buy(Book("mathgirl", 1800))
      _ <- buy(Book("genshiken", 600))
      _ <- buy(Book("mudazumo", 700))
      sanshiro <- get
    } yield sanshiro.money) eval
      Person("Sanshiro", Nil, 5000) assert_=== 1000

//    def buy(book: Book): State[Person, Unit] =
  //    buy(book.title, book.price)

    def check: StateT[Option, Person, Unit] =
      StateT(p => p.money >= 0 option p -> ())

    (for {
      money <- (Person.money -= 80).lift[Option]
      _ <- check
    } yield money) eval
      Person("takashi", Nil, 100) assert_=== Some(20)

    def buyAndCheck(book: Book): StateT[Option, Person, Unit] =
      for {
	_ <- buy(book).lift[Option]
	_ <- check
      } yield ()

    (for {
      _ <- buyAndCheck(Book("yuruyuri", 900))
      _ <- buyAndCheck(Book("mathgirl", 1800))
      _ <- buyAndCheck(Book("genshiken", 600))
      _ <- buyAndCheck(Book("mudazumo", 700))
      person <- get.lift[Option]
    } yield person.money) eval
      Person("Sanshiro", Nil, 3000) assert_=== None 
  }

  2.success[String] assert_=== Validation.success(2)
  "geso".failure[Int] assert_=== Validation.failure("geso")


  locally {
    import Validation._

    def parseInt(s: String): String \?/ Int = try {
      s.toInt.success
    } catch {
    case e: Throwable => e.getMessage.failure
    }

    parseInt("2") assert_=== 2.success
    parseInt("geso") assert_=== """For input string: "geso"""".failure

    (parseInt("2").toValidationNEL |@|
      parseInt("2").toValidationNEL)(_ * _) assert_===
	4.success

    (parseInt("foo").toValidationNEL |@|
      parseInt("bar").toValidationNEL)(_ * _) assert_===
	NonEmptyList(
	  """For input string: "foo"""",
	  """For input string: "bar""""
	).failure

    def plus(s1: String, s2: String): NonEmptyList[String] \?/ Int =
      (parseInt(s1).toValidationNEL |@| parseInt(s2).toValidationNEL)(_ + _)

    plus("1", "2") assert_=== 3.success
    plus("foo", "bar") assert_=== NonEmptyList(
      """For input string: "foo"""",
      """For input string: "bar""""
    ).failure

    def get[K, V](k: K)(m: Map[K, V]): String \?/ V = try {
      m(k).success
    } catch {
      case e: Throwable => e.getMessage.failure
    }

    def user(m: Map[String, String]): NonEmptyList[String] \?/ User =
      (get("id")(m).toValidationNEL |@|
	(get("age")(m) flatMap parseInt toValidationNEL))(User.apply)

    user(Map("id" -> "halcat0x15a", "age" -> "19")) assert_=== User("halcat0x15a", 19).success
    user(Map("id" -> "halcat0x15a")) assert_=== NonEmptyList("key not found: age").failure
    user(Map("age" -> "geso")) assert_===
      NonEmptyList("key not found: id", """For input string: "geso"""").failure

  }

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
    def get(s: String): Reader[Map[String, String], String] =
      Reader(_(s))

    (for {
      id <- get("id")
      age <- get("age")
    } yield User(id, age.toInt)) run 
      Map(
	"id" -> "halcat0x15a",
	"age" -> "19"
      ) assert_=== User("halcat0x15a", 19)
  }

  locally {
    def get(s: String): Kleisli[Option, Map[String, String], String] =
      Kleisli(_ get s)

    (for {
      id <- get("id")
      age <- get("age")
    } yield User(id, age.toInt)) run 
      Map(
	"age" -> "19"
      ) assert_=== None
  }

  locally {
    def buy(book: Book): Kleisli[Option, Person, Person] =
      Kleisli(p =>
	p.money >= 0 option p.copy(money = p.money - book.price))
    for {
      _ <- buy(Book("yuruyuri", 900))
      _ <- buy(Book("mathgirl", 1800))
      _ <- buy(Book("genshiken", 600))
      p <- buy(Book("mudazumo", 700))
    } yield p
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

  locally {
    def mod(n: Int, s: String): Int => Option[String] =
      _ % n === 0 option s
    lazy val fold: ((Option[String], Option[String])) => Option[String] =
      _.fold(_ |+| _)
    lazy val default: ((Option[String], Int)) => String =
      _.fold(_ | _.shows)
    lazy val fizzbuzz: Int => String =
      ((mod(3, "Fizz") &&& mod(5, "Buzz")) >>> fold &&& identity) >>> default

    fizzbuzz(2) assert_=== "2"
    fizzbuzz(3) assert_=== "Fizz"
    fizzbuzz(5) assert_=== "Buzz"
    fizzbuzz(15) assert_=== "FizzBuzz"
  }

  lazy val fib: Int => Int = {
    case 0 => 0
    case 1 => 1
    case n => fib(n - 1) + fib(n - 2)
  }

  locally {
    lazy val m: Int => Int = identity
    lazy val n: Int => Int = n => fib(n - 1) + fib(n - 2)
    lazy val either: Int => Int \/ Int = n => n < 2 either n or n
    lazy val fib: Int => Int = either >>> (m ||| n)    
  }

}
