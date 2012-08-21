import language.postfixOps

import scalaz._, Scalaz._

object StartScalaz2 extends App {

  (2 set "" run) assert_=== Writer("", 2).run
  (2 set "" value) assert_=== 2
  (2 set "" written) assert_=== ""

  (for {
    a <- 2 set mzero[List[Int]]
    b <- a + 2 set a.point[List]
    c <- b * 2 set b.point[List]
    d <- c - 2 set c.point[List]
    e <- d / 2 set d.point[List]
  } yield e).run assert_=== List(2, 4, 8, 6) -> 3

  case class Vec(x: Int, y: Int)

  def move(v: Vec) = v set math.sqrt(v.x * v.x + v.y * v.y)

  (for {
    a <- move(Vec(3, 4))
    b <- move(Vec(5, 12))
    c <- move(Vec(7, 24))
  } yield (a.x + b.x + c.x, a.y + b.y + c.y)).run assert_=== 43.0 -> (15, 40)

  case class Person(money: Int)

//  def buy(book: String) = 

}
