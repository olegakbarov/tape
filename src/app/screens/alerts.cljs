(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.components.profile :refer [header]]
            [app.components.dropdown :refer [dropdown]]
            [app.actions :as actions]
            [app.db :refer [db]]
            [app.logic :refer [get-market-names
                               get-crypto-currs]]
            [goog.functions]
            [app.components.ui :refer [Wrapper
                                       Container]]))


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

;; :on-click #(update-if-valid name market amount)}]]]
;;
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
           :value @market}]]
       (when @curr-dropdown
        [dropdown
          {:items (get-crypto-currs)
           :value name
           :handler on-curr-change}])
       (when @market-dropdown
        [dropdown
          {:items (get-market-names)
           :value market
           :handler on-market-change}])
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
        (when @curr-dropdown
          [dropdown
            {:items (get-crypto-currs)
             :value name
             :handler on-curr-change}])
        (when @market-dropdown
          [dropdown
            {:items (get-market-names)
             :value market
             :handler on-market-change}])
        [:input.input_item
          {:type "text"
           :placeholder "MRKT"
           :on-focus #(reset! market-dropdown true)
           :on-blur (goog.functions.debounce
                      #(reset! market-dropdown false)
                      10)
           :value @market}]]])))

(defn alerts []
  [Container
    [header]
    [Wrapper
      [add-rec]]])
