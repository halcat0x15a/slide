!SLIDE

# ClojureScript

## in 残パン会

### 時間余ったからスライド作った。

!SLIDE

# 自己紹介

* よしださんしろう
* halcat0x15a
* 大学生
* Scala, Clojure, Pythonがメイン

!SLIDE

# 作ったもの

## Monadicな非同期通信ライブラリ

### 既存のライブラリをラップしたもの

!SLIDE

# 理由とか

* コールバック地獄が嫌なので
* 現在ClojureScriptを使ったサービスを作っているので
* こんなに素晴しいのにClojureScriptを誰も使ってないのが残念だから

!SLIDE

# 書いたコード

```clojure
(defn send [url method content headers timeout-interval]
  #(xhr-io/send url (fn [xhr] (% xhr.target)) method content headers timeout-interval))

(defn bind [f g]
  #(f (fn [x] ((g x) %))))

(defn fmap [f g]
  #(f (fn [x] (% (g x)))))
```

!SLIDE

# 書いたコード

```clojure
(defmacro do-m [b m]
  (let [[[fk fv] & t] (reverse (partition 2 b))
        f #(list %1 %2 (list 'fn (vector %3) %4))]
    (reduce (fn [l [k v]] (f 'onedit.core/bind v k l)) (f 'onedit.core/fmap fv fk m) t)))
```

!SLIDE

# 書いたコード

```clojure
(defn test-syntax []
  (syntax/do-m [a (core/fmap (core/send "lexers") #(.getResponseText %))
                b (core/fmap (core/send "lexers") #(.getResponseText %))]
               (+ a.length b.length)))
```

これだけ

!SLIDE

# 比較とか

## JavaScript

```javascript
goog.net.XhrIo.send(
  "geso",
  function (x) {
    goog.net.XhrIo.send(
      x.getResponseText(),
      function (x) {
        console.log(x)
      }
    )
  }
)
```

!SLIDE

## ClojureScript

```clojure
(xhr-io/send
  "geso"
  (fn [x]
    (xhr-io/send
      (x.getResponseText x)
      #(.log js/console %))))
```

!SLIDE

## JavaScript(Monadic)

```javascript
onedit.core.bind(onedit.core.send("geso"), function (x) {
  onedit.core.send(x.getResponseText())
})(console.log)
```

!SLIDE

## ClojureScript(Monadic)

```clojure
((syntax/do-monad
  [x (core/send "geso")
   y (core/send (.getResponseText x))] y)
 #(.log js/console %))
```

!SLIDE

# 動かしながらの解説とか

!SLIDE

# ありがとうございました
