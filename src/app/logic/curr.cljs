(ns app.logic.curr
  (:require [clojure.walk]
            [reagent.core :as r]
            [app.db :refer [db]]
            [cljs.pprint :refer [pprint]]))

(defn get-lowest-prices
  "Returns lowest `:last` prices across all markets"
  [markets]
  (let [market-items (vals markets)
        res (reduce
              (fn [acc item]
                 (if-let [v (get acc (:symbol-pair item))]
                    (if (< (:last item) (:last v))
                      acc
                      (assoc acc (:symbol-pair item) item))
                    (assoc acc (:symbol-pair item) item)))
              {}
              ;; flat vector of all pairs
              (mapcat vals (vals markets)))]
      res))

(defn best-pairs
  "Returns pairs with lowest prices across all markets"
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
  (set (flatten (map
                  #(flatten (clojure.string/split % "-"))
                  (->> markets
                       vals
                       (map keys)
                       flatten
                       (map name))))))

(defn get-all-pair-names
  "Returns unique currency pairs"
  [markets]
  (->> markets
       vals
       (map keys)
       flatten
       (map name)
       set))

(defn get-crypto-currs
  "TODO: remove hadcoded fiats"
  [markets]
  (remove #(some (fn [x] (= x %)) ["USD" "RUB"]) (get-all-currs markets)))

(defn currs-by-market
  "Returns currencies available for given market"
  [market]
  (let [m @(r/cursor db [:markets])
        pairs (get m market)]
    (reduce (fn [acc item] (flatten (conj acc (clojure.string/split item "-"))))
            []
            (keys pairs))))

(defn user-favs
  [markets favs]
  (if (empty? markets)
    []
    (reduce (fn [acc tupl] (conj acc (get-in markets tupl))) [] favs)))

(defn by-query
  [markets q]
  (->> markets
       vals
       (mapcat vals)
       (filter #(re-find (re-pattern q) (:market %)))))

(defn pairs-by-query
  "Returns pairs collection only with items where :market
  or :currency-pair fields matches the substring `q`"
  ;; TODO fails with special chars (eg \)
  [pairs q]
  (let [lc #(.toLowerCase %)]
    (filter #(or (re-find (re-pattern (lc q))
                          (lc (-> %
                                  :market
                                  name)))
                 (re-find (re-pattern (lc q))
                          (lc (-> %
                                  :symbol-pair
                                  name))))
            pairs)))

(defn- to-dollar
  "Returns dollar price of curr on this market"
  [amount curr market]
  ;; TODO handle case where altcoin doesn't have dollar value (if any)
  ;; TODO spec it
  (let [pair (keyword (str (name curr) "-USD"))
        lst @(r/cursor db [:markets (keyword market) (keyword pair) :last])]
    (* amount lst)))

(defn get-total-worth
  [folio]
  ; (js/console.log folio)
  (reduce (fn [acc [id item]]
            (let [{:keys [amount currency market]} item]
              (+ acc (to-dollar (js/parseFloat amount) currency market))))
          0
          folio))

