!SLIDE

# Scalaz

!SLIDE

# パッケージ

* scalaz
    * 型クラスとデータ型
* scalaz.std
    * 標準ライブラリに対する型クラスのインスタンス
* scalaz.syntax
    * シンタックス

!SLIDE

# 命名規則

* Instances
    * データ型に対する型クラスのインスタンス
* Functions
    * データ型に関係した関数
* Ops
    * 型クラスに関係したメソッド
* Syntax
    * 暗黙の型変換

!SLIDE

# 型クラスのコンパニオンオブジェクト

* 型クラスに関係した関数
* applyはインスタンスを得る
* インスタンスを定義するための関数

```scala
def double[A: Semigroup](a: A) = Semigroup[A].append(a, a)
```

!SLIDE

# データ型のコンパニオンオブジェクト

## InstancesとFunctionsを継承している
