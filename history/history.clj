(ns user.stack)

(defrecord History [undo redo current])

(def history (partial ->History [] []))

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

(defmulti inverse identity)
(defmethod inverse :undo [_] :redo)
(defmethod inverse :redo [_] :undo)

(defn return [stack {:keys [current] :as history}]
  (if-let [value (-> history stack peek)]
    (let [stack' (inverse stack)]
      (assoc history
        :current value
        stack (-> history stack pop)
        stack' (-> history stack' (conj current))))))

(def undo (partial return :undo))
(def redo (partial return :redo))

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

(->> (history "foo")
     (commit "bar")
     undo
     (commit "baz")
     prn)

(ns user.zipper)

(require '[clojure.zip :as zip])

(defprotocol History
  (branch? [history])
  (children [history])
  (make-node [history list]))

(defrecord Change [list buffer]
  History
  (branch? [_] true)
  (children [_] list)
  (make-node [change list]
    (assoc change :list list)))

(def change (partial ->Change []))

(def history
  (comp (partial zip/zipper branch? children make-node) change))

(defn commit [buffer history]
  (-> history (zip/insert-child (change buffer)) zip/down))

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

(-> (->> (history "foo")
         (commit "bar")
         zip/up
         (commit "baz"))
    zip/up
    zip/children
    prn)
