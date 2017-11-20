(ns app.actions.api
  (:require [clojure.walk]
            [app.db :refer [db]]))

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
  (swap! db assoc-in [:markets market pair :change] res)))

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

