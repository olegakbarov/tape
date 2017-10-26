(ns app.renderer
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.api :refer [listen-ws!]]
            [app.listeners :refer [start-listeners!]]
            [app.actions.storage :refer [read-file!]]
            [app.actions.ui :refer [to-screen]]
            [app.config :refer [config]]
            [app.screens.live :refer [live-board]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]
            [app.components.header :refer [Header]]
            [app.components.ui :refer [Container
                                       Icon]]))

(enable-console-print!)

(defn init []
 (read-file! "portfolio.edn")
 (listen-ws!)
 (start-listeners!))

(defn routes []
 (let [s (-> @router :screen)]
   (condp = s
     :live [live-board]
     :portfolio [portfolio]
     :settings  [settings]
     :alerts    [alerts])))

(defn root []
 (let [toggle-items ["Live" "Portfolio"]]
   [Container
    [Header toggle-items]
    [routes]]))

(reagent/render
  [root]
  (js/document.getElementById "container"))
