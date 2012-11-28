import scala.language.higherKinds

package object free {

  type Trampoline[+A] = Free[Function0, A]

  implicit val f0Functor =
    new Functor[Function0] {
      def map[A, B](a: () => A)(f: A => B): () => B =
	() => f(a())
    }

  implicit def statefFun[S] =
    new Functor[({ type F[+A] = StateF[S, A] })#F] {
      def map[A, B](m: StateF[S, A])(f: A => B): StateF[S, B] =
	m match {
	  case Get(g) => Get((s: S) => f(g(s)))
	  case Put(s, a) => Put(s, f(a))
	}
    }

  type FreeState[S, +A] =
    Free[({ type F[+B] = StateF[S, B] })#F, A]

  def pureState[S, A](a: A): FreeState[S, A] =
    Done[({ type F[+B] = StateF[S, B] })#F, A](a)

  def getState[S]: FreeState[S, S] =
    More[({ type F[+B] = StateF[S, B] })#F, S](
      Get(s => Done[({ type F[+B] = StateF[S, B] })#F, S](s)))

  def setState[S](s: S): FreeState[S, Unit] =
    More[({ type F[+B] = StateF[S, B] })#F, Unit](
      Put(s, Done[({ type F[+B] = StateF[S, B] })#F, Unit](())))

  def evalS[S, A](s: S, t: FreeState[S, A]): A =
    t.resume match {
      case Left(Get(f)) => evalS(s, f(s))
      case Left(Put(n, a)) => evalS(n, a)
      case Right(a) => a
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

  sealed trait StateF[S, +A]

  case class Get[S, A](f: S => A)
    extends StateF[S, A]

  case class Put[S, A](s: S, a: A)
    extends StateF[S, A]

}
