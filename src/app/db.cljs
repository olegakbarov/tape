(ns app.db
  (:require [reagent.core :as r]))

(defonce db
  (r/atom
    {:ui { :screen :markets
           :sort :asc}

     :markets {"bitfinex" {"BTC-USD" {}
                           "LTC-USD" {}}
               "yobit" {"BTC-RUB" {}
                        "BTC-USD" {}
                        "LTC-USD" {}
                        "LTC-RUB" {}}
               "cex" {"BTC-RUB" {}
                      "BTC-USD" {}}}}))


