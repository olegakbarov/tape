(ns app.db
  (:require [reagent.core :as r]))

(defonce db
  {:ui { :screen :markets
         :sort :asc}

   :data []})


