(ns app.db
  (:require [reagent.core :as r]))

(defonce db
  (r/atom
    {:ui { :screen :markets
           :sort :asc}

     :data []}))


