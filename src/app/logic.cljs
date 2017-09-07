(ns app.logic
  (:require [clojure.walk]
            [app.db :refer [db]]))

(defn get-best-pairs []
  (let [markets (-> @db :markets)
        market-items (vals markets)]
    (reduce
     (fn [acc item]
      (into {}
        (map
         (fn [item]
          (let [[key val] item
                acc-pair (get acc key)]
            (if-not acc-pair
                (assoc acc key val)
                (if (< (:last val)
                       (:last acc-pair))
                    (assoc acc key val)
                    acc))))
         item)))
     {}
     market-items)))

