(ns app.renderer
  (:require [reagent.core :as reagent]
            [mount.core :as mount]
            [klang.core :refer-macros [info! warn! erro! crit! fata! trac!]]
            [app.db :refer [router]]
            [app.actions.api :refer [fetch-state!]]
            [app.actions.storage :refer [read-data-file!]]
            [app.actions.ui :refer
             [to-screen
              update-window-size]]
            [app.eventloop :refer [start-offline-watch-loop!]]
            [app.actions.ntf :refer
             [ntf-gone-offline
              ntf-gone-online]]
            [app.screens.live :refer [live-board]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]
            [app.config :refer [config]]
            [cljsjs.raven]))

(enable-console-print!)

(defn init
  []
  (when-let [slug (:sentry config)]
    (do (-> js/Raven
            (.config slug)
            (.install))
        (info! (str "Sentry endpoint: " slug))))
  (fetch-state!)
  (update-window-size (.-innerWidth js/window)
                      (.-innerHeight js/window))
  (start-offline-watch-loop! ntf-gone-online ntf-gone-offline)
  (mount/start))

(defn root
  []
  (let [s (-> @router
              :screen)]
    [:div#container
     (condp = s
       :live [live-board]
       :portfolio [portfolio]
       :settings [settings]
       :alerts [alerts])]))

(reagent/render [root] (js/document.getElementById "root"))
