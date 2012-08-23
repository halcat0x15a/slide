!SLIDE

# State

!SLIDE

# 状態を持つ

## 状態 => (状態, 値)

```scala
case class Person(name: String, money: Int)

(for {
  person <- init[Person]
  _ <- modify[Person](_ copy (money = person.money + 100))
  person <- get
} yield person.money) eval Person("Sanshiro", 1000) assert_=== 1100
```

!SLIDE

# Lens

## フィールドに対するgetterとsetter

```scala
case class Person(name: String, money: Int)

object Person {
  val money: Lens[Person, Int] = Lens.lensg(p => m => p copy (money = m), _.money)
}

(for {
  money <- Person.money += 100
} yield money) eval Person("Sanshiro", 1000) assert_=== 1100
```

!SLIDE

# Example

```scala
def buy(book: Book) = Person.money -= book.price

(for {
  _ <- buy(Book("yuruyuri", 900))
  _ <- buy(Book("mathgirl", 1800))
  _ <- buy(Book("genshiken", 600))
  money <- buy(Book("mudazumo", 700))
} yield money) eval Person("Sanshiro", 5000) assert_=== 1000
```

!SLIDE

# 演習

## 先の例で所持金がマイナスになったらNoneを返すようにするため、check関数を定義せよ

```scala
def check: StateT[Option, Person, Unit]

(for {
  _ <- buy(Book("yuruyuri", 900)).lift[Option]
  _ <- buy(Book("mathgirl", 1800)).lift[Option]
  _ <- buy(Book("genshiken", 600)).lift[Option]
  _ <- buy(Book("mudazumo", 700)).lift[Option]
  _ <- check
  person <- get.lift[Option]
} yield person.money) eval Person("Sanshiro", 3000) assert_=== None
```
