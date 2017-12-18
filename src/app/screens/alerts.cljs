(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [cljsjs.react-select]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.actions.form :refer [update-alert-form clear-alert-form]]
            [app.logic.curr :refer [get-market-names get-all-pair-names]]
            [app.logic.validation :refer [str->amount validate-alert]]
            [app.actions.alerts :refer [create-alert]]
            [app.components.ui
             :refer
             [EmptyListCompo InputWrapper Checkbox Button TextInput]]))

(defn select-pair
  []
  (let [m (-> @db
              :markets)
        v (-> @db
              :form/alert
              :pair)
        opts (get-all-pair-names m)
        on-change #(update-alert-form
                    :pair
                    (if % (aget % "value") (update-alert-form :pair "")))]
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
              :form/alert
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

(defn select-repeat
  []
  (let [opts ["Yes" "No"]
        v (-> @db
              :form/alert
              :repeat)
        on-change #(update-alert-form
                    :repeat
                    (if % (aget % "value") (update-alert-form :repeat "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))
      :onChange on-change}]))

(defn alert-items
  []
  (fn []
    (let [alerts (-> @db
                     :user
                     :alerts
                     vals)]
      [:div
       (for [a alerts
             :let [{:keys [id amount repeat]} a]]
         ^{:key id}
         [:div.row_wrap
          [:div.left_cell
           [:div.title
            (-> a
                :pair
                keyword)]
           [:div.market
            (-> a
                :market
                keyword)]]
          ^{:key "last-price"} [:div.right_cell [:span amount]]])])))

(defn alerts-list
  []
  (let [alerts (-> @db
                   :user
                   :alerts)]
    (if-not (pos? (count alerts)) [EmptyListCompo "alerts"] [alert-items])))

(defn alerts
  []
  (let [on-change (fn [e]
                    (let [v (-> e
                                .-target
                                .-value)]
                      (update-alert-form :amount (str->amount v))))
        on-submit #(when-let [a (validate-alert (-> @db
                                                    :form/alert))]
                    (do (clear-alert-form) (create-alert a)))]
    (fn []
      [:div#wrapper
       [alerts-list]
       [:div.form_wrap
        [InputWrapper "Market" [select-market {:key "market"}]]
        [InputWrapper "Currency pair" [select-pair {:key "pair"}]]
        [TextInput
         {:on-change on-change
          :value #(-> @db
                      :form/alert
                      :amount)}]
        [InputWrapper "Repeat alert" [select-repeat {:key "pair"}]]
        [:div.input_wrapper
         [Button
          {:on-click on-submit
           :type "submit"
           :ref nil
           :disabled false
           :color "#000"}
          "Add"]]]])))
