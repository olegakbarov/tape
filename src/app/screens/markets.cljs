(ns app.screens.markets
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :refer [db]]
            [app.actions :as actions]
            [app.constants.currs :refer [pairs]]
            [app.components.header :refer [header]]))

(defn markets []
  (let [markets (-> @db :markets)]
    [:div.markets_wrapper
     [header]
     [:ul
      (for [m markets]
        (let [[name pairs] m]
          ^{:key name}
          [:li name]))]]))


