(ns app.renderer
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.api :refer [listen-ws! fetch-state!]]
            [app.eventloop :refer [start-title-loop!]]
            [app.actions.storage :refer [read-data-file!]]
            [app.actions.ui :refer [to-screen]]
            [app.config :refer [config]]
            [app.screens.live :refer [live-board]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]
            [app.screens.detailed :refer [DetailsContent]]
            [app.components.header :refer [Header]]
            [mount.core :as mount]
            [goog.object :as gobj]
            [app.motion :refer [Motion spring presets]]))

(enable-console-print!)

(def electron (js/require "electron"))
(def webcontents (.-webFrame electron))

(defn disable-content-scale!
  []
  (.setVisualZoomLevelLimits webcontents 1 1)
  (.setLayoutZoomLevelLimits webcontents 0 0))

(disable-content-scale!)

(defn init [] (fetch-state!) (mount/start))

(defn Routes
  []
  (let [s (-> @router
              :screen)]
    (condp = s
      :live [live-board]
      :portfolio [portfolio]
      :settings [settings]
      :alerts [alerts])))

(defn Child
  [{c :children}]
  (let [y (gobj/get c "y")]
    [:div
     {:style {:position "absolute",
              :width "321px",
              :height "320px",
              :background-color "#fff",
              :z-index 999,
              :border-radius "4px 4px 0 0",
              :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)",
              :-webkit-transform (str "translateY(" y "px)"),
              :transform (str "translateY(" y "px)")}} [DetailsContent]]))

(def Child-comp (reagent/reactify-component Child))

(defn DetailedView
  []
  (fn [] [:div
          {:style {:position "absolute",
                   :bottom 0,
                   :display (if (:ui/detailed-view @db) "block" "none")}}
          [Motion {:style {:y (spring (if (:ui/detailed-view @db) -320 0))}}
           (fn [x] (reagent/create-element Child-comp #js {} x))]]))

(defn root
  []
  (let [toggle-items ["Live" "Portfolio" "Alerts" "Settings"]]
    [:div#container [Header toggle-items] [Routes] [DetailedView]]))

(reagent/render [root] (js/document.getElementById "root"))
