!SLIDE

# Tagged Types

!SLIDE

# [Tag](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.Tag$)

## 既存の型を別の型として定義する

```scala
sealed trait Author
sealed trait Title

case class Book(title: String @@ Title, author: String @@ Author)

val book = Book(Tag("Programming in Scala"), Tag("Martin Odersky"))
/* book.copy(title = book.author) */ // compile error
```

!SLIDE

# newtype

## Scalaz6ではPimp my Library Patternが用いられてる

### Scalaz7ではTagged Typesを用いる

```scala
import scalaz.Tags._
3 |+| 3 assert_=== 6
(Multiplication(3) |+| Multiplication(3): Int) assert_=== 9
(Conjunction(true) |+| Conjunction(false): Boolean) assert_=== false
(Disjunction(true) |+| Disjunction(false): Boolean) assert_=== true
import scalaz.Dual._
(Dual("hello") |+| Dual("world"): String) assert_=== "worldhello"
```

!SLIDE

# [UnionTypes](http://halcat0x15a.github.com/scalaz/core/target/scala-2.9.2/api/index.html#scalaz.UnionTypes$)

## Eitherとは違い、コンテナで包む必要がない

```scala
def size[A](a: A)(implicit ev: A Contains t[Int]#t[String]#t[List[_]]) = a match {
  case i: Int => i
  case s: String => s.length
  case l: List[_] => l.size
}
size(1) assert_=== 1
size("geso") assert_=== 4
size(List(1, 2, 3)) assert_=== 3
/* size(1L) */ // compile error
```
