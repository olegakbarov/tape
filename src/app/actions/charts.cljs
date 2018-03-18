(ns app.actions.charts
  (:require [clojure.walk]
            [reagent.core :as r]
            [app.db :refer [db chart-data]]))

(defn select-chart-points
  [market pair]
  (if (and market pair) (get-in @chart-data [market pair])))

(defn set-fetching-chart [m p]
  (swap! db assoc :ui/current-graph [m p]))

