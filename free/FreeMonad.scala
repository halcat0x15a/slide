import scala.language.postfixOps
import scalaz._, Scalaz._
import effect._, IO._

case class Pair[+A](left: A, right: A)

object Pair {
  implicit object Instance extends Functor[Pair] {
    def map[A, B](pair: Pair[A])(f: A => B) = Pair(f(pair.left), f(pair.right))
  }
}

sealed trait Part
case object Left extends Part
case object Right extends Part

object BinTreeApp extends SafeApp {

  type BinTree[+A] = Free[Pair, A]

  def branch[A](left: A, right: A) =
    Free.Suspend[Pair, A](Pair(Free.Return(left), Free.Return(right)))

  def branch[A](left: BinTree[A], right: A) =
    Free.Suspend[Pair, A](Pair(left, Free.Return(right)))

  def branch[A](left: A, right: BinTree[A]) =
    Free.Suspend[Pair, A](Pair(Free.Return(left), right))

  def get[A](i: List[Part])(tree: BinTree[A]) = tree.map(_.some).foldRun(i) {
    case (Nil, _) => Nil -> Free.Return(none[A])
    case (Left :: tail, Pair(left, _)) => tail -> left
    case (Right :: tail, Pair(_, right)) => tail -> right
  }._2

  override def runc = for {
    _ <- putStrLn("Binary Tree")
    tree = branch(branch(1, branch(branch(2, branch(3, 4)), 5)), 6)
    _ <- putOut(tree) >> putStrLn("")
    _ <- putStrLn("get example")
    _ <- putStrLn(get(Nil)(tree).shows)
    _ <- putStrLn(get(List(Left, Left))(tree).shows)
    _ <- putStrLn(get(List(Left, Right, Left, Right, Right))(tree).shows)
    _ <- putStrLn("map example")
    _ <- putStrLn(tree.map(_.succ) |> get(List(Right)) shows)
  } yield ()

}


object StreamApp extends SafeApp {

  type Stream[A] = Cofree[Function0, A]

  def iterate[A, B](z: A)(f: A => A): Stream[A] =
    Cofree.unfoldC(z)(a => () => f(a))

  def take[A](n: Int)(s: Stream[A]) = {
    def iter(n: Int, a: List[A], s: Stream[A]): List[A] =
      n match {
	case m if m <= 1 => s.extract :: a
	case m => iter(m.pred, s.extract :: a, s.out())
      }
    iter(n, Nil, s).reverse
  }

  override def runc = for {
    _ <- putStrLn("Stream")
    numbers = iterate(1)(_.succ)
    _ <- putStrLn("take example")
    _ <- putStrLn(take(5)(numbers).shows)
    _ <- putStrLn("with side effect")
    numbers = iterate(1) { n => println(n); n.succ }
    _ <- putStrLn(take(7)(numbers).shows)
  } yield ()

}

object Factorial extends SafeApp {

  val Zero = BigInt(0)
  val One = BigInt(1)

  lazy val factorial: BigInt => BigInt = {
    case Zero | One => One
    case n => n + factorial(n - 1)
  }

  override def runc = for {
    _ <- putOut(factorial(10000))
  } yield ()

}

object Foldl {

  def foldl[A, B](as: List[A], b: B, f: (B, A) => B): B =
    as match {
      case Nil => b
      case x :: xs => foldl(xs, f(b, x), f)
    }

  def foldlw[A, B](as: List[A], b: B, f: (B, A) => B): B = {
    var z = b
    var az = as
    while (true) {
      az match {
	case Nil => return z
	case x :: xs => {
	  z = f(z, x)
	  az = xs
	}
      }
    }
    z
  }

}

object EvenOdd {

  lazy val even: Int => Boolean = {
    case 0 => true
    case n => odd(n - 1)
  }

  lazy val odd: Int => Boolean = {
    case 0 => false
    case n => even(n - 1)
  }

}
