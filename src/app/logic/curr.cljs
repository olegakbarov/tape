(ns app.logic.curr
  (:require [clojure.walk]
            [app.db :refer [db]]))

(defn best-pairs
  "Returns pairs with lowest prices across "
  [markets]
  (let [market-items (vals markets)]
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

(defn get-market-names []
  (map
   #(.toUpperCase %)
   (keys (:markets @db))))

(defn get-all-currs []
  (into #{}
    (flatten
      (map
       #(flatten (clojure.string/split % "-"))
       (->> (:markets @db)
            vals
            (map keys)
            flatten)))))

(defn get-crypto-currs
  "TODO: remove hadcoded fiats"
  []
  (remove
   #(some (fn [x] (= x %)) ["USD" "RUB"])
   (get-all-currs)))

(defn currs-by-market [market]
  (let [m (:markets @db)
        pairs (get m market)]
    (reduce
     (fn [acc item]
       (flatten (conj acc
                  (clojure.string/split item "-"))))
     []
     (keys pairs))))

