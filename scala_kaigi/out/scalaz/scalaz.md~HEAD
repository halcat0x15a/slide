!SLIDE

# Scalaz!

[@halcat0x15a](http://twitter.com/#!/halcat0x15a)

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

# 使い方

```scala
scala> import scalaz._
import scalaz._

scala> import Scalaz._
import Scalaz._

scala>
```

!SLIDE

簡単だね！

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

Equalは同値比較ができるという性質があります。

そして*===*はその性質を利用した関数となります。

!SLIDE

# 論よりコード

```scala
scala> implicit lazy val ScalaChanEqual = new Equal[ScalaChan] {
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

# うれしいところ

* もとのデータに変更を加えることなく拡張が可能
* 必要最低限のものを定義するだけで高度な関数を利用可能
	* これには複数の性質を定義することが必要

!SLIDE

# 複数の性質を使う

*すべての要素にある値を加算する*

!SLIDE

# 普通に書く

```scala
scala> def mapAppend(s: Seq[Int])(i: Int) = s.map(_ + i)
mapAppend: (s: Seq[Int])(i: Int)Seq[Int]

scala> mapAppend(List(1, 2, 3))(5)
res0: Seq[Int] = List(6, 7, 8)

scala> mapAppend(Vector(1, 2, 3))(5)
res1: Seq[Int] = Vector(6, 7, 8)

scala> 
```

!SLIDE

Int以外にDoubleやStringも対応させたい。

結合二項演算がひつよう。

!SLIDE

# Semigroup

```scala
scala> def mapAppend[A: Semigroup](s: Seq[A])(a: A) = s.map(_ |+| a)
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

# Functor

```scala
scala> def mapAppend[M[_]: Functor, A: Semigroup](m: M[A])(a: A) = m.map(_ |+| a)
mapAppend: [M[_], A](m: M[A])(a: A)(implicit evidence$1: scalaz.Functor[M], implicit evidence$2: scalaz.Semigroup[A])M[A]

scala> mapAppend(List(1, 2, 3))(5)
res4: List[Int] = List(6, 7, 8)

scala> mapAppend(Option("Hello"))("World")
res5: Option[java.lang.String] = Some(HelloWorld)

scala> 
```

!SLIDE

# Scalazの基本

* 性質を定義する
* 性質を利用する

!SLIDE

ここまでで質問とか。

!SLIDE

# Scalazプログラミング！

!SLIDE

なにかいいものはないかとScalaのコードを漁っていたのですが、Scalazで書きやすい題材は見つからず・・・

!SLIDE

いろいろなScala関係の書籍を漁ってようやくたどり着いたのが・・・

!SLIDE

*[Real World Haskell](http://www.oreilly.co.jp/books/9784873114231/)*

![Real World Haskell](scalaz/picture_large978-4-87311-423-1.jpeg)

!SLIDE

具体例にJSONライブラリがあるので書いてみましょう。

!SLIDE

# [JSON](http://www.json.org/)データを表現する

```scala
sealed trait JValue

sealed abstract class AbstractJValue[A](value: A) extends NewType[A] with JValue

case class JString(value: String) extends AbstractJValue(value)

case class JNumber(value: Double) extends AbstractJValue(value)

case class JBoolean(value: Boolean) extends AbstractJValue(value)

case object JNull extends JValue

case class JObject(value: (JString, JValue)*) extends AbstractJValue(value)

case class JArray(value: JValue*) extends AbstractJValue(value)
```

!SLIDE

# [NewType](http://scalaz.github.com/scalaz/scalaz-2.9.1-6.0.2/doc/index.html#scalaz.NewType)

既存の型を拡張するものです。

!SLIDE

# NewTypeの定義

```scala
trait NewType[X] {
  val value: X

  override def toString =
    value.toString
}

object NewType {
  implicit def UnwrapNewType[X](n: NewType[X]): X = n.value
}
```

!SLIDE

# NewTypeの例

```scala
scala> case class MyInt(value: Int) extends NewType[Int]
defined class MyInt

scala> 2 + MyInt(1)
res61: Int = 3

scala>
```

!SLIDE

# JSONデータの表示

*JValue*をJSON形式で表示します。

普通は*toString*をオーバーライドしますが、ここでは*Show*を使って実装したいと思います。

!SLIDE

# renderJSON

```scala
lazy val renderJSON: JValue => String = {
  case JNumber(n) => n.shows
  case JString(s) => "\"%s\"".format(s)
  case JBoolean(b) => b.shows
  case JNull => "null"
  case JObject(o @ _*) => o.map(_.fold(renderJSON(_) + ": " + renderJSON(_))).mkString("{", ", ", "}")
  case JArray(a @ _*) => a.map(renderJSON).mkString("[", ", ", "]")
}
```

!SLIDE

**o**の型は(JString, JValue)*

本来Tupleにないはずの*fold*というメソッドを呼び出している。

!SLIDE

# *W

## 拡張するための型

Scalazには*"型名 + W"*という規則で名付けられた型があります。

これはその型を拡張するもので、標準ライブラリにあるものが定義されています。

!SLIDE

# fold

Tupleは*Tuple2W*から*Tuple12W*まで定義されており、そこにfoldが定義されています。

foldはTupleを取り、何らかの値を返す関数を渡します。

!SLIDE

# foldの例

```scala
scala> (1, 2).fold(_ + _)
res116: Int = 3

scala> (1, 2, 3).fold(_ + _ + _)
res117: Int = 6

```

!SLIDE

# Showのインスタンスを作る

```scala
implicit def JValueShow: Show[JValue] = shows(renderJSON)
```

!SLIDE

# *s

## 型クラスを定義するための型

型クラスを定義するには*Equal*の例であったように**new**を使ってインスタンスを作る他に、*"型クラス名 + s"*で定義された関数を使う方法があります。

!SLIDE

# 結果

```scala
scala> JObject(
     |   JString("age") -> JNumber(18),
     |   JString("language") -> JArray(JString("Scala"), JString("Clojure"), JString("Python"))
     | ): JValue
res105: JValue = WrappedArray((age,18.0), (language,WrappedArray(Scala, Clojure, Python)))

scala> res105.shows
res106: String = {"age": 18.0, "language": ["Scala", "Clojure", "Python"]}

```

!SLIDE

表示ができました！
