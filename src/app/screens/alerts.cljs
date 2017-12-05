(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [cljsjs.react-select]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.logic.curr :refer [get-market-names get-crypto-currs]]
            [app.components.ui :refer [EmptyList]]))

(defonce curr (r/atom nil))
(defonce market (r/atom nil))

(defn select-curr
  []
  (let [opts (get-crypto-currs (-> @db
                                   :markets))]
    [:>
     js/window.Select
     {:value @curr
      :options (clj->js (map
                         #(zipmap [:value :label] [% %])
                         opts))
      :onChange #(reset! curr (aget % "value"))}]))

(defn select-market
  []
  (let [opts (get-market-names (-> @db
                                   :markets))]
    [:>
     js/window.Select
     {:value @market
      :options (clj->js (map
                         #(zipmap [:value :label] [% %])
                         opts))
      :onChange #(reset! market (aget % "value"))}]))

(defn alerts-list []
  [EmptyList "alerts"])

(defn add-alert [])

(defn alerts
  []
  [:div#wrapper
   [alerts-list]
   [:div.form_wrap
    [:div.input_wrapper
     [:div.label "Currency"]
     [select-curr]]
    [:div.input_wrapper
     [:div.label "Market"]
     [select-market]]]])

