(ns app.logic.curr
  (:require [clojure.string :as s]
            [clojure.test :refer [is]]
            [app.logic.curr :refer [best-pairs]]))

(def markets
  {"bitfinex" {"BTC-USD" {:high 5877
                          :sell 5518.4
                          :buy 5510.5
                          :vol-cur 0
                          :low 5450
                          :avg 0
                          :market "bitfinex"
                          :timestamp 1508085038.397682
                          :currency-pair "BTC-USD"
                          :last 5511.4
                          :vol 50536.88303885}
               "LTC-USD" {:high 70.364
                          :sell 62.98
                          :buy 62.97
                          :vol-cur 0
                          :low 61.76
                          :avg 0
                          :market "bitfinex"
                          :timestamp 1508085035.4137683
                          :currency-pair "LTC-USD"
                          :last 62.97
                          :vol 860628.01110068}}
   "yobit" {"BTC-RUB" {:high 317566
                       :sell 314400
                       :buy 314386
                       :vol-cur 40.531035
                       :low 308381.5
                       :avg 312973.75
                       :market "yobit"
                       :timestamp 1508085039
                       :currency-pair "BTC-RUB"
                       :last 314400
                       :vol 12690123}
            "BTC-USD" {:high 5700
                       :sell 5567
                       :buy 5553
                       :vol-cur 35.7734
                       :low 5474
                       :avg 5587
                       :market "yobit"
                       :timestamp 1508084820
                       :currency-pair "BTC-USD"
                       :last 5568.9999
                       :vol 199621.81}
            "LTC-USD" {:high 68.35
                       :sell 63.701308
                       :buy 63.447265
                       :vol-cur 310.55603
                       :low 61.709101
                       :avg 65.029551
                       :market "yobit"
                       :timestamp 1508084957
                       :currency-pair "LTC-USD"
                       :last 63.447265
                       :vol 20034.044}
            "LTC-RUB" {:high 3802.1879
                       :sell 3621.6789
                       :buy 3570.1124
                       :vol-cur 287.85515
                       :low 3407.978
                       :avg 3605.083
                       :market "yobit"
                       :timestamp 1508084908
                       :currency-pair "LTC-RUB"
                       :last 3578.6806
                       :vol 1041818.7}}
   "cex" {"BTC-RUB" {:high 364999.98
                     :sell 332999.99
                     :buy 323000
                     :vol-cur 1.57258202
                     :low 322500.3
                     :avg 0
                     :market "cex"
                     :timestamp 1508085052
                     :currency-pair "BTC-RUB"
                     :last 332999.99
                     :vol 87.34974424}
          "BTC-USD" {:high 5869
                     :sell 5599.9999
                     :buy 5592.3423
                     :vol-cur 1936.0460133
                     :low 5520
                     :avg 0
                     :market "cex"
                     :timestamp 1508085054
                     :currency-pair "BTC-USD"
                     :last 5592.2
                     :vol 38068.48557385}}})

(def empty-markets
  {"bitfinex" {"BTC-USD" {}
               "LTC-USD" {}}
   "yobit" {"BTC-RUB" {}
            "BTC-USD" {}
            "LTC-USD" {}
            "LTC-RUB" {}}
   "cex" {"BTC-RUB" {}
          "BTC-USD" {}}})

;; TODO!

; (best-pairs empty-markets)
; (best-pairs markets)
