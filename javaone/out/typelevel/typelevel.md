!SLIDE

# Scala

## 型レベルプログラミング（仮）

!SLIDE

# 自己紹介

* よしださんしろう
* halcat0x15a
* 大学生になりました
* Scala歴2年くらい
* JVM言語好き関数型言語好き

!SLIDE

# 今日やること

* 自然数を定義する
* n <= mを定義する
* n <= mが真の時のみコンパイルできる関数を定義する

!SLIDE

# 自然数

```scala
abstract class Nat
```

```java
abstract class Nat {}
```

!SLIDE

# 0

```scala
abstract class _0 extends Nat
val _0 = new _0 {}
```

```java
abstract class _0 extends Nat {}
final _0 _0 = new _0() {};
```

!SLIDE

# n + 1

```scala
case class S[N <: Nat](n: N) extends Nat
```

```java
final class S<N extends Nat> extends Nat {
    public final N n;
    public S(N n) {
        this.n = n;
    }
}
```

!SLIDE

# 1, 2

```scala
val _1 = S(_0)
val _2 = S(_1)
```

```java
final S<_0> _1 = new S<>(_0);
final S<S<_0>> _2 = new S<>(_1);
```

!SLIDE

# n <= m

```scala
abstract class <=[N <: Nat, M <: Nat]
```

```java
abstract class LE<N extends Nat, M extends Nat> {}
```

!SLIDE

# Check

```scala
def check[N <: Nat, M <: Nat](n: N, m: M)(implicit ev: N <= M) {}
```

```java
<N extends Nat, M extends Nat> void check(final N n, final M m, final LE<N, M> ev) {}
```

!SLIDE

# =

```scala
implicit def e[N <: Nat]: N <= N = new <=[N, N] {}
```

```java
<N extends Nat> LE<N, N> e() {
    return new LE<N, N>() {};
}
```

!SLIDE

```scala
check(_0, _0)(e[_0])
check(_1, _1)(e[_1])
check(_2, _2)(e[_2])
```

!SLIDE

# <

```scala
implicit def l[N <: Nat, M <: Nat](implicit ev: N <= M): N <= S[M] = new <=[N, S[M]] {}
```

```java
<N extends Nat, M extends Nat> LE<N, S<M>> l(final LE<N, M> ev) {
    return new LE<N, S<M>>() {};
}
```

!SLIDE

```scala
check(_0, _1)(l[_0, _0](e[_0]))
check(_0, _2)(l[_0, _1](le[_0, _0](e[_0])))
check(_1, _2)(l[_1, _1](e[_1]))
```

!SLIDE

ご清聴ありがとうございました。
