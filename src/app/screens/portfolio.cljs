(ns app.screens.portfolio
  (:require [reagent.core :as r]
            [app.components.profile :refer [header]]
            [app.components.dropdown :refer [dropdown]]
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


(defn add-rec []
  (let [name (r/atom "")
        amount (r/atom "")
        market (r/atom "")
        curr-dropdown (r/atom false)
        market-dropdown (r/atom false)
        on-market-change #(reset! market %)
        on-curr-change #(reset! name %)]
    (fn []
      [:div
        [:div.add_rec_wrapper
         [:input.input_item
          {:type "text"
           :autoFocus true
           :placeholder "1000"
           :value @amount
           :on-change #(update-amount (-> % .-target .-value) amount)}]
         [:input.input_item
          {:type "text"
           :placeholder "CURR"
           :on-focus #(reset! curr-dropdown true)
           :on-blur (goog.functions.debounce
                      #(reset! curr-dropdown false)
                      10)
           :value @name}]
         [:input.input_item
          {:type "text"
           :placeholder "MRKT"
           :on-focus #(reset! market-dropdown true)
           :on-blur (goog.functions.debounce
                      #(reset! market-dropdown false)
                      10)
           :value @market}]
         [:div
          [:img.folio_plus
           {:src (str "icons/plus-circle.svg")
            :on-click #(update-if-valid name market amount)}]]]
       (when @curr-dropdown
        [dropdown
          {:items (get-crypto-currs)
           :value name
           :handler on-curr-change}])
       (when @market-dropdown
        [dropdown
          {:items (get-market-names)
           :value market
           :handler on-market-change}])])))

(defn portfolio-list []
  (let [folio (:portfolio @db)]
   [:div
    [:h3.assets_title " Your assets"]
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

(defn portfolio []
  [:div
    [header]
    [:div#portfolio_wrapper
      [add-rec]
      [portfolio-list]]])
      ; [:button {:on-click #(actions/save-portfolio)} "Save folio"]
      ; [:button {:on-click #(actions/read-local-portfolio!)} "Read folio"]
      ; [:button {:on-click #(actions/log-folio)} "Log folio"]]])
