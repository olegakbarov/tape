(ns app.renderer
  (:require [reagent.core :as reagent]
            [mount.core :as mount]
            [app.db :refer [router]]
            [app.api :refer [fetch-state!]]
            [app.actions.storage :refer [read-data-file!]]
            [app.actions.ui :refer [to-screen]]
            [app.eventloop :refer [start-offline-watch-loop!]]
            [app.actions.ntf :refer [ntf-gone-offline
                                     ntf-gone-online]]
            [app.screens.live :refer [live-board]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]))

(enable-console-print!)

(defn init
  []
  (fetch-state!)
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
