(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [goog.object :as gobj]
            [cljsjs.react-select]
            [app.motion :refer [Motion spring presets]]
            [app.components.header :refer [header]]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.actions.alerts :refer [create-alert]]
            [app.components.ui :as ui]
            [app.actions.form :refer [update-alert-form clear-alert-form]]
            [app.logic.curr :refer [get-market-names get-all-pair-names]]
            [app.logic.validation :refer [str->amount validate-alert]]
            [app.actions.ui :refer [open-detailed-view
                                    close-detailed-view]]))

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
                    (if % (aget % "value")
                          (update-alert-form :pair "")))]
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
                    (if % (aget % "value")
                          (update-alert-form :market "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))
      :onChange on-change}]))

(defn select-repeat
  []
  (let [opts [["Yes" true]
              ["No" false]]
        v (-> @db
              :form/alert
              :repeat)
        on-change #(update-alert-form
                    :repeat
                    (if % (aget % "value")
                          (update-alert-form :repeat "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [(last %) (first %)]) opts))
      :onChange on-change}]))

(defn alert-items
  []
  (fn []
    (let [show-btn (r/atom true)
          alerts (-> @db
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
    (if-not (pos? (count alerts))
            [ui/empty-list "alerts"]
            [alert-items])))

(defn add-folio-item []
  (let [on-change (fn [e]
                    (let [v (-> e
                                .-target
                                .-value)]
                      (update-alert-form :amount (str->amount v))))
        on-submit #(when-let [a (validate-alert (-> @db
                                                    :form/alert))]
                    (do (clear-alert-form) (create-alert a)))]
     [:div.form_wrap
      [:h1 {:style {:margin "30px 0 50px"}} "Add alert"]
      [ui/close {:position "absolute"
                 :right "20px"
                 :top "20px"}
                close-detailed-view]
      [ui/input-wrap "Market" [select-market {:key "market"}]]
      [ui/input-wrap "Currency pair" [select-pair {:key "pair"}]]
      [ui/text-input
       {:on-change on-change
        :label "amount"
        :value #(-> @db
                    :form/alert
                    :amount)}]
      [ui/input-wrap "Repeat alert" [select-repeat {:key "pair"}]]
      [:div.input_wrapper
       [ui/button
        {:on-click on-submit
         :type "submit"
         :ref nil
         :disabled false
         :color "#000"}
        "Add"]]]))

(def height 420)

(defn view
  [{c :children}]
  (let [y (gobj/get c "y")]
    [:div
     {:style {:position "fixed"
              :width "321px"
              :height (str height "px")
              :background-color "#fff"
              :z-index 999
              :border-radius "4px 4px 0 0"
              :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)"
              :-webkit-transform (str "translateY(" y "px)")
              :transform (str "translateY(" y "px)")}}
     [add-folio-item]]))

(def animated-comp (r/reactify-component view))

(defn detailed-view
  []
  (fn []
    [:div
      {:style {:position "absolute"
               :bottom 0
               :display (if (:ui/detailed-view @db) "block" "none")}}
      [Motion
       {:style {:y (spring (if (:ui/detailed-view @db) (- height) 0))}}
       (fn [x] (r/create-element animated-comp #js {} x))]]))

(defn add-btn [s]
  [:div {:style {:padding "0 10px"
                 :width "100%"}}
    [ui/button
     {:on-click #(do (reset! s false)
                     (open-detailed-view "row" "kek"))
      :type "submit"
      :ref nil
      :disabled false
      :color "#000"}
     "Add"]])

(defn alerts
  []
  (fn []
   (let [show-btn (r/atom true)]
     [:div.container_100
      [header]
      [:div.items_wrapper_flex
       [alerts-list]
       (when @show-btn [add-btn show-btn])]
      [detailed-view]])))
