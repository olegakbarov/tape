(ns app.screens.bestprice
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.components.header :refer [header]]
            [cljsjs.moment]
            [app.actions :as actions]
            [clojure.string :refer [split]]))

(defn curr-pair-row [data key]
  (let [pair (:currency-pair data)
        avg (:avg data)]
     (str pair " : " avg)))

(defn render-market-row [market]
 (for [pair (keys market)]
   (let [{:keys [market currency-pair avg low high timestamp]} (get market pair)
         [left right] (split currency-pair "-")]
     ^{:key (str market "x" pair)}
     [:div.currency_row
       [:h5 market]
       [:img.currpic {:src (str "images/" left ".png")
                      :style {:height "25px"}}]
       [:img.currpic {:src (str "images/" right ".png")
                      :style {:height "25px"}}]
       [:h5 (str "Low: " low)]
       [:h5 (str "High: " high)]
       [:div [:div "Updated: "]
             [:div (.fromNow (js/moment (* timestamp 1000)))]]])))

(defn bestprice []
  (let [markets (:markets @db)]
   [:div#wrapper
     [header]
     [:button  {:on-click #(js/console.log (:markets @db))} "State yo"]
     (for [name  (-> @db
                     :markets
                     keys)]
       ^{:key (get markets name)}
       (render-market-row (get markets name)))]))
