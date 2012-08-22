!SLIDE

# Writer

!SLIDE

# 計算を記録するモナド

## 半群の性質を利用する

### (記録値, 計算値)

```scala
(2 set "" run) assert_=== Writer("", 2).run
(2 set "" value) assert_=== 2
(2 set "" written) assert_=== ""
```

!SLIDE

# 計算の過程を記録する

## (((((2) + 2) * 2) - 2) / 2)

```scala
(for {
  a <- 2 set mzero[List[Int]]
  b <- a + 2 set a.point[List]
  c <- b * 2 set b.point[List]
  d <- c - 2 set c.point[List]
  e <- d / 2 set d.point[List]
} yield e).run assert_=== List(2, 4, 8, 6) -> 3
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
