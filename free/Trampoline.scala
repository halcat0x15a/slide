import scala.language.postfixOps

package trampoline {

  sealed trait Either[+A, +B]

  case class Left[+A, +B](a: A) extends Either[A, B]

  case class Right[+A, +B](b: B) extends Either[A, B]

  sealed trait Trampoline[+A] {
   def flatMap[B](f: A => Trampoline[B]): Trampoline[B] =
     this match {
       case a FlatMap g =>
 	FlatMap(a, (x: Any) => g(x) flatMap f)
       case x => FlatMap(x, f)
     }
   final def resume: Either[() => Trampoline[A], A] =
     this match {
 	case Done(a) => Right(a)
 	case More(k) => Left(k)
 	case a FlatMap f => a match {
 	  case Done(a) => f(a).resume
 	  case More(k) => Left(() => k() flatMap f)
 	  case b FlatMap g => b.flatMap((x: Any) => g(x) flatMap f).resume
 	}
     }
   def zip[B](b: Trampoline[B]): Trampoline[(A, B)] =
     (resume, b.resume) match {
 	case (Left(a), Left(b)) =>
 	  More(() => a() zip b())
 	case (Left(a), Right(b)) =>
 	  More(() => a() zip Done(b))
 	case (Right(a), Left(b)) =>
 	  More(() => Done(a) zip b())
 	case (Right(a), Right(b)) =>
 	  Done((a, b))
     }
 }

  case class Done[+A](a: A) extends Trampoline[A]

  case class More[+A](k: () => Trampoline[A]) extends Trampoline[A]

  case class FlatMap[A, +B](a: Trampoline[A], f: A => Trampoline[B]) extends Trampoline[B]

}

package simple {

  sealed trait Trampoline[+A] {
    final def runT: A =
      this match {
	case More(k) => k().runT
	case Done(v) => v
      }
    def flatMap[B](f: A => Trampoline[B]) =
      More(() => f(runT))
  }

  case class Done[+A](a: A) extends Trampoline[A]

  case class More[+A](k: () => Trampoline[A]) extends Trampoline[A]

  package object trampoline {

    lazy val even: Int => Trampoline[Boolean] = {
      case 0 => Done(true)
      case n => More(() => odd(n - 1))
    }

    lazy val odd: Int => Trampoline[Boolean] = {
      case 0 => Done(false)
      case n => More(() => even(n - 1))
    }

    val Zero = BigInt(0)
    val One = BigInt(1)

    lazy val factorial: BigInt => Trampoline[BigInt] = {
      case Zero | One => Done(One)
      case n => More(() => Done(n * factorial(n - 1).runT))
    }

  }

}

package improved {

  sealed trait Either[+A, +B]
  case class Left[+A, +B](a: A) extends Either[A, B]
  case class Right[+A, +B](b: B) extends Either[A, B]

  sealed trait Trampoline[+A] {
    def flatMap[B](f: A => Trampoline[B]): Trampoline[B] =
      this match {
	case a FlatMap g =>
	  FlatMap(a, (x: Any) => g(x) flatMap f)
	case x => FlatMap(x, f)
      }
    def map[B](f: A => B): Trampoline[B] =
      flatMap(a => Done(f(a)))
    @annotation.tailrec
    final def resume: Either[() => Trampoline[A], A] =
      this match {
	case Done(a) => Right(a)
	case More(k) => Left(k)
	case a FlatMap f => a match {
	  case Done(a) => f(a).resume
	  case More(k) => Left(() => k() flatMap f)
	  case b FlatMap g => b.flatMap((x: Any) => g(x) flatMap f).resume
	}
      }
    @annotation.tailrec
    final def runT: A = resume match {
      case Right(a) => a
      case Left(k) => k().runT
    }
  }

  case class Done[+A](a: A) extends Trampoline[A]

  case class More[+A](k: () => Trampoline[A]) extends Trampoline[A]

  case class FlatMap[A, +B](sub: Trampoline[A], k: A => Trampoline[B]) extends Trampoline[B]

  package object raw {
    lazy val fib: Int => Int = {
      case n if n < 2 => n
      case n => fib(n - 1) + fib(n - 2)
    }
  }

}

object Trampoline extends App {
  import improved._
  val Zero = BigInt(0)
  val One = BigInt(1)
  lazy val factorial: BigInt => Trampoline[BigInt] = {
    case Zero | One => Done(One)
    case n => More(() => factorial(n - 1)).map(n *)
  }
  lazy val fib: Int => Trampoline[Int] = {
    case n if n < 2 => Done(n)
    case n => for {
      x <- More(() => fib(n - 1))
      y <- More(() => fib(n - 2))
    } yield x + y
  }
  factorial(10000)
}
