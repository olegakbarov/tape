(ns app.renderer
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.api :refer [start-loop!]]
            [app.listeners :refer [start-listeners!]]
            [app.actions.storage :refer [read-portfolio!]]
            [app.config :refer [config]]
            [app.screens.bestprice :refer [bestprice]]
            [app.screens.markets :refer [markets]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]))

(defn init []
 (read-portfolio!)
 (start-loop!)
 (start-listeners!))

(defn routes []
 (let [s (-> @router :screen)]
   (condp = s
     :bestprice [bestprice]
     :markets   [markets]
     :settings  [settings]
     :portfolio [portfolio]
     :alerts    [alerts])))

(defn root []
   [routes])

(reagent/render
  [root]
  (js/document.getElementById "container"))
