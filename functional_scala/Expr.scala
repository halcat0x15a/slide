package datatypesalacarte

import scala.language.higherKinds

trait Functor[F[_]] {

  def map[A, B](fa: F[A])(f: A => B): F[B]

}

object Functor {

  implicit val value: Eval[Val] = new Eval[Val] {

    def map[A, B](fa: Val[A])(f: A => B): Val[B] = Val(fa.n)

    def eval(fa: Val[Int]): Int = fa.n

  }

  implicit val add: Eval[Add] = new Eval[Add] {

    def map[A, B](fa: Add[A])(f: A => B): Add[B] = Add(f(fa.left), f(fa.right))

    def eval(fa: Add[Int]): Int = fa.left + fa.right

  }

  implicit val mul: Eval[Mul] = new Eval[Mul] {

    def map[A, B](fa: Mul[A])(f: A => B): Mul[B] = Mul(f(fa.left), f(fa.right))

    def eval(fa: Mul[Int]): Int = fa.left * fa.right

  }

  implicit def coproduct[F[+_], G[+_]](implicit F: Functor[F], G: Functor[G]): Functor[(F :+: G)#Coproduct] = new Functor[(F :+: G)#Coproduct] {

    def map[A, B](fa: (F :+: G)#Coproduct[A])(f: A => B): (F :+: G)#Coproduct[B] =
      fa match {
        case Inl(a) => Inl(F.map(a)(f))
        case Inr(a) => Inr(G.map(a)(f))
      }

  }

}

trait Eval[F[_]] extends Functor[F] {

  def eval(fa: F[Int]): Int

}

object Eval {

  def apply[F[+_]](expr: Expr[F])(implicit F: Eval[F]): Int =
    Expr.fold(expr)(F.eval)

  implicit def coproduct[F[+_], G[+_]](implicit F: Eval[F], G: Eval[G]): Eval[(F :+: G)#Coproduct] = new Eval[(F :+: G)#Coproduct] {

    def map[A, B](fa: (F :+: G)#Coproduct[A])(f: A => B): (F :+: G)#Coproduct[B] = Functor.coproduct[F, G].map(fa)(f)

    def eval(fa: (F :+: G)#Coproduct[Int]): Int =
      fa match {
        case Inl(a) => F.eval(a)
        case Inr(a) => G.eval(a)
      }

  }

}

case class Expr[+F[+_]](value: F[Expr[F]])

object Expr {

  def fold[F[+_], A](expr: Expr[F])(f: F[A] => A)(implicit F: Functor[F]): A = f(F.map(expr.value)(fold(_)(f)))

}

case class Val[+A](n: Int)

case class Add[+A](left: A, right: A)

case class Mul[+A](left: A, right: A)

sealed trait :+:[+F[_], +G[_]] extends Product with Serializable { self =>

  type Type[+A]

  sealed trait Coproduct[+A] {

    def value: Type[A]

  }

}

case class Inl[F[+_]]() extends (F :+: Nothing) {

  type Type[+A] = F[A]

  case class Left[A](value: Type[A]) extends Coproduct[A]

}

object Inl {

  def apply[F[+_], A](value: F[A]): Inl[F]#Coproduct[A] = Inl().Left(value)

  def unapply[F[+_], G[+_], A](coproduct: (F :+: G)#Coproduct[A]): Option[F[A]] =
    coproduct match {
      case left: Inl[F]#Left[A] => Some(left.value)
      case _: Inr[G]#Right[A] => None
    }

}

case class Inr[G[+_]]() extends (Nothing :+: G) {

  type Type[+A] = G[A]

  case class Right[A](value: Type[A]) extends Coproduct[A]

}

object Inr {

  def apply[G[+_], A](value: G[A]): Inr[G]#Coproduct[A] = Inr().Right(value)

  def unapply[F[+_], G[+_], A](coproduct: (F :+: G)#Coproduct[A]): Option[G[A]] =
    coproduct match {
      case _: Inl[F]#Left[A] => None
      case right: Inr[G]#Right[A] => Some(right.value)
    }

}

object Main extends App {
  
  println(Eval(Expr(Inr(Inr(Add(Expr(Inr(Inl(Mul(Expr(Inl(Val(80))), Expr(Inl(Val(5))))))), Expr(Inl(Val(4)))))))))

  println(Eval(Expr(Inr(Add(Expr(Inl(Val(118))), Expr(Inl(Val(1219))))))))

}
