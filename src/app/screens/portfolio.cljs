(ns app.screens.portfolio
  (:require [reagent.core :as reagent]
            [app.components.profile :refer [header]]))

(defn portfolio []
  [:div
    [header]
    [:h1 "portfolio"]])
