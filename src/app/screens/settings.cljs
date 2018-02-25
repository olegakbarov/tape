(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.components.header :refer [header]]))

(defn settings []
  [:div
   [header]
   [:div#wrapper]])
