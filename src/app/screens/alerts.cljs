(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [cljsjs.react-select]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.actions.form :refer [update-alert-form]]
            [app.logic.curr :refer [get-market-names get-all-pair-names]]
            [app.components.ui
             :refer
             [EmptyListCompo InputWrapper Checkbox AmountInput]]))

(defn select-pair
  []
  (let [m (-> @db
              :markets)
        v (-> @db
              :form/alerts
              :currency)
        opts (get-all-pair-names m)
        on-change #(update-alert-form
                    :currency
                    (if % (aget % "value") (update-alert-form :currency "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))
      :onChange on-change}]))

(defn select-market
  []
  (let [m (-> @db
              :markets)
        v (-> @db
              :form/alerts
              :market)
        opts (get-market-names m)
        on-change #(update-alert-form
                    :market
                    (if % (aget % "value") (update-alert-form :market "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))
      :onChange on-change}]))

(defn alerts-list [] [EmptyListCompo "alerts"])

(defn add-alert [])

(defn alerts
  []
  (let [form (-> @db
                 :form/alerts)]
    (fn []
      [:div#wrapper
       [alerts-list]
       [:div.form_wrap
        [InputWrapper "Market" [select-market]]
        [InputWrapper "Currency pair" [select-pair]]
        [AmountInput
         {:value (-> form
                     :amount)}]
        [Checkbox "Repeat alert"]]])))
