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
            [app.components.ui :refer [Wrapper
                                       Container
                                       Icon]]))

; (defn custom-parse-float [strng]
;   (let [val (if (or (s/starts-with? strng ".")
;                     (s/starts-with? strng ","))
;                 (str "0" strng)
;                 strng)]
;     (.parseFloat js/window val)))

; (defn update-market [val market]
;   (reset! market val))

; (defn valid-rec? [rec]
;   (let [{:keys [name amount]} rec]
;     (if
;      (and (> amount 0) (not (s/blank? name)))
;      rec
;      false)))

; (defn update-if-valid [name market amount]
;   ;; TODO check if curr/market pair exists
;   (when-let [rec (valid-rec? {:name @name
;                               :amount (custom-parse-float @amount)
;                               :market @market})]
;     (actions/add-record rec)
;     (actions/save-portfolio)
;     (reset! name "")
;     (reset! amount "")
;     (reset! market "")))

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
 (let [toggle-items ["Portfolio" "Alerts"]]
  [Container
   [Header
    [Icon
     #(to-screen :bestprice)
     "icons/arrow-left.svg"]
    [Icon
      #(to-screen :settings)
      "icons/settings.svg"]
    toggle-items]
   [Wrapper
     [input-group input-configs]
     [portfolio-list]]]))

