(ns app.renderer
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :refer [db]]
            [app.actions :as actions]))

(defn init []
  (js/console.log "Starting Application"))

(defn header []
  (let [screen (get-in @db [:ui :screen])]
    [:div#header
      [:div#toggle
        [:div.by_currency
         {:on-click #(actions/to-screen :currency)
          :class (if (= screen :currency)
                     :active)}
         "currency"]
        [:div.by_market
         {:on-click #(actions/to-screen :market)
          :class (if (= screen :market)
                     :active)}
         "market"]]]))

(defn curr-pair-row [data key]
  (let [pair (:CurrencyPair data)
        avg (:Avg data)]
     (str pair " : " avg)))

(defn items-table [items]
  (let [screen (get-in @db [:ui :screen])]
    [:div
      (cond
        (= screen :market) [:h1 "Markets"]
        (= screen :currency) [:h1 "Currency"])]))
   ; (for [[name info] (:data db)]
   ;   ^{:key key}
   ;   [:div [:strong name] (str " " (curr-pair-row info key))])])


(defn root-component []
  (let [data (> (count (:data db))
                0)]
    [:div
      [header]
      [items-table data]]))

(reagent/render
  [root-component]
  (js/document.getElementById "container"))
