(ns app.screens.portfolio
  (:require
   [clojure.string :as s]
   [reagent.core :as r]
   [app.components.header :refer [Header]]
   [app.actions.ui :refer [to-screen]]
   [app.db :refer [db]]
   [app.logic.curr :refer [get-market-names get-crypto-currs]]
   [app.logic.validation :refer [str->amount validate-portfolio-record]]
   [cljsjs.react-select]
   [app.actions.form :refer [update-portfolio-form clear-portfolio-form]]
   [app.actions.portfolio
    :refer
    [create-portfolio-record remove-portfolio-record get-total-worth]]
   [app.components.ui
    :refer
    [EmptyListCompo InputWrapper Checkbox Button TextInput]]))

(defn total-worth
  []
  (fn []
    (let [w (.toFixed (get-total-worth) 2)]
      (if (> w 0) [:div.total_worth (str "$ " w)] [:div]))))

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
           [:div.row_wrap
            ^{:key "currency"}
            [:div.left_cell
             [:div.title (str (name currency) " " amount)]
             [:div.market market]]
            ^{:key "last-ctrls"}
            [:div.right_cell
             [:div.actions
              [:div.edit {:on-click #(js/console.log "nimp")} "edit"]
              [:div.delete
               {:on-click #(remove-portfolio-record id)}
               "delete"]]]])))]))

(defn select-market
  []
  (let [m (-> @db
              :markets)
        v (-> @db
              :form/portfolio
              :market)
        opts (get-market-names m)
        on-change #(update-portfolio-form
                    :market
                    (if % (aget % "value") (update-portfolio-form :market "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))
      :onChange on-change}]))

(defn select-curr
  []
  ;; TODO: only currency available on selected market
  (let [m (-> @db
              :markets)
        v (-> @db
              :form/portfolio
              :currency)
        opts (get-crypto-currs m)
        on-change
        #(update-portfolio-form
          :currency
          (if % (aget % "value") (update-portfolio-form :currency "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))
      :onChange on-change}]))

(defn portfolio
  []
  (let [on-change (fn [e]
                    (let [v (-> e
                                .-target
                                .-value)]
                      (update-portfolio-form :amount (str->amount v))))
        on-submit #(when-let [a (validate-portfolio-record (->
                                                             @db
                                                             :form/portfolio))]
                    (do (clear-portfolio-form) (create-portfolio-record a)))]
    (fn []
      [:div#wrapper
       [total-worth]
       [portfolio-list]
       [:div.form_wrap
        [InputWrapper "Market" [select-market {:key "market"}]]
        [InputWrapper "Currency" [select-curr {:key "currency"}]]
        [TextInput
         {:on-change on-change
          :value #(-> @db
                      :form/portfolio
                      :amount)
          :label "amount"}]
        [:div.input_wrapper
         [Button
          {:on-click on-submit
           :type "submit"
           :ref nil
           :disabled false
           :color "#000"}
          "Add"]]]])))
