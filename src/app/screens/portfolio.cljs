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

;; TODO: dont re-render on every ws event
;;
(defn portfolio-list []
 (let [folio (-> @db :user :portfolio)]
  (js/console.log folio)
  [:div
   (if (pos? (count folio))
    (for [row folio]
     (let [{:keys [name amount market]} row]
          ^{:key (str name "x" amount "x" market)}
      [:div.folio_row
       [:div.item
        [:div.name name]
        [:div.market market]]
       [:div.item.amount
        amount
        [:span.price_change "+ 0.00 %"]]])))]))

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

