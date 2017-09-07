(ns app.screens.bestprice
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.components.header :refer [header]]
            [cljsjs.moment]
            [app.actions :as actions]
            [clojure.string :refer [split]]))

(defn get-pair-cell [left right]
 [:div
   [:img.currpic {:src (str "images/" left ".png")
                  :style {:height "25px"}}]
   [:img.currpic {:src (str "images/" right ".png")
                  :style {:height "25px"}}]])

(defn render-market-row [m]
 (for [pair (keys m)]
   (let [{:keys [market currency-pair avg low high timestamp]} (get m pair)
         [left right] (split currency-pair "-")]
     ^{:key (str market "x" pair)}
     [:div.currency_row
       [:h5 (str "Low: " low)]
       [:h5 (str "High: " high)]
       [:div [:div "Updated: "]
             [:div (.fromNow (js/moment (* timestamp 1000)))]]])))

(defn thead []
  [:div])

(defn bestprice []
  (let [markets (:markets @db)]
   [:div#wrapper
     [header]
     [thead]
     (for [name  (-> @db
                     :markets
                     keys)]
       (let [n (get markets name)]
         ^{:key n}
         (render-market-row n)))]))
