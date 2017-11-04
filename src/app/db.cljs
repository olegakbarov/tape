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
     :user/notifs [{:id 1
                    :market :bitfinex
                    :pair :BTC-LTC
                    :change :below
                    :price "5000"
                    :active true
                    :repeat true}]

     :markets {}}))

