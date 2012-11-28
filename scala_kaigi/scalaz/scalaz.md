!SLIDE

# Scalaz!

[@halcat0x15a](http://twitter.com/#!/halcat0x15a)

!SLIDE

# 自己紹介

* よしだ さんしろう
* 高校生
* Scala, Clojure, Python
* Twitter [http://twitter.com/#!/halcat0x15a](http://twitter.com/#!/halcat0x15a)
* github [http://github.com/halcat0x15a](http://github.com/halcat0x15a)

!SLIDE

今日はScalazについて話します。

JIT(Just In Tsukkomi)は大歓迎。

!SLIDE

いま話題のScalaz！

!SLIDE

# 皆さんの声

* 難しい！
* 怖い！
* 変態！
* Haskell！
* ☆！

!SLIDE

# [Scalaz](http://code.google.com/p/scalaz/)ってなあに？

[github](http://github.com/scalaz/scalaz)には

*"An extension to the core scala library."*

と書いてある。

!SLIDE

まあ、コードを見ればわかるのではないか？

!SLIDE

# 使い方

```scala
scala> import scalaz._
import scalaz._

scala> import Scalaz._
import Scalaz._

scala>
```

!SLIDE

簡単そうだね！

!SLIDE

# 何ができるようになったか

```scala
scala> 1 |+| 1
res0: Int = 2

scala> 'A === 'A
res1: Boolean = true

scala> 1.0.some
res2: Option[Double] = Some(1.0)

scala> 
```

!SLIDE

(；ﾟДﾟ)！？

!SLIDE

# 何が起きているのか

[Int](http://www.scala-lang.org/api/current/index.html#scala.Int)や[Symbol](http://www.scala-lang.org/api/current/index.html#scala.Symbol),[Double](http://www.scala-lang.org/api/current/index.html#scala.Double)に存在しないメソッドが呼べた => implicit conversion!

!SLIDE

どんなものに変換されているのだろう？

!SLIDE

# 調べる

```scala
scala> (1: { def some: Option[Int] }).getClass
res16: java.lang.Class[_ <: AnyRef] = class scalaz.Identity$$anon$1

```

!SLIDE

どうやら*Identity*とやらに変換されている模様

!SLIDE

# [Identity](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc.sxr/scalaz/Identity.scala.html)

```scala
scala> (1: Identity[Int])
res19: scalaz.Identity[Int] = 1

scala> res19.
/==             ===             ??              ?|?             asInstanceOf    assert_===      assert_≟        canEqual        cons            
constantState   doWhile         dual            equal           equalA          equalBy         fail            failNel         gt              
gte             isInstanceOf    iterate         leaf            left            logger          lt              lte             mapply          
matchOrZero     max             min             node            ok              pair            print           println         pure            
pureUnit        repeat          replicate       right           set             show            shows           snoc            some            
squared         state           success         successNel      text            toString        unfold          unfoldTree      unfoldTreeM     
unit            value           whileDo         wrapNel         zipper          |+|             |>              η               σ               
≟               ≠               ⊹               

scala> res19.
```

!SLIDE

なんだか変な名前のメソッドがいっぱいですね！

!SLIDE

# 自分で定義したクラスを試す

```scala
scala> case class ScalaChan()
defined class ScalaChan

scala> ScalaChan() === ScalaChan()
<console>:16: error: could not find implicit value for parameter e: scalaz.Equal[ScalaChan]
              ScalaChan() === ScalaChan()
                          ^

scala> 
```

!SLIDE

**Equal[ScalaChan]**の*implicit value*が見つからない。

!SLIDE

# [===](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc.sxr/scalaz/Identity.scala.html#48251)の定義

```scala
sealed trait Identity[A] extends Equals with IdentitySugar[A] {
  def value: A

  def ===(a: A)(implicit e: Equal[A]): Boolean = e equal (value, a)

```

!SLIDE

# [Equal](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc.sxr/scalaz/Equal.scala.html)の定義

```scala
trait Equal[-A] {
  def equal(a1: A, a2: A): Boolean
}
```

!SLIDE

シンプル！

!SLIDE

# Type Class

この*Equal*というのは一般的に*型クラス*と呼ばれるものです。

*型クラス*はある型に対して性質を定義することができます。

!SLIDE

Equalは同値比較をするための型クラスです。

!SLIDE

# 論よりコード

```scala
scala> implicit lazy val ScalaChanEqual: Equal[ScalaChan] = new Equal[ScalaChan] {
     |   def equal(a1: ScalaChan, a2: ScalaChan): Boolean = a1 == a2
     | }
ScalaChanEqual: java.lang.Object with scalaz.Equal[ScalaChan]

scala> ScalaChan() === ScalaChan()
res21: Boolean = true

scala> Identity(ScalaChan()).===(ScalaChan())(ScalaChanEqual)
res22: Boolean = true

scala> 
```

!SLIDE

*ScalaChanEqual*を作ったことで**ScalaChan**も*===*が利用できるようになりました。

!SLIDE

# もう一つ例

```scala
scala> implicit lazy val ScalaChanShow: Show[ScalaChan] = new Show[ScalaChan] {
     |   def show(a: ScalaChan): List[Char] = "Scalaちゃん".toList
     | }
ScalaChanShow: scalaz.Show[ScalaChan] = <lazy>

scala> ScalaChan().shows
res0: String = Scalaちゃん

scala> Identity(ScalaChan()).shows(ScalaChanShow)
res1: String = Scalaちゃん

scala> 
```

!SLIDE

このようにScalazでは**implicit value**を定義していくことで、利用できる関数が増えていきます。

!SLIDE

# うれしいところ

* もとのデータに変更を加えることなく拡張が可能
* 必要最低限のものを定義するだけで高度な関数を利用可能
	* これには複数の性質を定義することが必要な場合も

!SLIDE

# 複数の性質を使う

*すべての要素にある値を加算する*

!SLIDE

# 普通に書く

```scala
scala> def mapAppend(s: Seq[Int])(i: Int): Seq[Int] = s.map(_ + i)
mapAppend: (s: Seq[Int])(i: Int)Seq[Int]

scala> mapAppend(List(1, 2, 3))(5)
res0: Seq[Int] = List(6, 7, 8)

scala> mapAppend(Vector(1, 2, 3))(5)
res1: Seq[Int] = Vector(6, 7, 8)

scala> 
```

!SLIDE

Int以外にDoubleやStringも対応させたい。

*結合二項演算*が必要。

!SLIDE

# [Semigroup](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Semigroup)

```scala
scala> def mapAppend[A: Semigroup](s: Seq[A])(a: A): Seq[A] = s.map(_ |+| a)
mapAppend: [A](s: Seq[A])(a: A)(implicit evidence$1: scalaz.Semigroup[A])Seq[A]

scala> mapAppend(List(1, 2, 3))(5)
res2: Seq[Int] = List(6, 7, 8)

scala> mapAppend(List("Hello", "Real"))("World")
res3: Seq[java.lang.String] = List(HelloWorld, RealWorld)

scala> 
```

!SLIDE

Seq以外にも対応したい。

各要素に関数を適用する関数(*map*)が必要。

!SLIDE

# [Functor](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Functor)

```scala
scala> def mapAppend[M[_]: Functor, A: Semigroup](m: M[A])(a: A): M[A] = m.map(_ |+| a)
mapAppend: [M[_], A](m: M[A])(a: A)(implicit evidence$1: scalaz.Functor[M], implicit evidence$2: scalaz.Semigroup[A])M[A]

scala> mapAppend(List(1, 2, 3))(5)
res4: List[Int] = List(6, 7, 8)

scala> mapAppend(Option("Hello"))("World")
res5: Option[java.lang.String] = Some(HelloWorld)

scala> 
```

!SLIDE

# 解決済みのmapAppend

```scala
def mapAppend[M[_], A](m: M[A])(a: A)(implicit f: Functor[M], s: Semigroup[A]) =
  maImplicit(m).map(x => (mkIdentity(x) |+| a)(s))(f)

mapAppend[Option, String](
  Option("Hello")
)(
  "World"
)(
  Functor.OptionFunctor, Semigroup.StringSemigroup
)
```

!SLIDE

# Scalazの基本

* **性質を定義する**
* **性質を利用する**

!SLIDE

# Scalazに関する基礎知識

!SLIDE

# 重要な３つの型

implicit conversionによりScalazの主な関数を提供します。

* [Identity](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Identity)
	* すべての型
* [MA](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.MA)
	* 型パラメータを１つとる型
* [MAB](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.MAB)
	* 型パラメータを２つとる型

!SLIDE

# 命名規則

!SLIDE

# クラス名+W

## *Wrapper Class*

* [IntW](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.IntW)
* [StringW](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.StringW)
* [ListW](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.ListW)
* [OptionW](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.OptionW)
* [Tuple2W](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Tuples$Tuple2W)
* [Function1W](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Function1W)
* etc...

!SLIDE

# 型クラス名+s

## *implicit conversion*, *factory method*

ScalazオブジェクトにMix-inされる。

* [Equals](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Equals)
* [Orders](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Orders)
* [Shows](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Shows)
* [Semigroups](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Semigroups)
* [Zeros](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Zeros)
* etc...

!SLIDE

# 型クラス名+Low

## *implicit function*

型クラス自身が継承する。

* [OrderLow](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.OrderLow)
* [MonoidLow](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.MonoidLow)
* [ApplicativeLow](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.ApplicativeLow)
* [MonadLow](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.MonadLow)
* [ComonadLow](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.Comonad)
* etc...

!SLIDE

# 型名+Sugar

## *function sugar*

例のunicode文字の関数が定義されたもの。

* [IdentitySugar](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.IdentitySugar)
* [MASugar](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.MASugar)
* [MAContravariantSugar](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.MAContravariantSugar)

!SLIDE

Scalazのドキュメントはこれらを抑えておけば読めるようにます。

!SLIDE

# 最後に

[一人Scalaz Advent Calendar](http://partake.in/events/4b3afdc8-e4ec-4010-b8ec-31b89210dda0)やってます。

少しでもScalazの日本語の情報が増えたらなと思います。

!SLIDE

# ご清聴ありがとうございました
