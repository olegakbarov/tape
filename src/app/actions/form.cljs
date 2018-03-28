(ns app.actions.form
  (:require [app.db :refer [db]]))

(defn update-alert-form [k v] (swap! db assoc-in [:form/alerts k] v))

(defn clear-alert-form
  []
  (swap! db update-in
    [:form/alerts]
    (fn [a] (zipmap (keys a) (repeat (count (keys a)) "")))))

(defn update-portfolio-form
  [k v]
  ;; TODO ugly...
  (if (= k :pair)
    (swap! db assoc-in [:form/portfolio (keyword k)] v)
    (swap! db assoc-in [:form/portfolio k] v)))

(defn clear-portfolio-form
  []
  (swap! db update-in
    [:form/portfolio]
    (fn [a] (zipmap (keys a) (repeat (count (keys a)) "")))))
