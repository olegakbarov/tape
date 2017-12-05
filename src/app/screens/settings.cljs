(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.components.header :refer [Header]]
            [app.components.ui :refer [Button]]))

(defn settings
  []
  [:div#wrapper
   [Button
    {:type "submit" :on-click #(js/console.log "saved!") :color "#12D823"}
    "Save"]])
