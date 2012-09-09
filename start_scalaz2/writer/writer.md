!SLIDE

# Writer

!SLIDE

# 計算を記録する

## (記録値, 計算値)

```scala
(2 set "geso" run) assert_=== "geso" -> 2
(2 set "geso" value) assert_=== 2
(2 set "geso" written) assert_=== "geso"
"geso".tell.written assert_=== "geso"
```

!SLIDE

# 計算の過程を記録する

```scala
(for {
  _ <- "start;".tell
  a <- 2 set "a = 2;"
  b = a + 2
  _ <- s"a + 2 = $b;".tell
  _ <- "end;".tell
} yield b).run assert_===
  "start;a = 2;a + 2 = 4;end;" -> 4
```

!SLIDE

# 合成する時にSemigroupを利用する

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
