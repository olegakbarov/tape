(ns app.logic.ui
  (:require [app.db :refer [chart-data]]))

(defn get-chart-points
  [market pair]
  (if (and market pair)
      (get-in @chart-data [market pair])
      nil))
