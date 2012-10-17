import scala.language.higherKinds

package object free {

  type Trampoline[+A] = Free[Function0, A]

  implicit val f0Functor =
    new Functor[Function0] {
      def map[A, B](a: () => A)(f: A => B) =
	() => f(a())
    }

}

package free {

  sealed trait Either[+A, +B]
  case class Left[+A, +B](a: A) extends Either[A, B]
  case class Right[+A, +B](b: B) extends Either[A, B]

  sealed trait Free[S[+_], +A] {
    private case class FlatMap[S[+_], A, +B](a: Free[S, A], f: A => Free[S, B]) extends Free[S, B]
    def flatMap[B](f: A => Free[S, B]): Free[S, B] =
      this match {
	case a FlatMap g =>
	  FlatMap(a, (x: Any) => g(x) flatMap f)
	case x => FlatMap(x, f)
      }
    final def resume(implicit S: Functor[S]): Either[S[Free[S, A]], A] =
      this match {
	case Done(a) => Right(a)
	case More(k) => Left(k)
	case a FlatMap f => a match {
	  case Done(a) => f(a).resume
	  case More(k) => Left(S.map(k)(_ flatMap f))
	  case b FlatMap g => b.flatMap((x: Any) => g(x) flatMap f).resume
	}
      }
    def zip[B](b: Free[S, B])(implicit S: Functor[S]): Free[S, (A, B)] =
      (resume, b.resume) match {
	case (Left(a), Left(b)) =>
	  More(S.map(a)(x => More(S.map(b)(y => x zip y))))
	case (Left(a), Right(b)) =>
	  More(S.map(a)(x => x zip Done(b)))
	case (Right(a), Left(b)) =>
	  More(S.map(b)(y => Done(a) zip y))
	case (Right(a), Right(b)) =>
	  Done((a, b))
      }
  }

  case class Done[S[+_], +A](a: A) extends Free[S, A]

  case class More[S[+_], +A](k: S[Free[S, A]]) extends Free[S, A]

  trait Functor[F[_]] {
    def map[A, B](m: F[A])(f: A => B): F[B]
  }

}
