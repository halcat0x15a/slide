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
  }

  case class Done[+A](a: A) extends Trampoline[A]

  case class More[+A](k: () => Trampoline[A]) extends Trampoline[A]

}

package object trampoline {

  lazy val even: Int => Trampoline[Boolean] = {
    case 0 => Done(true)
    case n => More(() => odd(n - 1))
  }

  lazy val odd: Int => Trampoline[Boolean] = {
    case 0 => Done(false)
    case n => More(() => even(n - 1))
  }

}
