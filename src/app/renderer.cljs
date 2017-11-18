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
            [mount.core :as mount]))

(enable-console-print!)

(def electron (js/require "electron"))
(def webcontents (.-webFrame electron))

(defn disable-content-scale! []
 (.setVisualZoomLevelLimits webcontents  1 1)
 (.setLayoutZoomLevelLimits webcontents  0 0))

(disable-content-scale!)

(defn init []
 (mount/start))

(defn Routes []
 (let [s (-> @router :screen)]
  (condp = s
   :live      [live-board]
   :portfolio [portfolio]
   :settings  [settings]
   :alerts    [alerts])))

(defn root []
 (let [toggle-items ["Live" "Portfolio" "Alerts" "Settings"]]
  [:div#containter
   [Header
    ^{:key "header"}
    toggle-items]
   [Routes]]))

(reagent/render
  [root]
  (js/document.getElementById "root"))
