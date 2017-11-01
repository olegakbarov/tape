(ns app.db
  (:require [reagent.core :as r]))

(defonce router
  (r/atom
   {:screen :live}))

(defonce db
  (r/atom
    {
     :ui/detailed-view nil
     :ui/current-filter nil
     :ui/filter-q ""

     :user/portfolio []
     :user/favorites []
     :user/settings {}

     :markets {}}))

