!SLIDE

# 純粋な世界で生きるために

[halcat0x15a](https://twitter.com/halcat0x15a)

!SLIDE

## 基本的なアプリケーション

    入力 -> 計算 -> 出力

```clojure
(defn main []
  (->> (read-line) (str "hello") prn))
```

!SLIDE

## アプリケーションの要素

```clojure
(defn run [input]
  (str "hello" input))

(defn read []
  (read-line))

(defn write [result]
  (prn result))

(defn main []
  (->> (read) run write))
```

*副作用（外界との対話）は最小限に抑える*

!SLIDE

## イベントループ

    入力 -> 計算 -> 出力 -> 入力 -> 計算 -> 出力 -> ...

```clojure
(defn main []
  (loop [input (read)]
    (when-not (= input "quit")
      (-> input run write)
      (recur (read)))))
```

!SLIDE

## 状態

```clojure
(defn run [input buffer]
  (let [result (str buffer input)]
    [result result]))

(def init "")

(defn main []
  (loop [state init input (read)]
    (if-not (= input "quit")
      (let [[result state'] (run input state)]
        (write result)
        (recur state' (read))))))
```

!SLIDE

最低限の副作用

* 入力
* 出力
* イベントループ

!SLIDE

## 複数の状態に対応する

アプリケーションのモデルを考える

```clojure
(defrecord Editor [buffer cursor])

(defn split [{:keys [cursor buffer]}]
  [(subs buffer 0 cursor)
   (subs buffer cursor (count buffer))])

(defn show [editor]
  (let [[left right] (split editor)]
    (str left "\u20de" right)))

(defn append [input {:keys [cursor] :as editor}]
  (let [[left right] (split editor)]
    (-> editor
        (assoc :buffer (str left input right))
        (assoc :cursor (+ cursor (count input))))))
```

!SLIDE

```clojure
(defn run [input editor]
  (let [editor' (append input editor)
        result (show editor')]
    [result editor']))

(def init (Editor. "" 0))
```

!SLIDE

```clojure
(defn left [editor]
  (update-in editor [:cursor] dec))

(defn right [editor]
  (update-in editor [:cursor] inc))

(defn run [input editor]
  (let [editor'
        (case (first input)
          \< (left editor)
          \> (right editor)
          (append input editor))
        result (show editor')]
    [result editor']))
```

!SLIDE

## 計算は使い回しが可能

```clojure
(import [java.awt Frame FlowLayout Label TextField])
(import java.awt.event.ActionListener)

(defn main []
  (let [label (Label. "hello")
        field (TextField. 10)
        state (atom init)
        action
        (reify ActionListener
          (actionPerformed [this e]
            (let [[result editor]
                  (run (.getText field) @state)]
              (.setText label result)
              (reset! state editor))))]
    (doto (Frame.)
      (.setLayout (FlowLayout.))
      (.add (doto field (.addActionListener action)))
      (.add label)
      (.setSize 100 100)
      (.setVisible true))))
```

!SLIDE

*AWT, Swingは旧時代のものなのでJavaFX使おう*

!SLIDE

おわり
