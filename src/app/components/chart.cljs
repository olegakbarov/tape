(ns app.components.chart
  (:require [reagent.core :as r]))

(def Frappe (js/require "frappe-charts"))

(def data
 {:labels [1 2 3 4 5 6 7]
  :datasets [{:title "aaa"
              :color "light-blue"
              :values [10 52 23 54 94 31 22 12 43 22]}]})

(defn Chart [props]
 (let [!ref (atom nil)
       chart (atom nil)]
  (r/create-class
   {:display-name "frappe-chart"
    :component-did-mount
     #(reset! chart
        (Frappe.
          (clj->js
           {:type "line"
            :show_dots 0
            :heatline 1
            :region_fill 1
            :data data
            :title "title"
            :parent @!ref
            :height 190
            :width 250})))
    :reagent-render
     (fn []
      (let [{:keys [data]} props]
        [:div {:ref (fn [com] (reset! !ref com))}]))})))

