(ns app.actions.api
  (:require [clojure.walk]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [cljs.spec.test.alpha :as ts]
            [cljs.pprint :refer [pprint]]
            [klang.core :refer-macros [info! erro!]]
            [app.db :refer [db chart-data]]
            [app.config :refer [config]]))

(when (= :dev (:env config))
  (do (info! "[actions.api] Spec activated...")
      (ts/instrument)))

(defn log [& args]
  (js/console.log (pprint args)))

(defn validate-ticker
  "Returns ticker only when important fields exist (otherwise nil)"
  [t]
  (let [has? #(not (nil? (%1 %2)))
        has-market? #(has? :market %)
        has-pair? #(has? :symbolPair %)
        has-last? #(has? :last %)
        checklist ((juxt has-market? has-pair? has-last?) t)]
    (if-not (every? true? checklist)
     (log "Invalid ticker: " checklist)
     t)))

(defn format-msg [raw]
  (-> raw
      (get "payload")
      clojure.walk/keywordize-keys))

(defn process-change
  [msg]
  (let [c (format-msg msg)
        valid? (and (:symbolPair c) (:market c))]
    (if-not valid?
      (log "Invalid change msg: " c)
      (let [market (keyword (:market c))
            pair (keyword (:symbolPair c))
            {:keys [percent amount c]} c]
        ;; TODO set n/a when not provided
        (when (-> @db
                  :markets
                  (get market)
                  (get pair))
          (swap! db assoc-in [:markets market pair :changes] {:percent percent
                                                              :amount amount}))))))

(defn process-ticker
  [msg]
  (let [t (format-msg msg)
        pair (keyword (-> t :symbolPair))
        market (keyword (-> t :market))
        t' (validate-ticker t)]
    (when t'
      (as-> t' $
            (clojure.set/rename-keys $ {:symbolPair :symbol-pair})
            (assoc $ :symbol-pair pair)
            (assoc $ :market market)
            (swap! db assoc-in [:markets market pair] $)))))

(defn evt->db
  [msg]
  ; (js/console.log msg)
  (condp = (get msg "type")
    "tickers" (process-ticker msg)
    "changes" (process-change msg)
    :default (log "Unexpected event signature: " msg)))

(defn state->db
  [s]
  ;;TODO  CHECK
  (let [{:keys [tickers changes]} s]
    (doseq [item (map identity
                      (map #(into {} [{"type" "tickers"} {"payload" %}]) tickers))]
      (evt->db item))
    (doseq [item (map identity
                      (map #(into {} [{"type" "changes"} {"payload" %}]) changes))]
      (evt->db item))))

(defn chart-data->db
  [s]
  (let [k (clojure.walk/keywordize-keys s)
        {:keys [points marketName symbolPair]} k
        pts' (vec (remove nil? points))]
    (swap! chart-data assoc-in
      [(keyword marketName) (keyword symbolPair)]
      pts')))
