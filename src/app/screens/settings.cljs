(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.components.header :refer [Header]]))

(defn settings []
  [:div
   [Header]
   [:div#wrapper]])
