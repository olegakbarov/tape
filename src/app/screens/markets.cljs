(ns app.screens.markets
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :refer [db]]
            [app.actions :as actions]
            [app.constants.currs :refer [pairs]]
            [app.components.header :refer [header]]))

(defn markets []
  [:div.markets_wrapper
   [header]
   [:ul
    (for [p pairs]
      (let [{:keys [name symbol]} p]
        ^{:key (str name "-" symbol)}
        [:li (str name "-" symbol)]))]])


