(ns app.renderer
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.screens.bestprice :refer [bestprice]]
            [app.screens.markets :refer [markets]]))

(defn init []
  (js/console.log "Starting Application"))

(defn router []
  (let [curr-screen (-> @db :ui :screen)]
    (condp = curr-screen
      :bestprice [bestprice]
      :markets [markets])))

(reagent/render
  [router]
  (js/document.getElementById "container"))
