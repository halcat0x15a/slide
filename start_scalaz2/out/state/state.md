!SLIDE

# State

!SLIDE

# Lens

## フィールドに対するgetterとsetter

```scala
case class Person(name: String, property: List[String], money: Int)

object Person {

  val property: Lens[Person, List[String]] =
    Lens.lensu((p, pr) => p copy (property = pr), _.property)

  val money: Lens[Person, Int] =
    Lens.lensg(p => m => p copy (money = m), _.money)

}
```

!SLIDE

# 演習

> たかしくんは400円を持って80円のりんごを2つ、60円のみかんを3つ買いました。
> この時のたかしくんの所持金を求めなさい。
> ただし、計算にREPLを使ってよいものとする。

```scala
def buy(thing: String, price: Int): State[Person, Unit] =
  for {
    _ <- Person.property %= (thing :: _)
    _ <- Person.money -= price
  } yield ()

(for {
  _ <- buy("apple", 80)
  _ <- buy("apple", 80)
  _ <- buy("orange", 60)
  _ <- buy("orange", 60)
  _ <- buy("orange", 60)
  takashi <- get
} yield takashi.money) eval Person("takashi", Nil, 400)
```

!SLIDE

# State

## 状態 => (状態, 値)

```scala
(for {
  _ <- Person.property := List("orange")
  property <- Person.property %= ("apple" :: _)
  money <- Person.money -= 80
} yield property -> money) eval
  Person("takashi", Nil, 100) assert_=== List("apple", "orange") -> 20
```

!SLIDE

# Example

```scala
def buy(book: Book): State[Person, Unit] =
  buy(book.title, book.price)

(for {
  _ <- buy(Book("yuruyuri", 900))
  _ <- buy(Book("mathgirl", 1800))
  _ <- buy(Book("genshiken", 600))
  _ <- buy(Book("mudazumo", 700))
  sanshiro <- get
} yield sanshiro.money) eval
  Person("Sanshiro", Nil, 5000) assert_=== 1000
```

!SLIDE

# StateT

```scala
def check = StateT[Option, Person, Unit](p => p.money >= 0 option p -> ())

(for {
  money <- (Person.money -= 80).lift[Option]
  _ <- check
} yield money) eval
  Person("takashi", Nil, 100) assert_=== Some(20)
```

!SLIDE

# 演習

## 所持金がマイナスになったらNoneを返すようなbuyAndCheck関数を定義せよ

```scala
def buyAndCheck(book: Book): StateT[Option, Person, Unit]

(for {
  _ <- buyAndCheck(Book("yuruyuri", 900))
  _ <- buyAndCheck(Book("mathgirl", 1800))
  _ <- buyAndCheck(Book("genshiken", 600))
  _ <- buyAndCheck(Book("mudazumo", 700))
  person <- get.lift[Option]
} yield person.money) eval
  Person("Sanshiro", Nil, 3000) assert_=== None
```
