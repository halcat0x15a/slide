(ns evaluator
  (:refer-clojure :exclude [eval])
  (:import clojure.lang.IFn))

(defn self-evaluating? [exp]
  (or (true? exp)
      (false? exp)
      (number? exp)
      (string? exp)))

(defmulti eval-form (fn [env exp] (first exp)))

(defn eval [env exp]
  (cond (self-evaluating? exp) exp
        (symbol? exp) (@env exp)
        (seq? exp) (eval-form env exp)))

(defmethod eval-form 'quote [env [_ quotation]] quotation)

(defmethod eval-form 'if [env [_ predicate consequent alternative]]
  (if (eval env predicate)
    (eval env consequent)
    (eval env alternative)))

(defmethod eval-form 'define [env [_ name body]]
  (swap! env #(assoc % name (eval env body))))

(defmethod eval-form 'begin [env [& exps]]
  (->> exps (map (partial eval env)) last))

(defprotocol Procedure
  (appl [f args]))

(defmethod eval-form :default [env [operator & operands]]
  (appl (eval env operator) (map (partial eval env) operands)))

(deftype Lambda [env parameters body]
  Procedure
  (appl [lambda args]
    (eval (atom (merge @env (zipmap parameters args))) body)))

(defmethod eval-form 'lambda [env [_ parameters body]]
  (Lambda. env parameters body))

(extend-protocol Procedure
  IFn
  (appl [f args] (apply f args)))
