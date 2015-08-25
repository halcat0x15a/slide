# JavaのListをScalaのListにするはなし

```scala
val xs: List[Int] = java.util.Arrays.asList(1, 2, 3)
```

[halcat0x15a](https://twitter.com/halcat0x15a)



## java.util.List

Javaのライブラリによくある

Scalaからは使いづらい



## JavaConvertersとJavaConversions

JavaとScalaのコレクションを相互に変換するモジュール

基本的にはJavaConvertersを使おう

```scala
import scala.collection.JavaConverters._

java.util.Arrays.asList(1, 2, 3).asScala
```



## collection.mutable.Buffer

java.util.ListはBufferにラップされる

immutableなListにするにはtoList

```scala
val xs: java.util.List[Int] = java.util.Arrays.asList(1, 2, 3)

val ys: List[Int] = xs.asScala.toList
```



## java.util.List[Integer]とList[Int]

JavaのAPIはjava.util.List[Integer]を返す

List[Int]として扱いたいときはasInstanceOf

```scala
val xs: java.util.List[Integer] = java.util.Arrays.asList(1, 2, 3)

val ys: List[Int] = xs.asScala.toList.asInstanceOf[List[Int]]
```



## java.util.List[Integer]とList[String]

Scalaのコレクションはmapが使える

```scala
val xs: java.util.List[Integer] = java.util.Arrays.asList(1, 2, 3)

val ys: List[String] = xs.asScala.map(_.toString).toList
```

しかしBuffer[String]が作られてしまう



## scala.collection.breakOut

breakOutを使うと中間オブジェクトが作られない

```scala
val xs: java.util.List[Integer] = java.util.Arrays.asList(1, 2, 3)

val ys: List[String] = xs.asScala.map(_.toString)(scala.collection.breakOut)
```



# まとめ

以下の2つは覚えておくとよいでしょう

* scala.collection.JavaConverters
* scala.collection.breakOut
