(ns app.screens.portfolio
  (:require [reagent.core :as r]
            [app.components.header :refer [Header]]
            [app.actions.ui :refer [to-screen]]
            [app.actions.portfolio :refer [add-item]]
            [app.db :refer [db]]
            [clojure.string :as s]
            [app.actions.portfolio :refer [remove-item set-editing-item]]
            [app.logic.curr :refer [get-market-names get-crypto-currs]]
            [app.logic.validation :refer [str->amount str->item]]
            [app.components.ui :refer [EmptyListCompo]]
            [app.actions.form :refer [update-portfolio-form]]))

;; TODO: dont re-render on every ws event
(defn portfolio-list
  []
  (let [folio (-> @db
                  :user
                  :portfolio
                  vals)]
    [:div
     (if-not (pos? (count folio))
       [EmptyListCompo "portfolio items"]
       (for [row folio]
         (let [{:keys [currency amount market id]} row]
           ^{:key id}
           [:div.folio_row
            [:div.content
             [:div.amount (str currency ": " amount)]
             [:div.market market]]
            [:div.actions
             [:div.edit {:on-click #(set-editing-item id)} "edit"]
             [:div.delete {:on-click #(remove-item id)} "delete"]]])))]))

(defn handle-submit
  "Packs field names with values from input-form"
  []
  (let [form
        (-> @db
            :form/portfolio)]))

(defn portfolio [] [:div#wrapper [portfolio-list]])
