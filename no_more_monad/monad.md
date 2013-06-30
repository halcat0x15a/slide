!SLIDE

# モナドなんていらない!

[@halcat0x15a](https://twitter.com/halcat0x15a)

!SLIDE

Clojureには(重要)モナドがいらない

* モナドの為の制御構造が存在しない
* 標準ライブラリはモナドを意識していない

!SLIDE

じゃあどうやってプログラミングすればいいんだ？(重症なモナド脳)

!SLIDE

# モナドといえば

* 手続き型的な記述
* 継続を扱える
* コンテキストを持つ

これをClojureでどうやる？

!SLIDE

# Threading Macro

```clojure
(->> (read) (str "Hello, ") prn)
```

* 括弧が減る
* ネストしない
* 読み易い
* いくつか種類がある

!SLIDE

# 継続の破棄

計算の途中でnilが返ったらその後の計算を破棄したい

```clojure
(->
  {:foo 1}
  (get :bar) ; nil
  inc)       ; NPE!
```

こんなときはsome->やsome->>を使う

```clojure
(some->
  {:foo 1}
  (get :bar)
  inc)       ; nil
```

!SLIDE

しかし、nilには情報を付与できない

例外を使ってex-infoを投げるのが無難

```clojure
(defn get' [m k]
  (if-let [v (get m k)]
    v
    (throw (ex-info (str "key not found: " k) {:key k}))))

(try
  (-> {:foo 1} (get' :bar) inc)
  (catch clojure.lang.ExceptionInfo e
    (-> e ex-data :key)))
```

!SLIDE

# 分岐

手続きを破棄するだけでなく、選択をすることも可能

```clojure
(defn fizzbuzz [n] 
  (or
    (cond-> nil
      (zero? (mod n 3)) (str "Fizz")
      (zero? (mod n 5)) (str "Buzz"))
    n))

(map fizzbuzz (range 1 16))
```

!SLIDE

# 難しいこと

* 計算値の束縛
    * 多値を返す
    * 関数をうまく分割する
* 暗黙の状態
    * マクロを使う

!SLIDE

複雑な計算の組み立てにはThreading Macroを!
