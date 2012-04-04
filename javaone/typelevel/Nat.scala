package nat

sealed abstract class Nat

sealed abstract class _0 extends Nat

case class S[N <: Nat](n: N) extends Nat

object Nat {
  val _0: _0 = new _0 {}
  val _1 = S(_0)
  val _2 = S(_1)
}

sealed abstract class <=[N <: Nat, M <: Nat]

object <= {
  import Nat._
  def check[N <: Nat, M <: Nat](n: N, m: M)(implicit ev: N <= M) {}
  implicit def e[N <: Nat]: N <= N = new <=[N, N] {}
  implicit def l[N <: Nat, M <: Nat](implicit ev: N <= M): N <= S[M] = new <=[N, S[M]] {}
  check(_0, _0)
  check(_0, _1)
  check(_0, _2)
  check(_1, _2)
}
