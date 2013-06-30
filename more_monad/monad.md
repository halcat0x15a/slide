!SLIDE

# モナドを使おう!

[@halcat0x15a](https://twitter.com/halcat0x15a)

!SLIDE

やっぱりClojureでもモナドを使いたい

* 汎用的な制御構造
* シンプルなインターフェース

!SLIDE

# モナド内包表記

簡単な変換規則が存在する

```clojure
(= (for-m [a ma] (f a))
   (fmap ma (fn [a] (f a))))
(= (for-m [a ma b mb] (f a b))
   (bind ma (fn [a] (fmap mb (fn [b] (f a b))))))
(= (for-m [a ma b mb c mc] (f a b c))
   (bind ma (fn [a] (bind mb (fn [b] (fmap mc (fn [c] (f a b c))))))))
```

!SLIDE

defmacroで簡単に書ける

```clojure
(defmacro for-m [[var val & exprs] expr]
  (if (empty? exprs)
    `(fmap ~val (fn [~var] ~expr))
    `(bind ~val (fn [~var] (for-m ~exprs ~expr)))))
```

!SLIDE

# 多相性

fmap, bindは多相的な関数でなければならない

やりかたはいくつかある

* metadata
* multimethod
* protocol

!SLIDE

# metadata

```clojure
(defn fmap [m f]
  (vary-meta ((-> m meta :fmap) f m) merge (meta m)))

(defn point-seq [seq] (with-meta seq {:fmap map}))
(defn point-vec [vec] (with-meta vec {:fmap mapv}))

(for-m [a (point-seq '(1 2 3))] (inc a))
(for-m [a (point-vec [1 2 3])] (inc a))
```

!SLIDE

# 特徴

* 最初にmetadataを付与しなければならない
    * 1回付与すれば伝搬する
* IObjでなければならない
* metadataを変更するだけで動作を変えられる
* metadataを消されると壊れる

!SLIDE

# multimethod

```clojure
(defmulti fmap (fn [m f] (type m)))
(defmethod fmap clojure.lang.ISeq [m f] (map f m))
(defmethod fmap nil [m f] nil)

(for-m [a '(1 2 3)] (inc a))
(for-m [a nil] (inc a))
```

!SLIDE

# 特徴

* dispatch関数を自由に設定可能
    * しかし、広く適用出来るものに限る
* アドホックな定義が可能
* defaultの動作も定義出来る
* classやkeywordを使えば階層も考慮される

!SLIDE

# protocol

```clojure
(defprotocol Monad
  (bind [m f]))
(defprotocol Functor
  (fmap [m f]))
(extend-protocol Functor
  clojure.lang.ISeq
  (fmap [m f] (map f m))
  nil
  (fmap [m f] nil))

(for-m [a '(1 2 3)] (inc a))
(for-m [a nil] (inc a))
```

!SLIDE

# 特徴

* multimethodの利点の多くを受け継ぐ
* Java特有の制約がある
    * 可変長引数が使えない
    * 第一引数は固定
* 高速

!SLIDE

# どれがよいか

型による振り分けならばprotocolを使うのがよい

multimethodは値によるdispatchに限る

!SLIDE

# ハイパーモナドタイム

モナドを使った魅力的な関数の紹介

!SLIDE

# bind

データ型によって動作を変えられる汎用的なスレッド

```clojure
(extend-type java.lang.Object
  Functor
  (fmap [m f] (f m))
  Monad
  (bind [m f] (f m)))

(defn >>= [m & fs] (reduce bind m fs))

(>>= 1 inc inc (fn [x] (* x x)))
```

!SLIDE

# sequence

モナドのリストをリストをもつモナドにする

```clojure
(defn sequence [m & ms]
  (reduce (fn [m n] (bind m #(fmap n (partial conj %)))) (fmap m vector) ms))

(extend-protocol Monad
  clojure.lang.ISeq
  (bind [m f] (mapcat f m))
  nil
  (bind [m f] nil))

(sequence '(1 2 3) '(2 4 6))
(sequence 1 2 nil 3)
```

!SLIDE

# lift

モナドの持つ値を関数に適用する

```clojure
(defn lift [f m & ms]
  (fmap (apply sequence m ms) (partial apply f)))

(lift + 1 2 3)
(lift + 1 2 nil 3)
(lift list '(1 2 3) '(2 4 6))
(lift + '(1 2 3) '(2 4 6))
```

!SLIDE

これだけ有用な関数がfmapとbindを定義するだけで使える

今日の例は[emerald](https://github.com/halcat0x15a/emerald/)に実装されています

!SLIDE

おわり
