(ns app.logic
  (:require [clojure.walk]
            [app.db :refer [db]]))

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

