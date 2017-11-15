(ns app.renderer
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.api :refer [listen-ws!]]
            [app.eventloop :refer [start-title-loop!]]
            [app.actions.storage :refer [read-data-file!]]
            [app.actions.ui :refer [to-screen]]
            [app.config :refer [config]]
            [app.screens.live :refer [live-board]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]
            [app.components.header :refer [Header]]
            [mount.core :as mount]
            [app.components.ui :refer [Container
                                       Icon]]))

(enable-console-print!)

(defn init []
 (mount/start))

(defn routes []
 (let [s (-> @router :screen)]
  (condp = s
   :live [live-board]
   :portfolio [portfolio]
   :settings  [settings]
   :alerts    [alerts])))

(defn root []
 (let [toggle-items ["Live" "Portfolio" "Alerts" "Settings"]]
  [Container
   [Header toggle-items]
   [routes]]))

(reagent/render
  [root]
  (js/document.getElementById "container"))
