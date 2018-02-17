(ns app.components.chart
  (:require [reagent.core :as r]
            [cljsjs.highstock]))

(def hc (js/require "highcharts/highstock"))

(defn chart-config
  [data]
  {:credits false
   :plotOptions {:series {:animation false}}
   :rangeSelector {:selected 1
                   :buttons [{:type "day" :count 1 :text "1d"}
                             {:type "day" :count 7 :text "1w"}
                             {:type "month" :count 1 :text "1m"}
                             {:type "month" :count 3 :text "3m"}
                             {:type "month" :count 6 :text "6m"}
                             {:type "month" :count 12 :text "1y"}]
                   :inputPosition {:align "left" :x "40px"}}
   :xAxis [{:visible false}]
   :yAxis [{:visible false}]
   :tooltip {:split false}
   :scrollbar {:enabled false}
   :navigator {:enabled false}
   :chart {:style {:font-family
                   "-apple-system, BlinkMacSystemFont, sans-serif"}}
   :series [{:name "TODO"
             :type "area"
             :color "black"
             :upColor "#00F72C"
             :data data
             :treshold nil
             :fillColor {:linearGradient {:x1 0 :y1 0 :x2 0 :y2 1}
                         :stops [[0 "black"] [1 "#ccc"]]}}]})

(defn render-stock-fn
  [data]
  (fn [component]
    (.stockChart hc (r/dom-node component) (clj->js (chart-config data)))))

(defn Chart
  [data]
  (r/create-class
   {:component-did-mount (render-stock-fn data)
    :component-did-update (render-stock-fn data)
    :reagent-render
    (fn [data]
      [:div.chart {:style {:width "320px" :height "320px"}}])}))
