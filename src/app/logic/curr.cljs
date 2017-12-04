(ns app.logic.curr
  (:require [clojure.walk]
            [app.db :refer [db]]))

(defn get-lowest-prices
  "Returns lowest `:last` prices across all markets"
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
                      (if (< (:last val) (:last acc-pair))
                        (assoc acc key val)
                        acc))))
                item)))
      {}
      market-items)))

(defn best-pairs
  "Returns pairs with lowest prices across "
  ([markets] (remove empty? (vals (get-lowest-prices markets))))
  ([markets pair-name] (:sell (get (get-lowest-prices markets) pair-name))))

(defn all-pairs
  [markets]
  (->> markets
       vals
       flatten
       (map vals)
       flatten))

(def fiats #{"USD" "RUB"})

(defn get-market-names
  [markets]
  (->> markets
       keys
       (map name)))

;; rename get-all-curr-symbols
(defn get-all-currs
  "Returns all currencies across all markets"
  [markets]
  (set (flatten (map #(flatten (clojure.string/split % "-"))
                  (->> (:markets @db)
                       vals
                       (map keys)
                       flatten
                       (map name))))))

(defn get-crypto-currs
  "TODO: remove hadcoded fiats"
  [markets]
  (remove #(some (fn [x] (= x %)) ["USD" "RUB"]) (get-all-currs markets)))

(defn currs-by-market
  "Returns currencies available for given market"
  [market]
  (let [m (:markets @db)
        pairs (get m market)]
    (reduce (fn [acc item] (flatten (conj acc (clojure.string/split item "-"))))
      []
      (keys pairs))))

(defn user-favs
  [markets favs]
  (reduce (fn [acc tupl] (conj acc (get-in markets tupl))) [] favs))

(defn by-query
  [markets q]
  (->> markets
       vals
       (mapcat vals)
       (filter #(re-find (re-pattern q) (:market %)))))
