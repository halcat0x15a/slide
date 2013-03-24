!SLIDE

# Scheme on Clojure

[halcat0x15a](https://twitter.com/halcat0x15a)

!SLIDE

Clojureの特徴を簡単なScheme評価機を作る過程で紹介します。

!SLIDE

# 評価機

この評価機は、以下のような動作をします。

```clojure
(def env (atom {'+ +}))

(eval env '(define double (lambda (x) (+ x x))))
(eval env '(define foo (double (double 3))))
(assert (= (@env 'foo) 12))
```

!SLIDE

# Atom

Clojureはdefによりvarに値を束縛することが出来る。

しかし、通常、他のLispのようにset!による代入は出来ない。

ここでは、可変参照としてatomを使用する。

```clojure
(def a (atom 1))
(assert (= @a 1))
(reset! a 2)
(assert (= @a 2))
(swap! a inc)
(assert (= @a 3))
```

!SLIDE

# 自己評価式

文字列や数値やなど。

```clojure
(defn self-evaluating? [exp]
  (or (true? exp)
      (false? exp)
      (number? exp)
      (string? exp)))

(defn eval [env exp]
  (cond (self-evaluating? exp) exp))

(assert (= (eval (atom {}) 100) 100))
(assert (= (eval (atom {}) "foo") "foo"))
```

!SLIDE

# 変数の探索

環境はhash-mapで表現している。

```clojure
(defn eval [env exp]
  (cond (self-evaluating? exp) exp
        (symbol? exp) (@env exp)))

(assert (= ({'foo 100} 'foo) 100))
(assert (= (eval (atom {'foo 100}) 'foo)) 100)
```

!SLIDE

# 特殊形式

特殊形式の判別はリストの先頭を比較する必要がある。

ClojureのMultimethodを用いて、evalを変更することなく制御構造を追加する。

```clojure
(defmulti eval-form (fn [env exp] (first exp)))

(defn eval [env exp]
  (cond (self-evaluating? exp) exp
        (symbol? exp) (@env exp)
        (seq? exp) (eval-form env exp)))
```

!SLIDE

# Multimethod

defmultiでdispatch関数を定義し、defmethodにより対応する値と手続きを定義します。

単純な比較ではなく、hierarchyも考慮される。

```clojure
(defmulti foo (fn [x] x))
(defmethod foo 'foo [x] 100)
(defmethod foo 'bar [x] "bar")
(defmethod foo :default [x] x)

(assert (= (foo 'foo) 100))
(assert (= (foo 'bar) "bar"))
(assert (= (foo 'baz) 'baz))
```

!SLIDE

# quote, if

式の分解には分配束縛を用いる。

```clojure
(defmethod eval-form 'quote [env [_ quotation]] quotation)

(defmethod eval-form 'if [env [_ predicate consequent alternative]]
  (if (eval predicate)
    (eval consequent)
    (eval alternative)))

(assert (= (eval (atom {}) '(quote (foo bar))) '(foo bar)))
(assert (= (eval (atom {}) '(if false "foo" 100)) 100))
```

!SLIDE

# define

無名関数(fn [x] (f x))を#(f %)と記述できる。

```clojure
(defmethod eval-form 'define [env [_ name body]]
  (swap! env #(assoc % name (eval env body))))

(def env (atom {}))
(eval env '(define foo 100))
(assert (= (eval env 'foo) 100))
```

!SLIDE

# begin

&を使うことで複数の値をseqとして束縛出来る。

```clojure
(defmethod eval-form 'begin [env [& exps]]
  (->> exps (map (partial eval env)) last))

(assert (= (eval (atom {}) '(begin "foo" 100)) 100))
(assert (= (eval (atom {}) '(begin (define bar "bar") bar)) "bar"))
```

!SLIDE

# Arrow

利点

* データの流れが分り易い
* ネストが無くなる
* 括弧が減る

```clojure
(assert (= (-> [] (conj "foo") (conj "bar") first)
           (first (conj (conj [] "foo") "bar"))))

(assert (= (->> (range 10) (filter odd?) reverse)
           (reverse (filter odd? (range 10)))))
```

!SLIDE

# Apply

この評価機では２種類の関数が存在する。

* 評価機で定義した関数
* Clojureの関数

これらに対してProtocolを定義する。

```clojure
(defprotocol Procedure
  (appl [f args]))

(defmethod eval-form :default [env [operator operands]]
  (appl (eval operator) (map eval operands)))
```

!SLIDE

# Protocol, Type, Record

ProtocolはJavaのinterfaceと相違ない。

TypeやRecordは定義時にProtocolを実装することができる。

```clojure
(defprotocol Foo
  (foo [x]))

(deftype Bar [value]
  Foo
  (foo [x] value))

(defrecord Baz []
  Foo
  (foo [x] "baz"))

(assert (= (foo (Bar. 100)) 100))
(assert (= (foo (Bar. "bar")) "bar"))
(assert (= (foo (Baz.)) "baz"))
```

!SLIDE

# lambda

仮引数と実引数のペアで環境を拡張する。

```clojure
(deftype Lambda [env parameters body]
  Procedure
  (appl [lambda args]
    (eval (atom (merge @env (zipmap parameters args))) body)))

(defmethod eval-form 'lambda [env [_ parameters body]]
  (Lambda. env parameters body))

(assert (= (eval (atom {}) '((lambda (x y) y) "foo" "bar")) "bar"))
```

!SLIDE

# primitive

extend-protocolにより既存のデータ型に対してProtocolの実装が可能。

```clojure
(import 'clojure.lang.IFn)

(extend-protocol Procedure
  IFn
  (appl [f args] (apply f args)))

(assert (= (eval (atom {'+ +}) '(+ 2 3)) 5))
```

!SLIDE

これで目標とする評価機が完成した。

!SLIDE

# ClojureScript

Clojureのみで書かれているので、JavaScriptにコンパイルが可能...

な筈だがextend-protocolの部分を書き換える必要がある。

```clojure
(extend-protocol Procedure
  js/Function
  (appl [f args] (apply f args)))
```

!SLIDE

# まとめ

以下の言語機能を紹介した。

* Multimethod
* Destructuring
* Arrow
* Procotol
