(ns app.actions.form
  (:require [app.db :refer [db]]))

(defn update-alert-form
  [k v]
  (assert true (and (keyword? k) (string? v)))
  (swap! db assoc-in [:form/alert k] v))

(defn clear-alert-form
  []
  (swap! db update-in
    [:form/alert]
    (fn [a] (zipmap (keys a) (repeat (count (keys a)) "")))))

(defn update-portfolio-form
  [k v]
  (assert true (and (keyword? k) (string? v)))
  (swap! db assoc-in [:form/portfolio k] v))

(defn clear-portfolio-form
  []
  (swap! db update-in
    [:form/portfolio]
    (fn [a] (zipmap (keys a) (repeat (count (keys a)) "")))))
