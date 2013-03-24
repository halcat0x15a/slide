# clojure.zipを使ったHistoryの表現

## Stackを使ったHistory

よくある履歴機能の実装として、スタックを使ったものがあります。

```clojure
(defrecord History [undo redo current])

(def history (partial ->History [] []))
```

* commitはcurrentに新しい値を設定します。
* undo, redoはstackの先頭から値を取り出し、currentに設定します。

```clojure
(defn commit [value {:keys [undo current] :as history}]
  (assoc history
    :current value
    :undo (conj undo current)
    :redo []))

(defn undo [{:keys [undo redo current] :as history}]
  (if-let [value (peek undo)]
    (assoc history
      :current value
      :undo (pop undo)
      :redo (conj redo current))))

(defn redo [{:keys [undo redo current] :as history}]
  (if-let [value (peek redo)]
    (assoc history
      :current value
      :undo (conj undo current)
      :redo (pop redo))))
```

undo, redoの操作は抽象化可能です。

```clojure
(defmulti inverse identity)
(defmethod inverse :undo :redo)
(defmethod inverse :redo :undo)

(defn return [stack {:keys [current] :as history}]
  (if-let [value (-> history stack peek)]
    (let [stack' (inverse stack)]
      (assoc history
        :current value
        stack (-> history stack (conj current))
        stack' (-> history stack' pop)))))

(def undo (partial return :undo))
(def redo (partial return :redo))
```

実際の動作を覗きます。

```clojure
(defn peep [f obj]
  (prn obj)
  (f obj))

(->> (history "foo")
     (commit "bar")
     (commit "baz")
     (peep undo)
     (peep undo)
     (peep redo)
     (peep redo)
     prn)
```

以下のような出力が得られます。

```
#user.History{:undo ["foo" "bar"], :redo [], :current "baz"}
#user.History{:undo ["foo"], :redo ["baz"], :current "bar"}
#user.History{:undo [], :redo ["baz" "bar"], :current "foo"}
#user.History{:undo ["foo"], :redo ["baz"], :current "bar"}
#user.History{:undo ["foo" "bar"], :redo [], :current "baz"}
```

実にシンプルな実装ですね。
ブラウザのGo back, Go forwardや、テキストエディタのUndo, Redoはこのような動作をするものが多いと思います。

## Zipperを使ったHistory

しかし、先の実装ではcommitのたびにredoが初期化されるので、変更が消えてしまうことがあります。

```clojure
(->> (history "foo")
     (commit "bar")
     undo
     (commit "baz"))
; => #user.stack.History{:undo ["foo"], :redo [], :current "baz"}
```

そこで、全ての変更を残し、辿ることを可能にするため、Historyを木構造で表し、Zipperで操作します。

### clojure.zip/zipper

clojure.zipはZipperを扱うためのAPIです。
Zipperの構築にはclojure.zip/zipperを使います。
この関数は少々複雑です。

```
Usage: (zipper branch? children make-node root)
```

* branch?
    * Zipperがfocusする値がブランチかどうかを判別する関数。
* children
    * Zipperを構成する値から子ノードのシーケンスを取り出す関数。
* make-node
    * Zipperがfocusする値と子ノードのシーケンスからZipperを構成する値を返す関数。
* root
    * Zipperを構成する値。

例として、clojure.zip/vector-zipとclojure.zip/seq-zipの実装を挙げます。
実際にはmetadataを付加するコードが含まれます。

```clojure
(defn vector-zip [root]
  (zipper vector? seq (fn [node children] (vec children)) root))

(defn seq-zip [root]
  (zipper seq? identity (fn [node children] children) root))
```

clojure.zip/zipperを用いたHistoryは以下のようになります。

```clojure
(require '[clojure.zip :as zip])

(defprotocol History
  (branch? [history])
  (children [history])
  (make-node [history list]))

(defrecord Change [list value]
  History
  (branch? [change] true)
  (children [change] list)
  (make-node [change list]
    (assoc change :list list)))

(def change (partial ->Change []))

(def history (comp (partial zip/zipper branch? children make-node) change))

(defn commit [value history]
  (-> history (zip/insert-child (change value)) zip/down))
```

動作を見てみましょう。

```clojure
(defn peep [f obj]
  (-> obj zip/node prn)
  (f obj))

(->> (history "foo")
     (commit "bar")
     (commit "baz")
     (peep zip/up)
     (peep zip/up)
     (peep zip/down)
     (peep zip/down)
     zip/node
     prn)
```

以下のような出力が得られます。

```clojure
#user.zipper.Change{:list [], :buffer "baz"}
#user.zipper.Change{:list (#user.zipper.Change{:list [], :buffer "baz"}), :buffer "bar"}
#user.zipper.Change{:list (#user.zipper.Change{:list (#user.zipper.Change{:list [], :buffer "baz"}), :buffer "bar"}), :buffer "foo"}
#user.zipper.Change{:list (#user.zipper.Change{:list [], :buffer "baz"}), :buffer "bar"}
#user.zipper.Change{:list [], :buffer "baz"}
```

コミットが消えてしまう問題がどのように解決されたか見てみましょう。

```clojure
(-> (->> (history "foo")
         (commit "bar")
         zip/up
         (commit "baz"))
    zip/up
    zip/children)
; => (#user.zipper.Change{:list [], :buffer "baz"} #user.zipper.Change{:list [], :buffer "bar"})
```

変更が並行に存在することが確認できました。

## 雑感

clojure.zip/zipperは実際複雑。
REPLでいじりながら理解するといいかもしれません。

ZipperはListやVectorのようなシーケンスだけでなく、Treeのように深さがあるようなデータ構造も扱えます。
様々なデータ構造に適用できるclojure.zipの抽象化は新鮮でした。
