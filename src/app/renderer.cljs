(ns app.renderer
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :as db]))

(defn init []
  (js/console.log "Starting Application"))

(defn header []
  [:div#header
    [:div#toggle
      [:div.by_currency {:onClick "currency"}]
      [:div.by_market   "market"]]])

(defn curr-pair-row [data key]
  (let [pair (:CurrencyPair data)
        avg (:Avg data)]
     (str pair " : " avg)))

(defn items-table [items]
  [:div
   (for [[name info] (:data db)]
     ^{:key key}
     [:div [:strong name] (str " " (curr-pair-row info key))])])

(defn root-component []
  (let [data (> (count (:data db))
                0)])
  [:div
    [header]
    [items-table data]])

(reagent/render
  [root-component]
  (js/document.getElementById "container"))
