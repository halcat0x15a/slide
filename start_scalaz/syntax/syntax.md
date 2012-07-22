!SLIDE

# Syntax

!SLIDE

## 先の例は次の様に書ける

```scala
def double[A: Semigroup](a: A) = a |+| a
```

!SLIDE

# |+|

## SemigroupOpsに定義されてるメソッド

### Semigroupのインスタンスを持つ型に対して暗黙の型変換がされる

```scala
def double[A: Semigroup](a: A) = ToSemigroupOps(a) |+| a
```

!SLIDE

# scalaz.syntax

## インスタンスが存在すればOpsで定義されたメソッドが使える

### 主要な関数のほとんどはシンタックスが定義されている

```scala
def quote[A: Show](a: A) = Show[A].show(a).mkString("'", "", "'")
```

```scala
def quote[A: Show](a: A) = a.show.mkString("'", "", "'")
```
