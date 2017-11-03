(ns app.renderer
  ; (:require-macros [mount.core :as mount :refer [defstate]])
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.api :refer [listen-ws!]]
            [app.eventloop :refer [start-title-updater!]]
            [app.actions.storage :refer [read-data-file!]]
            [app.actions.ui :refer [to-screen]]
            [app.config :refer [config]]
            [app.screens.live :refer [live-board]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]
            [app.components.header :refer [Header]]
            [app.components.ui :refer [Container
                                       Icon]]))
; (mount/in-cljc-mode)
; (mount/start)

(enable-console-print!)

(defn init []
 (read-data-file!)
 (listen-ws!)
 (start-title-updater!))

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
