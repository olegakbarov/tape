(ns app.components.chart
  (:require-macros [app.macros :refer [with-preserved-ctx unless]])
  (:require [reagent.core :as r]
            [cljsjs.chartjs]))

(defn draw-chart
  []
  (let [context (.getContext (.getElementById js/document "chart") "2d")
        chart-data
        {:type "line"
         :options {:legend {:display false}
                   :tooltips {:enabled false}
                   ; :gridLines {:display false}
                   :animation 0
                   :scales {:yAxes [{:ticks {:display false :stepSize 400}}]
                            ;; TODO should calculate dynamically
                            :xAxes [{:ticks {:display false :stepSize 3}}]}}
         :data
         {:labels ["0" "2" "4" "6" "8" "10" "12" "14" "16" "18" "20" "22" "24"]
          :datasets
          [{:data
            [3319 1000 1500 2000 2005 1230 2230 1523 1340 1330 1235 1420 3330]
            :borderColor "#657AF3"
            :fill "none"
            :pointStyle "line"
            :radius 1}]}}]
    (js/Chart. context (clj->js chart-data))))

(defn Chart
  []
  (r/create-class {:component-did-mount #(draw-chart)
                   :display-name "chartjs-component"
                   :reagent-render
                   (fn [] [:canvas {:id "chart" :width "700" :height "380"}])}))
