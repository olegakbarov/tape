(ns app.screens.portfolio
  (:require [reagent.core :as r]
            [app.components.profile :refer [header]]
            [app.components.form :refer [input-group]]
            [app.actions :as actions]
            [app.db :refer [db]]
            [app.logic :refer [get-market-names
                               get-crypto-currs]]
            [clojure.string :as s]
            [cljsjs.react-motion]
            [goog.functions]))

(defn custom-parse-float [strng]
  (let [val (if (or (s/starts-with? strng ".")
                    (s/starts-with? strng ","))
                (str "0" strng)
                strng)]
    (.parseFloat js/window val)))

;; TODO move to utils
(defn update-amount [val amount]
  (let [valid-chars "1234567890,."
        l (last val)]
    (if (= nil l)
        (reset! amount "")
        (when (s/includes? valid-chars l)
              (reset! amount val)))))

(defn update-market [val market]
  (reset! market val))

(defn valid-rec? [rec]
  (let [{:keys [name amount]} rec]
    (if
     (and (> amount 0) (not (s/blank? name)))
     rec
     false)))

(defn update-if-valid [name market amount]
  ;; TODO check if curr/market pair exists
  (when-let [rec (valid-rec? {:name @name
                              :amount (custom-parse-float @amount)
                              :market @market})]
    (actions/add-record rec)
    (actions/save-portfolio)
    (reset! name "")
    (reset! amount "")
    (reset! market "")))

(defn portfolio-list []
  (let [folio (:portfolio @db)]
   [:div
    [:h1.assets_title " Your assets"]
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

(defn validate-amount [v]
  (let [valid-chars "1234567890,."]
    (if (s/includes? valid-chars v)
        true
        false)))

(def input-configs
   [{:name "amount"
     :placeholder "1000"}
    {:name "currency"
     :placeholder "CURR"
     :options (get-crypto-currs)}
    {:name "market"
     :placeholder "MRKT"
     :options (get-market-names)}])

(defn portfolio []
  [:div
    [header]
    [:div#portfolio_wrapper
      [input-group input-configs]
      [portfolio-list]]])
      ; [:button {:on-click #(actions/save-portfolio)} "Save folio"]
      ; [:button {:on-click #(actions/read-local-portfolio!)} "Read folio"]
      ; [:button {:on-click #(actions/log-folio)} "Log folio"]]])
