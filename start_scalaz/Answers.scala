import scalaz._, Scalaz._

object Answers extends App {
  def times[A: Semigroup](a: A, n: Int): A = n ensuring (_ > 0) match {
    case 1 => a
    case n => a |+| times(a, n - 1)
  }
  times(3, 3) assert_=== 9
  times("x", 5) assert_=== "xxxxx"
  times(List(1, 2), 2) assert_=== List(1, 2, 1, 2)
}
