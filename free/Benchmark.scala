import scala.language.postfixOps
import scalaz._, Scalaz._

object Benchmark extends App {

  lazy val even: Int => Boolean = {
    case 0 => true
    case n => odd(n.pred)
  }

  lazy val odd: Int => Boolean = {
    case 0 => false
    case n => even(n.pred)
  }

  lazy val feven: Int => Free.Trampoline[Boolean] = {
    case 0 => Free.Return(true)
    case n => Free.Suspend(() => fodd(n.pred))
  }

  lazy val fodd: Int => Free.Trampoline[Boolean] = {
    case 0 => Free.Return(false)
    case n => Free.Suspend(() => feven(n.pred))
  }

  lazy val fib: Long => Long = {
    case n if n < 2 => n
    case n => fib(n - 1) + fib(n - 2)
  }

  lazy val ffib: Long => Free.Trampoline[Long] = {
    case n if n < 2 => Free.Return(n)
    case n =>
      for {
	x <- Free.Suspend(() => ffib(n - 1))
	y <- Free.Suspend(() => ffib(n - 2))
      } yield x + y
  }

  lazy val sum: Long => Long = {
    case 0 => 0
    case n => n + sum(n.pred)
  }

  lazy val fsum: Long => Free.Trampoline[Long] = {
    case 0 => Free.Return(0)
    case m =>
      for {
	n <- Free.Suspend(() => fsum(m.pred))
      } yield m + n
  }

  def time[A](f: => A) = {
    val s = System.nanoTime
    val x = f
    val e = System.nanoTime
    println(x, (e - s toDouble) / 1000000000)
  }

  time(even(1000))
  time(feven(1000).run)
  time(fib(30))
  time(ffib(30).run)
  time(sum(5000))
  time(fsum(5000).run)

}
