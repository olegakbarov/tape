(ns app.screens.alerts
  (:require [reagent.core :as reagent]
            [app.components.profile :refer [header]]))

(defn alerts []
  [:div
    [header]
    [:h1 "Alerts"]])


