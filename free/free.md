!SLIDE

# 6. Free Monads: A Generalization of
Trampoline

!SLIDE

TrampolineはFunction0を利用しています

このFunction0の部分を抽象化すると次のような定義が可能です

!SLIDE

```scala
sealed trait Free[S[+_], +A] {
  private case class FlatMap[S[+_], A, +B](a: Free[S, A], f: A => Free[S, B]) extends Free[S, B]
}

case class Done[S[+_], +A](a: A) extends Free[S, A]

case class More[S[+_], +A](k: S[Free[S, A]]) extends Free[S, A]
```

!SLIDE

Trampolineは以下のように定義出来る

```scala
type Trampoline[+A] = Free[Function0, A]
```

!SLIDE

Function0を抽象化したことによって、resumeを変更しなければならない

実は、resumeではFunction0をFunctorとして利用することが出来た

!SLIDE

# Functor

```scala
trait Functor[F[_]] {
  def map[A, B](m: F[A])(f: A => B): F[B]
}
```

!SLIDE

## Function0Functor

```scala
implicit val f0Functor =
  new Functor[Function0] {
    def map[A, B](a: () => A)(f: A => B): () => B =
      () => f(a())
  }
```

!SLIDE

# 6.1 Functions deﬁned on all free monads

resumeはFunctorを利用して次のように定義出来る

!SLIDE

```scala
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
```

!SLIDE

# 6.2 Common data types as free monads

!SLIDE

Freeで表現出来るデータ型はTrampolineだけではありません

Free[S, A]のSを枝、Aを葉と見做すことで木構造を表現出来ます

!SLIDE

```scala
type Pair[+A] = (A, A)

type BinTree[+A] = Free[Pair, A]
```

!SLIDE

この場合は枝はTuple2、葉はAで二分木を表現しています

Pairに対して2つの要素に関数を適用するようなFunctorを定義すれば、BinTreeは全ての葉を走査するようなMonadが定義されます

!SLIDE

# 6.3 A free State monad

最後に、Freeを使ったプログラミングについて話します

ここでは例としてStateを構築します

!SLIDE

まず最初に、枝となるデータ型を定義します

!SLIDE

```scala
sealed trait StateF[S, +A]

case class Get[S, A](f: S => A)
  extends State[S, A]

case class Put[S, A](s: S, a: A)
  extends State[S, A]
```

!SLIDE

ここで大切なことは関数のモデルをレコードで表現することで、実装は行ないません

!SLIDE

次にFunctorを定義します

!SLIDE

```scala
implicit def statefFun[S] =
  new Functor[({ type F[A] = StateF[S, A] })#F] {
    def map[A, B](m: StateF[S, A])(f: A => B): StateF[S, B] =
      m match {
        case Get(g) => Get((s: S) => f(g(s)))
        case Put(s, a) => Put(s, f(a))
      }
  }
```

!SLIDE

Functor則に気を付ければ自然とmapを定義することが可能です

!SLIDE

StateFを使ったFreeStateの定義は以下のようになります

!SLIDE

```scala
type FreeState[S, +A] =
  Free[({ type F[B] = StateF[S, B] })#F, A]
```

!SLIDE

FreeStateを返す関数として、次のようなものが定義出来ます

!SLIDE

```scala
def pureState[S, A](a: A): FreeState[S, A] =
  Done[({ type F[+B] = StateF[S, B] })#F, A](a)

def getState[S]: FreeState[S, S] =
  More[({ type F[+B] = StateF[S, B] })#F, S](
    Get(s => Done[({ type F[+B] = StateF[S, B] })#F, S](s)))

def setState[S](s: S): FreeState[S, Unit] =
  More[({ type F[+B] = StateF[S, B] })#F, Unit](
    Put(s, Done[({ type F[+B] = StateF[S, B] })#F, Unit](())))
```

!SLIDE

そして、最初に定義した関数のモデルの実装は、以下のように定義されます

!SLIDE

```scala
def evalS[S, A](s: S, t: FreeState[S, A]): A =
  t.resume match {
    case Left(Get(f)) => evalS(s, f(s))
    case Left(Put(n, a)) => evalS(n, a)
    case Right(a) => a
  }
```

!SLIDE

evalSは末尾で自身を呼び出しており、コンパイラによって最適化されます

このように、resumeを呼び出す関数で再帰的にモデルの評価を行なうことでス
タックを消費しない関数を定義することが可能です
