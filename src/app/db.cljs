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

     :portfolio []

     :favorites []
                ;;[[:bitfinex :BTC-USD]
                ;; [:yobit :BTC-RUB]

     :markets {}}))

