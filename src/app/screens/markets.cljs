(ns app.screens.markets
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :refer [db]]
            [app.actions :as actions]
            [app.components.header :refer [header]]))

(defn markets []
  [:div#wrapper
   [header]
   [:ul
    (for [name (-> @db
                   :markets
                   keys)]
      ^{:key name}
      [:li name])]])


