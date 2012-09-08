!SLIDE

# Writer

!SLIDE

# 計算を記録する

## (記録値, 計算値)

```scala
(2 set "" run) assert_=== Writer("", 2).run
(2 set "" value) assert_=== 2
(2 set "" written) assert_=== ""
"".tell.written assert_=== ""
```

!SLIDE

# 計算の過程を記録する

## 合成時にSemigroupを利用する

```scala
(for {
  _ <- NonEmptyList("start").tell
  a <- 2 set NonEmptyList("a = 2")
  b = a + 2
  _ <- NonEmptyList(s"a + 2 = $b").tell
  _ <- NonEmptyList("end").tell
} yield b).run assert_=== NonEmptyList(
  "start",
  "a = 2",
  "a + 2 = 4",
  "end"
) -> 4
```

!SLIDE

# Applicative Style

```scala
case class Book(title: String, price: Int)

def buy(book: Book) = book.title set book.price

(buy(Book("yuruyuri", 900)) |@|
 buy(Book("mathgirl", 1800)) |@|
 buy(Book("genshiken", 600)) |@|
 buy(Book("mudazumo", 700)))(_ :: _ :: _ :: _ :: Nil).run assert_===
4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo")
```
