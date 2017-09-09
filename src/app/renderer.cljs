(ns app.renderer
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.api :refer [start-loop!]]
            [app.actions :as actions]
            [app.config :refer [config]]
            [app.screens.bestprice :refer [bestprice]]
            [app.screens.markets :refer [markets]]
            [app.screens.settings :refer [settings]]
            [app.screens.portfolio :refer [portfolio]]
            [app.screens.alerts :refer [alerts]]))


(defn init []
  (start-loop!)
  (actions/read-local-portfolio!)
  (js/console.log "Started ws listener..."))

(defn debug-panel []
 [:div#debug_panel
   [:button {:on-click #(js/console.log (:markets @db))} "State yo"]
   [:button {:on-click #(actions/test-notif "TEST" "test")} "Notif me"]
   [:button {:on-click #(actions/cache-state)} "Cache me"]])

(defn router []
 (let [curr-screen (-> @db :ui/screen)]
   (condp = curr-screen
     :bestprice [bestprice]
     :markets   [markets]
     :settings  [settings]
     :portfolio [portfolio]
     :alerts    [alerts])))

(defn root []
  [:div {:style {:height "100%"}}
   (when (= (:env config) :dev)
         [debug-panel])
   [router]])

(reagent/render
  [root]
  (js/document.getElementById "container"))
