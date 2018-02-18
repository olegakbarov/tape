(ns app.renderer
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.api :refer [listen-ws! fetch-state!]]
            [app.eventloop :refer [start-title-loop! start-offline-watch-loop!]]
            [app.actions.storage :refer [read-data-file!]]
            [app.actions.ui :refer [to-screen]]
            [app.actions.ntf :refer [ntf-gone-offline ntf-gone-online]]
            [app.config :refer [config]]
            [app.screens.live :refer [live-board]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]
            [app.screens.detailed :refer [pair-detailed]]
            [app.components.header :refer [Header]]
            [app.motion :refer [Motion spring presets]]
            [mount.core :as mount]
            [goog.object :as gobj]))

(enable-console-print!)

(defn init
  []
  (fetch-state!)
  (start-offline-watch-loop! ntf-gone-online ntf-gone-offline)
  (mount/start))

(def height 400)

(defn Routes
  []
  (let [s (-> @router
              :screen)]
    (condp = s
      :live [live-board]
      :portfolio [portfolio]
      :settings [settings]
      :alerts [alerts])))

(defn view
  [{c :children}]
  (let [y (gobj/get c "y")]
    [:div
     {:style {:position "fixed"
              :width "321px"
              :height (str height "px")
              :background-color "#fff"
              :z-index 999
              :border-radius "4px 4px 0 0"
              :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)"
              :-webkit-transform (str "translateY(" y "px)")
              :transform (str "translateY(" y "px)")}}
     [pair-detailed]]))

(def animated-comp (reagent/reactify-component view))

(defn DetailedView
  []
  (fn [] [:div
          {:style {:position "absolute"
                   :bottom 0
                   :display (if (:ui/detailed-view @db) "block" "none")}}
          [Motion
           {:style {:y (spring (if (:ui/detailed-view @db) (- height) 0))}}
           (fn [x] (reagent/create-element animated-comp #js {} x))]]))

(defn root
  []
  [:div#container
   [Header]
   [Routes]
   [DetailedView]])

(reagent/render [root] (js/document.getElementById "root"))
