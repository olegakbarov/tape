(ns app.screens.portfolio
  (:require [clojure.string :as s]
            [reagent.core :as r]
            [goog.object :as gobj]
            [cljsjs.react-select]
            [app.components.header :refer [header]]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.motion :refer [Motion spring presets]]
            [app.components.ui :as ui]
            [app.logic.curr :refer [get-market-names get-crypto-currs]]
            [app.logic.validation :refer
             [str->amount validate-portfolio-record]]
            [app.actions.ui :refer
             [toggle-edit-portfolio-view
              close-detailed-view
              toggle-add-portfolio-view
              close-every-portfolio-view]]
            [app.actions.form :refer
             [update-portfolio-form
              clear-portfolio-form]]
            [app.actions.portfolio :refer
             [create-portfolio-record
              remove-portfolio-record
              get-total-worth]]))

(defn- total-worth
  []
  (fn []
    (let [w (.toFixed (get-total-worth) 2)]
      (if (pos? w) [:div.total_worth (str "$ " w)] [:div]))))

;; TODO: dont re-render on every ws event
(defn portfolio-list
  []
  (let [folio (-> @db
                  :user
                  :portfolio
                  vals)]
    [:div
     (if-not (pos? (count folio))
       [ui/empty-list "portfolio items"]
       (for [row folio]
         (let [{:keys [currency amount market id]} row]
           ^{:key id}
           [:div.row_wrap
            ^{:key "currency"} {:on-click #(toggle-edit-portfolio-view id)}
            [:div.left_cell
             [:div.title (str (name currency) " " amount)]
             [:div.market market]]
            ^{:key "last-ctrls"}
            [:div.right_cell
             [:div.actions]]])))]))

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

(defn item-add
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
    [:div.form_wrap
     [:h1 {:style {:margin "30px 0 50px"}}
      (cond (-> @db
                :ui/portfolio-add-view)
            "Add holding"
            (-> @db
                :ui/portfolio-edit-view)
            "Edit holding")]
     [ui/close
      {:position "absolute"
       :right "20px"
       :top "20px"}
      #(close-every-portfolio-view)]
     [ui/input-wrap "Market" [select-market {:key "market"}]]
     [ui/input-wrap "Currency" [select-curr {:key "currency"}]]
     [ui/text-input
      {:on-change on-change
       :value #(-> @db
                   :form/portfolio
                   :amount)
       :label "amount"}]
     [:div.input_wrapper
      [ui/button
       {:on-click on-submit
        :type "submit"
        :ref nil
        :disabled false
        :color "#000"}
       (cond (-> @db
                 :ui/portfolio-add-view)
             "Add"
             (-> @db
                 :ui/portfolio-edit-view)
             "Save")]]]))

(def height 355)

(defn view
  [{c :children}]
  (let [y (gobj/get c "y")]
    [:div
     {:style {:position "fixed"
              :width "100%"
              :height (str height "px")
              :background-color "#fff"
              :z-index 999
              :border-radius "4px 4px 0 0"
              :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)"
              :-webkit-transform (str "translateY(" y "px)")
              :transform (str "translateY(" y "px)")}}
     [item-add]]))

(def animated-comp (r/reactify-component view))

(defn detailed-view
  []
  (fn []
    (let [open? (or (:ui/portfolio-edit-view @db) (:ui/portfolio-add-view @db))]
      [:div
       {:style {:position "absolute"
                :bottom 0
                :display (if open? "block" "none")}}
       [Motion
        {:style {:y (spring (if open? (- height) 0))}}
        (fn [x] (r/create-element animated-comp #js {} x))]])))

(defn add-btn
  [s]
  [:div
   {:style {:padding "0 10px"
            :width "100%"}}
   [ui/button
    {:on-click #(do (reset! s false) (toggle-add-portfolio-view))
     :type "submit"
     :ref nil
     :disabled false
     :color "#000"}
    "Add"]])

(defn portfolio
  []
  (fn []
    (let [show-btn (r/atom true)]
      [:div.portfolio_container
       [header]
       [:div.portfolio_items_wrapper]
       [total-worth]
       [portfolio-list]
       [:div.portfolio_toolbar
        (when @show-btn [add-btn show-btn])]
       [detailed-view]])))
