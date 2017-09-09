(ns app.db
  (:require [reagent.core :as r]))

(defonce db
  (r/atom
    {:ui/screen :bestprice
     :ui/bestprice {:sort :asc}
     :ui/expanded-row []

     :settings {:pairs-view :images}

     :portfolio []

     :markets {"bitfinex" {"BTC-USD" {}
                           "LTC-USD" {}}
               "yobit" {"BTC-RUB" {}
                        "BTC-USD" {}
                        "LTC-USD" {}
                        "LTC-RUB" {}}
               "cex" {"BTC-RUB" {}
                      "BTC-USD" {}}}}))

