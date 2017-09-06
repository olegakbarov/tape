(ns app.screens.markets
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :refer [db]]
            [app.components.header :refer [header]]))

(defn markets []
  [:div#wrapper
   [header]
   [:ul
    (for [name (-> @db
                   :markets
                   keys)]
      [:li name])]])


