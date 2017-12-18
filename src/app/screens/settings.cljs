(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.components.header :refer [Header]]
            [app.components.ui :refer [Button Spinner]]))

(defn settings
  []
  [:div#wrapper]
  [:div
   {:style {:display "flex"
            :align-items "center"
            :justify-content "center"
            :height "100%"}}
   [Spinner]])
; [Button
;  {:type "submit" :on-click #(js/console.log "saved!")}
;  "Save"]])
