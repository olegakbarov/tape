(ns app.screens.portfolio
  (:require [reagent.core :as r]
            [app.components.profile :refer [header]]
            [app.actions :as actions]
            [app.db :refer [db]]
            [clojure.string :as s]))

(defn custom-parse-float [strng]
  (let [val (if (or (s/starts-with? strng ".")
                    (s/starts-with? strng ","))
                (str "0" strng)
                strng)]
    (.parseFloat js/window val)))

(defn update-name [val name]
  ;; check for name in db
  ;; autosuggest?
  (reset! name (.toUpperCase val)))

(defn update-amount [val amount]
  (let [valid-chars "1234567890,."
        l (last val)]
    (if (= nil l)
        (reset! amount ""))
        (when (s/includes? valid-chars l)
              (reset! amount val))))

(defn update-market [val market]
  (reset! market val))

(defn valid-rec? [rec]
  (let [{:keys [name amount]} rec]
    (if
     (and (> amount 0) (not (s/blank? name)))
     rec
     false)))

(defn update-if-valid [name market amount]
  (when-let [rec (valid-rec? {:name @name
                              :amount (custom-parse-float @amount)
                              :market @market})]
    (actions/add-record rec)
    (reset! name "")
    (reset! amount "")
    (reset! market "")))

(defn add-rec []
  (let [name (r/atom "")
        amount (r/atom "")
        market (r/atom "")]
    (fn []
      [:div
        [:div.add_rec_wrapper
         [:input.input_item
          {:type "text"
           :placeholder "BTC"
           :autoFocus true
           :value @name
           :on-change #(update-name (-> % .-target .-value) name)}]
         [:input.input_item
          {:type "text"
           :placeholder "1000"
           :value @amount
           :on-change #(update-amount (-> % .-target .-value) amount)}]
         [:input.input_item
          {:type "text"
           :placeholder "CEX"
           :value @market
           :on-change #(update-market (-> % .-target .-value) market)}]
         [:div
          [:img.folio_plus
           {:src (str "icons/plus-circle.svg")
            :on-click #(update-if-valid name market amount)}]]]])))

(defn portfolio-list []
  (let [folio (:portfolio @db)]
   [:div
    [:div.folio_table_header
      [:div.item "Currency"]
      [:div.item "Amount"]
      [:div.item "Market"]]
    (if (> (count folio) 0)
      (for [row folio]
        (let [{:keys [name amount market]} row]
          ^{:key name}
          [:div.folio_row
            [:div.item name]
            [:div.item amount]
            [:div.item market]])))]))

(defn portfolio []
  [:div
    [header]
    [:div#portfolio_wrapper
      [add-rec]
      [portfolio-list]]])
      ; [:button {:on-click #(actions/save-portfolio)} "Save folio"]
      ; [:button {:on-click #(actions/read-local-portfolio!)} "Read folio"]
      ; [:button {:on-click #(actions/log-folio)} "Log folio"]]])
