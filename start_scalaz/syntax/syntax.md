!SLIDE

# Syntax

!SLIDE

## 先の例は次の様に書ける

```scala
def double[A: Semigroup](a: A) = a |+| a
```

!SLIDE

# scalaz.syntax

## 型クラス名+Opsで定義されたメソッドが使える

### 主要な関数のほとんどはシンタックスが定義されている
