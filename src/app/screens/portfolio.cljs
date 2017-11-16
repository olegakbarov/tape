(ns app.screens.portfolio
  (:require [reagent.core :as r]
            [app.components.header :refer [Header]]
            [app.components.form :refer [input-group]]
            [app.actions.ui :refer [to-screen]]
            [app.actions.portfolio :refer [add-item]]
            [app.db :refer [db]]
            [clojure.string :as s]
            [cljsjs.react-motion]
            [app.components.ui :refer [Wrapper]]
            [app.actions.portfolio :refer [remove-item
                                           set-editing-item]]
            [app.logic.curr :refer [get-market-names
                                    get-crypto-currs]]))

;; TODO: dont re-render on every ws event
;;
(defn portfolio-list []
 (let [folio (-> @db :user :portfolio vals)]
  [:div
   (if (pos? (count folio))
    (for [row folio]
     (let [{:keys [currency amount market id]} row]
          ^{:key id}
      [:div.folio_row
       [:div.content
        [:div.amount amount]
        [:div.title currency]
        [:div " on "]
        [:div.market market]]
       [:div.actions
        [:div.edit
         {:on-click #(set-editing-item id)}
         "edit"]
        [:div.delete
         {:on-click #(remove-item id)}
         "delete"]]])))]))

(def fields
 [{:name "amount"
   :placeholder "1000"}
  {:name "currency"
   :placeholder "CURR"
   :options (get-crypto-currs)}
  {:name "market"
   :placeholder "MRKT"
   :options (get-market-names)}])

(defn handle-submit
 "Packs field names with values from input-form"
 [result]
 (let [item (zipmap
             (map #(-> % :name keyword)
                 fields)
             (map :value result))]
  (add-item item)))

(defn portfolio []
 [Wrapper
  [input-group fields handle-submit]
  [portfolio-list]])

