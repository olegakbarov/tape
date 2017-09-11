(ns app.logic
  (:require [clojure.walk]
            [app.db :refer [db]]))

(defn get-best-pairs []
  (let [markets (:markets @db)
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

(defn all-available-currs []
  (into #{}
    (flatten
      (map
       #(flatten (clojure.string/split % "-"))
       (->> (:markets @db)
            vals
            (map keys)
            flatten)))))

(defn currs-by-market [market]
  (let [m (:markets @db)
        pairs (get m market)]
    (reduce
     (fn [acc item]
       (flatten (conj acc
                  (clojure.string/split item "-"))))
     []
     (keys pairs))))

