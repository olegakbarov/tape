(ns app.actions
  (:require [app.db :refer [db]]))

(defn to-screen [screen]
  (swap! db assoc-in [:ui :screen] screen))

(defn update-db-with-ticker [ticker]
  (let [market (:Market ticker)
        updated-at (:Timestamp ticker)
        pair (:CurrencyPair ticker)
        last-price (:Last ticker)]
    (js/console.log (:markets @db))
    (if-not (nil? (:Market ticker))
      (swap! db assoc-in [:markets market pair] ticker))))

