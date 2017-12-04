(ns app.actions.api
  (:require [clojure.walk]
            [app.db :refer [db]]))

(defn format-pair
  "Accepts vector of {:name 'eos' :symbol 'EOS'}"
  [v]
  (let [[f l] v] (keyword (str (:symbol f) "-" (:symbol l)))))

(defn process-change
  [msg]
  (let [p (-> msg
              clojure.walk/keywordize-keys
              :payload
              clojure.walk/keywordize-keys)
        pair (format-pair (-> p
                              :currencyPair))
        market (keyword (-> p
                            :market))
        res (-> p
                (dissoc :currencyPair)
                (assoc :ts (-> p
                               :timestamp))
                (dissoc :timestamp))]
    ;; TODO! remove hardcode
    (swap! db assoc-in [:markets :bitfinex pair :change] res)))

(defn process-ticker
  [msg]
  (let [p (-> msg
              clojure.walk/keywordize-keys
              :payload
              clojure.walk/keywordize-keys)
        pair (format-pair (-> p
                              :currencyPair))
        market (keyword (-> p
                            :market))
        res (-> p
                (dissoc :currencyPair)
                (assoc :currency-pair (keyword pair))
                (assoc :market (keyword (-> p
                                            :market))))]
    (swap! db update-in [:markets market pair] #(merge % res))))

(defn evt->db
  [msg]
  (condp = (get msg "type")
    "ticker" (process-ticker msg)
    "change" (process-change msg)
    :default (js/console.log "Unexpected event signature: " msg)))

(defn state->db
  [s]
  (let [{:keys [ticker change]} s]
    (do
      (doseq [item (map identity
                     (map #(into {} [{"type" "ticker"} {:payload %}]) ticker))]
        (evt->db item))
      (doseq [item (map identity
                     (map #(into {} [{"type" "change"} {:payload %}]) change))]
        (evt->db item)))))
