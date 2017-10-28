  (ns app.db
  (:require [reagent.core :as r]
            [clojure.walk]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defonce router
  (r/atom
   {:screen :live}))

(defonce db
  (r/atom
    {
     :ui/detailed-view nil

     :ui/current-filter nil

     :settings {:pairs-view :images}

     :portfolio []

     :favorites [[:bitfinex :BTC-USD]
                 [:yobit :BTC-RUB]]

     :markets {}}))

     ; :markets {:bitfinex {:BTC-USD {}
     ;                      :LTC-USD {}}
     ;           :yobit {:BTC-RUB {}
     ;                   :BTC-USD {}
     ;                   :LTC-USD {}
     ;                   :LTC-RUB {}}
     ;           :cex {:BTC-RUB {}
     ;                 :BTC-USD {}}}}))

(defn process-ws-event [t]
 (clojure.walk/keywordize-keys
  (into {}
   (for [[k v] t]
        [(->kebab-case k) v]))))

(defn update-ticker! [ticker]
  (let [t (process-ws-event ticker)
        {:keys [market currency-pair]} t]
    (swap! db assoc-in [:markets (keyword market) (keyword currency-pair)] t)))

