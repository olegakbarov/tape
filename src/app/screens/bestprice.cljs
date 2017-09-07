(ns app.screens.bestprice
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.components.header :refer [header]]
            [cljsjs.moment]
            [app.logic :as logic]
            [app.actions :as actions]
            [clojure.string :refer [split]]))

(defn t-head []
  [:div.thead_wrapper
    (for [i ["Pair" "Price" "Change" "Market"]]
     ^{:key i}
     [:div.thead_item
       [:span.thead_clickable
         (str "▼ " i)]])])

(defn render-row [m]
   (let [[key value] m
         {:keys [market currency-pair avg low high timestamp]} value
         [left right] (split currency-pair "-")]
    ; (js/console.log m)
    ^{:key (:currency-pair value)}
    [:div.titem_wrapper
      ^{:key "curr-pair"}
      [:div.titem_cell currency-pair]
      ^{:key "last-price"}
      [:div.titem_cell (:last value)]
      ^{:key "dynamcis"}
      [:div.titem_cell "todo"]
      ^{:key "market-name"}
      [:div.titem_cell market]]))


(defn bestprice []
  (let [markets (:markets @db)]
   [:div
     [header]
     [t-head]
     [:div.bestprice_wrapper
       (for [pair (logic/get-best-pairs)]
         (render-row pair))]]))

