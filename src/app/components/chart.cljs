(ns app.components.chart
  (:require [reagent.core :as r]
            [cljsjs.dygraph]
            [cljsjs.moment]))

(defn obj->clj
  [obj]
  (-> (fn [result key]
        (let [v (aget obj key)]
          (if (= "function" (goog/typeOf v))
            result
            (assoc result key v))))
      (reduce {} (.getKeys goog/object obj))))

(defn legend-formatter [data]
  (let [cljd (obj->clj data)
        x (get cljd "x")
        y (-> cljd
              (get "series")
              (aget 0)
              (aget "y"))]
    ; (js/console.log y)
    (if (nil? x)
        nil
        (str " " (.format (js/moment x) "MMMM Do, hh:mm:ss")
                 " price " y))))

(def config
  {:axisLabelFontSize 9
   :drawAxesAtZero true
   :labels ["time" "price"]
   :drawAxis false
   :legendFormatter legend-formatter})

(defn render-stock-fn
  [data]
  (fn [component]
    (js/Dygraph.
      (r/dom-node component)
      (clj->js data)
      (clj->js config))))

(defn Chart
  [data]
  (r/create-class
    {:component-did-mount (render-stock-fn data)
     :component-did-update (render-stock-fn data)
     :reagent-render (fn [data] [:div.chart
                                 {:style {:width "320px"
                                          :height "220px"}}])}))
