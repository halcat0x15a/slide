import scalaz._, Scalaz._

object Answers extends App {
  /*
   * def times[A: Semigroup](a: A, n: Int): A = (1 |=> n).foldRight(a)(_ => _ |+| a)
   */
  def times[A: Semigroup](a: A, n: Int): A = (1 until n).foldRight(a)(_ => x => => x |+| a)
  times(3, 3) assert_=== 9
  times("x", 5) assert_=== "xxxxx"
  times(List(1, 2), 2) assert_=== List(1, 2, 1, 2)

  def fibonacci[A: Enum: Monoid](a: A): A = if (a <= mzero[A].succ)
    a
  else
    fibonacci(a.pred) |+| fibonacci(a.pred.pred)
  fibonacci(7) assert_=== 13
}
