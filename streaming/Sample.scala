import scalaz._, Scalaz._
import effect._, IO._

import com.clarifi.machines._, Machine._, Plan._

object Sample extends SafeApp {

  def lines = new Driver[IO, String => Any] {
    val M = Monad[IO]
    def apply(k: String => Any) = readLn map k map Some.apply
  }

  def append[A: Semigroup] = for {
    e1 <- await[A]
    e2 <- await[A]
    _ <- emit(e1 |+| e2)
  } yield ()

  override def runc = new Procedure[IO, String] {
    type K = String => Any
    def machine = append[String].compile
    def withDriver[R](f: Driver[IO, K] => IO[R]) = f(lines)
  } foreach putStrLn

}

object Sample2 extends SafeApp {

  def count = for {
    s <- await[String]
    _ <- emit(s.size)
  } yield ()

  def show = for {
    i <- await[Int]
    _ <- emit(i.shows)
  } yield ()

  override def runc = new Procedure[IO, String] {
    type K = String => Any
    def machine = count.compile andThen show.compile
    def withDriver[R](f: Driver[IO, K] => IO[R]) = f(Sample.lines)
  } foreach putStrLn

}
