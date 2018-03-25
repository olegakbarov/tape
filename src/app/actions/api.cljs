(ns app.actions.api
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.walk]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [cljs.spec.test.alpha :as ts]
            [cljs.pprint :refer [pprint]]
            [cljs-http.client :as http]
            [reagent.core :as r]
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
        (when (and percent amount (-> @db
                                      :markets
                                      (get market)
                                      (get pair)))
          (swap! db assoc-in
                 [:markets market pair :changes]
                 {:percent percent
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
            (swap! db update-in [:markets market pair] #(merge % $))))))

(defn evt->db
  [msg]
  (condp = (get msg "type")
    "tickers" (process-ticker msg)
    "changes" (process-change msg)
    :default (log "Unexpected event signature: " msg)))

(defn state->db
  [s]
  (let [{:keys [tickers changes]} s]
    (doseq [item (map identity
                      (map
                        #(into {} [{"type" "tickers"} {"payload" %}])
                        tickers))]
      (evt->db item))
    (doseq [item (map identity
                      (map
                        #(into {} [{"type" "changes"} {"payload" %}])
                        changes))]
      (evt->db item))))

(defn chart-data->db
  [s]
  (let [k (clojure.walk/keywordize-keys s)
        {:keys [Values]} (first k)
        [market pair] @(r/cursor db [:ui/current-graph])
        pts' (->> Values
                 (remove nil?)
                 ;; TODO wtf 1000000000????
                 (map (fn [v] (vec [(/ (first v) 1000000) (last v)]))))]
    (swap! chart-data assoc-in [market pair] pts')))

; https://cryptounicorns.io/api/v1/markets/bitfinex/tickers/eos-btc/last
(defn fetch-chart-data!
  [market pair]
  (go (let [endpoint (str (:http-endpoint config) "/tickers")
            now (.getTime (js/Date.))
            week (* 60 60 24 7 1000)
            query {
                   "market" (name market)
                   "symbolPair" (name pair)
                   "metric" "last"
                   "resolution" "1h"
                   "from" (* 1000000 (- now week))
                   "to" (* 1000000 now)}
            response (<! (http/get endpoint {:with-credentials? false,
                                             :query-params query}))]
        ;; handle 4xx-5xx resp
        (chart-data->db (-> response :body)))))

(defn set-initial-data-fetching []
  (swap! db assoc :ui/fetching-init-data? true))

(defn unset-initial-data-fetching []
  (swap! db assoc :ui/fetching-init-data? false))

(defn fetch-state!
  []
  (go (let [_ (set-initial-data-fetching)
            endpoint (str (:http-endpoint config) "/events")
            response (<! (http/get endpoint {:with-credentials? false}))]
        (state->db (:body response))
        (unset-initial-data-fetching))))

