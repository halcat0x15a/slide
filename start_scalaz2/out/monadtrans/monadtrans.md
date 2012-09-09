!SLIDE

# Monad Trans

!SLIDE

# モナド変換子

## 入れ子になったモナドを扱う仕組み

### 複数のモナドを合成する

!SLIDE

# WriterとOption

```scala
def buy(book: Book): Option[Writer[Int, String]] =
  book.price < 2000 option (book.title set book.price)

(for {
  a <- buy(Book("yuruyuri", 900))
  b <- buy(Book("mathgirl", 1800))
  c <- buy(Book("genshiken", 600))
  d <- buy(Book("mudazumo", 700))
} yield (for {
  e <- a
  f <- b
  g <- c
  h <- d
} yield List(e, f, g, h)).run) assert_===
Some(4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo"))
```

!SLIDE

# Applicative Style

```scala
(buy(Book("yuruyuri", 900)) |@|
 buy(Book("mathgirl", 1800)) |@|
 buy(Book("genshiken", 600)) |@|
 buy(Book("mudazumo", 700)))(
   _ |@| _ |@| _ |@| _ |> (_(_ :: _ :: _ :: _ :: Nil).run)) assert_===
Some(4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo"))
```

!SLIDE

# WriterT Option

```scala
def buy(book: Book): WriterT[Option, Int, String] =
  WriterT(book.price < 2000 option book.price -> book.title)

(buy(Book("yuruyuri", 900)) |@|
 buy(Book("mathgirl", 1800)) |@|
 buy(Book("genshiken", 600)) |@|
 buy(Book("mudazumo", 700)))(_ :: _ :: _ :: _ :: Nil).run assert_===
Some(4000 -> List("yuruyuri", "mathgirl", "genshiken", "mudazumo"))

(buy(Book("yuriyuri", 900)) |@|
 buy(Book("programming in scala", 4800)) |@|
 buy(Book("ubunchu", 800)))(_ :: _ :: _ :: Nil).run assert_===
None
```

!SLIDE

# Writer = WriterT Id

## Scalazのデータ型の殆どはモナド変換子

### モナド変換子を新たに定義するのではなく、モナド変換子によって定義される
