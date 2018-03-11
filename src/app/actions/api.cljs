(ns app.actions.api
  (:require [clojure.walk]
            [app.db :refer [db chart-data]]))

(defn process-change
  [msg]
  (js/console.log msg)
  (let [p (-> msg
              clojure.walk/keywordize-keys
              :payload
              clojure.walk/keywordize-keys)
        pair (-> p :symbolPair)
        market (keyword (-> p :market))
        res (-> p
                (dissoc :symbolPair)
                (assoc :ts (-> p :timestamp))
                (dissoc :timestamp))]
    (swap! db assoc-in [:markets market (keyword pair) :changes] res)))

(defn process-ticker
  [msg]
  (let [p (-> msg
              clojure.walk/keywordize-keys
              :payload
              clojure.walk/keywordize-keys)
        pair (-> p :symbolPair)
        market (keyword (-> p :market))
        res (-> p
                (dissoc :symbolPair)
                (assoc :symbol-pair (keyword pair))
                (assoc :market
                       (keyword (-> p :market))))]
    (swap! db update-in [:markets market (keyword pair)] #(merge % res))))

(defn evt->db
  [msg]
  ;; TODO spec it
  (condp = (get msg "type")
    "tickers" (process-ticker msg)
    "changes" (process-change msg)
    :default (js/console.log "Unexpected event signature: " msg)))

(defn state->db
  [s]
  (let [{:keys [tickers changes]} s]
    (doseq [item (map identity
                      (map #(into {} [{"type" "tickers"} {:payload %}]) tickers))]
      (evt->db item))
    (doseq [item (map identity
                      (map #(into {} [{"type" "changes"} {:payload %}]) changes))]
      (evt->db item))))

(defn chart-data->db
  [s]
  (let [k (clojure.walk/keywordize-keys s)
        {:keys [points marketName symbolPair]} k
        pts' (vec (remove nil? points))]
    (swap! chart-data assoc-in
      [(keyword marketName) (keyword symbolPair)]
      pts')))
