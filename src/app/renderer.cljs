(ns app.renderer
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :refer [db]]
            [app.api]
            [cljsjs.moment]
            [app.actions :as actions]
            [clojure.string :refer [split]]))

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
    ; [:div
    ;   (cond
    ;     (= screen :market) [:h1 "Markets"]
    ;     (= screen :currency) [:h1 "Currency"])]
    ;
   ; (js/console.log (:data @db))
   (for [[name info] (:data @db)]
     ^{:key key}
     [:div [:strong name] (str " " (curr-pair-row info key))])))

(defn render-market-row [market]
 (for [pair (keys market)]
   (let [{:keys [Market CurrencyPair Avg Low High Timestamp]} (get market pair)
         [left right] (split CurrencyPair "-")]
     (js/console.log Market CurrencyPair Avg)
     ^{:key (str (:Market market) "x" pair)}
     [:div.currency_row
       [:h5 Market]
       [:img.currpic {:src (str "images/" left ".png")
                      :style {:height "25px"}}]
       [:img.currpic {:src (str "images/" right ".png")
                      :style {:height "25px"}}]

       [:h5 (str "Low: " Low)]
       [:h5 (str "High: " High)]
       [:div [:div "Updated: "]
             [:div (.fromNow (js/moment))]]])))


(defn root []
  (let [markets (:markets @db)]
    [:div
     [header]
     [:div#wrapper
       (for [name  (-> @db
                       :markets
                       keys)]
         ^{:key (get markets name)}
         (render-market-row (get markets name)))]]))

(reagent/render
  [root]
  (js/document.getElementById "container"))
