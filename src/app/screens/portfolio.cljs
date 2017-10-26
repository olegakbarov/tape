(ns app.screens.portfolio
  (:require [reagent.core :as r]
            [app.components.header :refer [Header]]
            [app.components.form :refer [input-group]]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.logic.curr :refer [get-market-names
                                    get-crypto-currs]]
            [clojure.string :as s]
            [cljsjs.react-motion]
            [app.components.ui :refer [Wrapper]]))

(defn portfolio-list []
  (let [folio (:portfolio @db)]
   [:div
    (if (> (count folio) 0)
      (for [row folio]
        (let [{:keys [name amount market]} row]
          ^{:key (str name "x" amount "x" market)}
          [:div.folio_row
            [:div.item
              [:div.name name
                [:span.dynamics.green "  ▲ 0.00 %"]]
              [:div.market market]]
            [:div.item.amount amount]])))]))

(def fields
   [{:name "amount"
     :placeholder "1000"}
    {:name "currency"
     :placeholder "CURR"
     :options (get-crypto-currs)}
    {:name "market"
     :placeholder "MRKT"
     :options (get-market-names)}])

(defn portfolio []
  [Wrapper
    [input-group fields]
    [portfolio-list]])

