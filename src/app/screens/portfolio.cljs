(ns app.screens.portfolio
 (:require [reagent.core :as r]
           [app.components.header :refer [Header]]
           [app.components.form :refer [input-group]]
           [app.actions.ui :refer [to-screen]]
           [app.actions.portfolio :refer [add-item]]
           [app.db :refer [db]]
           [clojure.string :as s]
           [app.actions.portfolio :refer [remove-item
                                          set-editing-item]]
           [app.logic.curr :refer [get-market-names
                                   get-crypto-currs]]
           [app.logic.validation :refer [str->amount
                                         str->item]]))

;; TODO: dont re-render on every ws event
(defn portfolio-list []
 (let [folio (-> @db :user :portfolio vals)]
  [:div
   (if-not (pos? (count folio))
    "You haven't added any records yet"
    (for [row folio]
     (let [{:keys [currency amount market id]} row]
          ^{:key id}
      [:div.folio_row
       [:div.content
        [:div.amount (str currency ": " amount)]
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
   :placeholder "1000"
   :valid-fn str->amount}
  {:name "market"
   :placeholder "MRKT"
   :options (get-market-names (-> @db :markets))
   :valid-fn (partial str->item (get-market-names (-> @db :markets)))}
  {:name "currency"
   :placeholder "CURR"
   :options (get-crypto-currs (-> @db :markets))
   :valid-fn (partial str->item (get-crypto-currs (-> @db :markets)))}])

(defn handle-submit
 "Packs field names with values from input-form"
 [result]
 (let [item (zipmap
             (map #(-> % :name keyword)
                  fields)
             (map :value result))]
  (add-item item)))

(defn portfolio []
 [:div#wrapper
  [portfolio-list]
  [input-group fields handle-submit]])

