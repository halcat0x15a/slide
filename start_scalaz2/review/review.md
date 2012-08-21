!SLIDE

# 前回の復習

!SLIDE

# Type Class

```scala
def double[A: Semigroup](a: A) = a |+| a
```

* implicit parameterによるインスタンスの供給
* implicit conversionによるシンタックスの供給

!SLIDE

# 重要な型クラスと関数

* Show
    * shows
* Semigroup
    * |+|
* Monoid
    * mzero
* Equal
    * ===
    * =/=
* Functor
    * map
* Applicative
    * |@|
* Monad
    * >>=

!SLIDE

# パッケージと命名規則

[前回資料参照](http://halcat0x15a.github.com/slide/start_scalaz/out/#16)
