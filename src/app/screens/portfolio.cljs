(ns app.screens.portfolio
  (:require [reagent.core :as r]
            [app.components.profile :refer [header]]
            [app.actions :as actions]
            [clojure.string :as s]))

(defn custom-parse-float [strng]
  (let [val (if (or (s/starts-with? strng ".")
                    (s/starts-with? strng ","))
                (str "0" strng)
                strng)]
    (.parseFloat js/window val)))

(defn update-name [val name]
  (reset! name val))

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

(defn add-rec []
  (let [name (r/atom "")
        amount (r/atom "")
        market (r/atom "")]
    (fn []
      [:div
       [:input {:type "text"
                :placeholder "BTC, ETH ..."
                :value @name
                :on-change #(update-name (-> % .-target .-value) name)}]
       [:input {:type "text"
                :placeholder "1000"
                :value @amount
                :on-change #(update-amount (-> % .-target .-value) amount)}]
       [:input {:type "text"
                :placeholder "CEX, BITTREX..."
                :value @market
                :on-change #(update-market (-> % .-target .-value) market)}]
       [:button {:on-click #(when-let [rec (valid-rec? {:name @name
                                                        :amount (custom-parse-float @amount)
                                                        :market @market})]
                              (actions/add-record rec)
                              (reset! name "")
                              (reset! amount "")
                              (reset! market ""))}
        "Add"]])))

(defn portfolio []
  [:div
    [header]
    [:div#portfolio_wrapper
      [add-rec]
      [:button {:on-click #(actions/save-portfolio)} "Save folio"]
      [:button {:on-click #(actions/read-portfolio)} "Read folio"]
      [:button {:on-click #(actions/log-folio)} "Log folio"]]])
