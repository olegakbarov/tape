(ns app.actions.api
  (:require [clojure.walk]
            [app.db :refer [db]]))

;; TODO: change evnt HAS NO MARKET FIELD
(def c
 {"type" "change"
  "payload" {"amount" 3289.8
             "currencyPair" [{:name "bitcoin"
                              :symbol "BTC"}
                             {:name "united-states-dollar"
                              :symbol "USD"}]
             :percent 0.1
             ;; TODO
             :market "bitfinex"
             ;; TODO
             :period "24h"
             :timestamp 1511105150655846100}})

(def t
 {"type" "ticker"
  "payload" {:buy 1.91
             :currencyPair [{:name "eos"
                             :symbol "EOS"}
                            {:name "united-states-dollar"
                             :symbol "USD"}]
             "high" 2.07
             "last" 1.9
             "low" 1.7962
             "market" "bitfinex"
             :sell 1.9004
             :timestamp 1511105150701287000
             :vol 11848722.21655232}})

(defn format-pair
 "Accepts vector of {:name 'eos' :symbol 'EOS'}"
 [v]
 (let [[f l] v]
  (keyword
   (str (:symbol f) "-" (:symbol l)))))

(defmulti evt->db
 (fn [msg] (get msg "type")))

(defmethod evt->db "change" [msg]
 (let [p (-> msg
             clojure.walk/keywordize-keys
             :payload
             clojure.walk/keywordize-keys)
       pair (format-pair (-> p :currencyPair))
       market (keyword (-> p :market))
       res (-> p
               (dissoc :currencyPair)
               (assoc :ts (-> p :timestamp))
               (dissoc :timestamp))]
   (swap! db assoc-in [:markets :bitfinex pair :change] res)))

(defmethod evt->db "ticker" [msg]
 (let [p (-> msg
             clojure.walk/keywordize-keys
             :payload
             clojure.walk/keywordize-keys)
       pair (format-pair (-> p :currencyPair))
       market (keyword (-> p :market))
       res  (-> p
                (dissoc :currencyPair)
                (assoc :currency-pair (keyword pair))
                (assoc :market (keyword (-> p :market))))]
  (swap! db update-in [:markets market pair]
    #(merge % res))))

(defmethod evt->db :default [msg]
 ;; TODO
 (js/console.log "Unexpected event signature: " msg))

