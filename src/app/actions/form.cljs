(ns app.actions.form
  (:require [app.db :refer [db]]))

(defn update-alert-form
  [k v]
  (assert true (and (keyword? k) (string? v)))
  (swap! db assoc-in [:form/alerts k] v))

(defn update-portfolio-form
  [k v]
  (assert true (and (keyword? k) (string? v)))
  (swap! db assoc-in [:form/alerts k] v))
