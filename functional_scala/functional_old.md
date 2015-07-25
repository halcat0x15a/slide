# 関数型Scalaのキホン

[halcat0x15a](https://twitter.com/halcat0x15a)


型クラスを中心にScalaにおける関数プログラミングの技法や慣習を紹介します



## 型クラス

型クラスは抽象化の手法の一つです

Monoidを例にScalaでの型クラスをみていきましょう


型クラスは`trait`で宣言します

```scala
trait Monoid[A] {
  def zero: A
  def append(x: A, y: A): A
}
```

Monoidは型パラメータ`A`に関して

* 単位元: `zero`
* 二項演算: `append`

をもちます


単位元と二項演算は次の法則を満たします

* append(x, zero) == x
* append(zero, x) == x
* append(x, append(y, z)) == append(append(x, y), z)


IntをMonoidで抽象化してみましょう

型クラスのインスタンスはimplicitを付けて宣言します

```scala
implicit val intMonoid: Monoid[Int] =
  new Monoid[Int] {
    def zero: Int = 0
    def append(x: Int, y: Int): Int = x + y
  }
```

型クラスのインスタンスは型を明示すべきです


この実装は法則を満たしていることがわかります

* x + 0 == x
* 0 + x == x
* x + (y + z) == (x + y) + z


Stringも同様にMonoidで抽象化してみましょう

```scala
implicit val stringMonoid: Monoid[String] =
  new Monoid[String] {
    def zero: String = ""
    def append(x: String, y: String): String = x + y
  }
```


Monoidを使った関数を定義してみましょう

```scala
def double[A](a: A)(implicit A: Monoid[A]): A = A.append(a, a)
```

型クラスはカリー化されたimplicitパラメータにとります


implicitパラメータは型パラメータと同名の変数を付けることがあります

* 変数名を考えなくてよい
* どの型パラメータのインスタンスかわかりやすい


implicitがスコープ内にあれば`double`メソッドは次のように呼び出すことができます

```scala
assert(double(2) == 4)

assert(double("hoge") == "hogehoge")
```


implicitパラメータを明示しない記法があります

```scala
def quadruple[A: Monoid](a: A) = double(double(a))

assert(quadruple(2) == 8)
```

これはContext Boundsと呼ばれます



## implicit

implicitパラメータは呼び出しのスコープ以外に次のオブジェクトからも探索します

1. データ型のコンパニオンオブジェクト
1. 型クラススーパークラスのコンパニオンオブジェクト
1. 型クラスのコンパニオンオブジェクト

この順序でインスタンスを定義するオブジェクトを検討するとよいでしょう


SemigroupはMonoidのスーパークラスで`append`メソッドのみをもちます

```scala
trait Semigroup[A] {
  def append(x: A, y: A): A
}

trait Monoid[A] extends Semigroup[A] {
  def zero: A
}
```


SemigroupのコンパニオンオブジェクトにMonoidのインスタンスを定義してみましょう

```scala
object Semigroup {
  implicit val stringMonoid: Monoid[String] =
    new Monoid[String] {
      def zero: String = ""
      def append(x: String, y: String): String = x + y
    }
}
```


Semigroupが要求された時にMonoidが供給されます

```scala
def double[A](a: A)(implicit A: Semigroup[A]): A = A.append(a, a)

assert(double("hoge") == "hogehoge")
```


ひとつの型に関して複数のインスタンスが定義されることがあります

```scala
implicit val sumMonoid: Monoid[Int] =
  new Monoid[Int] {
    def zero: Int = 0
    def append(x: Int, y: Int): Int = x + y
  }

implicit val productMonoid: Monoid[Int] =
  new Monoid[Int] {
    def zero: Int = 1
    def append(x: Int, y: Int): Int = x * y
  }
```

この場合はimportによる明示的なインスタンスの選択が必要です


値クラスを使ってインスタンスが一意に定まるようにしてもよいでしょう

```scala
case class Sum(value: Int) extends AnyVal

case class Product(value: Int) extends AnyVal

implicit val sumMonoid: Monoid[Sum] =
  new Monoid[Sum] {
    def zero: Sum = Sum(0)
    def append(x: Sum, y: Sum): Sum = Sum(x.value + y.value)
  }

implicit val productMonoid: Monoid[Product] =
  new Monoid[Product] {
    def zero: Product = Product(1)
    def append(x: Product, y: Product): Product = Product(x.value * y.value)
  }
```

この場合は値をラップしてやる必要があります


implicitな値は型パラメータをもつことが可能です

ListをMonoidで抽象化してみましょう

```scala
implicit def listMonoid[A]: Monoid[List[A]] =
  new Monoid[List[A]] {
    def zero: List[A] = Nil
    def append(x: List[A], y: List[A]): List[A] = x ::: y
  }
```

型パラメータをとるにはdefで宣言する必要があります


implicitな値はimplicitパラメータをもつことが可能です

IntのMonoidを数値に一般化してみましょう

```scala
implicit def sumMonoid[A](implicit A: scala.math.Numeric[A]): Monoid[A] =
  new Monoid[A] {
    def zero: A = A.zero
    def append(x: A, y: A): A = A.plus(x, y)
  }

implicit def productMonoid[A](implicit A: scala.math.Numeric[A]): Monoid[A] =
  new Monoid[A] {
    def zero: A = A.one
    def append(x: A, y: A): A = A.times(x, y)
  }
```

NumericはScala標準の型クラスです


`append`のような二項演算は演算子として定義するのもよいでしょう

```scala
implicit class MonoidOps[A](x: A)(implicit A: Monoid[A]) {
  def <>(y: A): A = A.append(x, y)
}

assert(1 <> 2 <> 3 == 6)
assert("foo" <> "bar" <> "baz" == "foobarbaz")
```



## higher-kinded type

Scalaでは型パラメータをとる型パラメータを

指定できます

Functorを例に高階型をみていきましょう


Functorは`map`メソッドをもちます

```scala
import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

`map`は関数`A => B`を`F[A] => F[B]`に写し

`F[A]`に適用する関数と見做せます


`map`のように関数をカリー化されたパラメータに

とることで型推論を効かせることができます


mapは次の法則を満たします

* map(fa)(x => x) == fa
* map(fa)(x => g(f(x))) == map(map(fa)(f))(g)


ListとOptionをFunctorで抽象化してみましょう

```scala
implicit val listFunctor: Functor[List] =
  new Functor[List] {
    def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
  }

implicit val optionFunctor: Functor[Option] =
  new Functor[Option] {
    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }
```


Functorを使った関数を定義してみましょう

```scala
def pair[F[_], A](fa: F[A])(implicit F: Functor[F]): F[(A, A)] = F.map(fa)(a => (a, a))
```


`pair`メソッドは次のように呼び出すことができます

```scala
assert(pair(List(0, 1, 2)) == List((0, 0), (1, 1), (2, 2)))

assert(pair(Option(0)) == Some((0, 0)))
```


EitherもまたFunctorとみなすことができます

```scala
implicit def eitherFunctor[A]: Functor[({ type F[B] = Either[A, B] })#F] =
  new Functor[({ type F[B] = Either[A, B] })#F] {
    def map[B, C](fa: Either[A, B])(f: B => C): Either[A, C] = fa.right.map(f)
  }
```

Eitherは型パラメータをふたつとるため型を部分的に適用する必要があります


型の部分適用の記法は次のような構造になっています

* 型メンバをもつ無名の型を宣言 `{ type F[B] }`
* 型の適用 `type F[B] = Either[A, B]`
* 型メンバの参照 `({ type F[B] = Either[A, B] })#F`


`pair`メソッドの適用には型パラメータをひとつとる型として明示する必要があります

```scala
type StringEither[A] = Either[String, A]

assert(pair(Right(0): StringEither[Int]) == Right((0, 0)))
```
