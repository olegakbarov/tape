(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [goog.object :as gobj]
            [cljsjs.react-select]
            [app.motion :refer [Motion spring presets]]
            [app.components.header :refer [header]]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.components.ui :as ui]
            [app.actions.form :refer
             [update-alert-form
              clear-alert-form]]
            [app.logic.curr :refer
             [get-market-names
              get-all-pair-names]]
            [app.logic.validation :refer [str->amount]]
            [app.actions.form :refer
             [update-alert-form
              clear-alert-form]]
            [app.actions.alerts :refer
             [create-alert-record
              remove-alert-record
              update-alert-record]]
            [app.actions.ui :refer
             [toggle-edit-alert-view
              open-add-alert-view
              close-add-alert-view
              close-every-alert-view]]))


;; EVENT HANDLERS ARE COMPOSED OF GRANULAR API CALLS

(defn handle-delete
  []
  (remove-alert-record (-> @db
                           :ui/alert-edit))
  (close-every-alert-view))

(defn handle-close [] (close-every-alert-view) (clear-alert-form))

(defn handle-change
  [e]
  (let [v (-> e
              .-target
              .-value)]
    (update-alert-form :amount (str->amount v))))

(defn handle-submit
  []
  (let [a (-> @db
              :form/alerts)]
    (do (create-alert-record a) (close-every-alert-view) (clear-alert-form))))

(defn handle-update
  []
  (when-let [a (-> @db
                   :form/alerts)]
    (do (update-alert-record a) (clear-alert-form) (close-every-alert-view))))

;; ========================================
;; COMPONENTS

(defn select-pair
  []
  (let [m @(r/cursor db [:markets])
        v @(r/cursor db [:form/alerts :pair])
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
  (let [m @(r/cursor db [:markets])
        v @(r/cursor db [:form/alerts :market])
        opts (get-market-names m)
        on-change #(update-alert-form
                    :market
                    (if % (aget % "value") (update-alert-form :market "")))]
    [:>
     js/window.Select
     {:value v
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))
      :onChange on-change}]))

(defn alert-items
  []
  (fn []
    (let [alerts @(r/track #(-> @db
                                :user
                                :alerts
                                vals))]
      [:div.alerts_items_wrapper
       (for [a alerts
             :let [{:keys [id amount]} a]]
         ^{:key id}
         [:div.row_wrap
          {:on-click #(toggle-edit-alert-view id)}
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
  (let [alerts @(r/cursor db [:user :alerts])]
    (if-not (pos? (count alerts)) [ui/empty-list "alerts"] [alert-items])))

(defn edit-item
  []
  [:div.form_wrap
   [:h1 "Edit alert"]
   [ui/close "left_top" #(handle-close)]
   [:div.row
    [ui/input-wrap "Market" [select-market {:key "market"}]]
    [ui/input-wrap "Currency pair" [select-pair {:key "pair"}]]]
   [ui/text-input
    {:on-change handle-change
     :label "amount"
     :value @(r/cursor db [:form/alerts :amount])}]
   [:div.row
    [:div
     [ui/button
       {:on-click handle-delete
        :color "red"}
       "Delete"]]
    [:div
     [ui/button
       {:on-click handle-update
        :color "#000"}
       "Save"]]]])

(defn add-item
  []
  [:div.form_wrap
   [:h1 "Add alert"]
   [ui/close "left_top" #(handle-close)]
   [:div.row
    [ui/input-wrap "Market" [select-market {:key "market"}]]
    [ui/input-wrap "Currency pair" [select-pair {:key "pair"}]]]
   [ui/text-input
    {:on-change handle-change
     :label "amount"
     :value @(r/cursor db [:form/alerts :amount])}]
   [:div.input_wrapper
    [ui/button
     {:on-click handle-submit
      :color "#000"}
     "Add"]]])

(def animated-view-edit
  (r/reactify-component (fn [{c :children}]
                          (let [y (gobj/get c "y")]
                            [:div.detailed_view
                             {;:ref #(swap! db update-in
                                     ; [:ui/alert-edit-height]
                                     ; (fn [] (if % (.-offsetHeight %) 0))
                              :style {:transform (str "translateY(" y "px)")}}
                             [edit-item]]))))

(defn detailed-view-edit
  []
  (fn []
    (let [open? @(r/cursor db [:ui/alert-edit])]
      [:div.motion_wrapper
       [Motion
        {:style {:y (spring (if open? (- (:ui/alert-edit-height @db)) 0))}}
        (fn [y] (r/create-element animated-view-edit #js {} y))]])))

(def animated-view-add
  (r/reactify-component (fn [{c :children}]
                          (let [y (gobj/get c "y")]
                            [:div.detailed_view
                             {;:ref #(swap! db update-in
                                     ; [:ui/alert-add-height]
                                     ; (fn [] (if % (.-offsetHeight %) 0))
                              :style {:transform (str "translateY(" y "px)")}}
                             [add-item]]))))

(defn detailed-view-add
  []
  (fn []
    (let [open? @(r/cursor db [:ui/alert-add])
          height @(r/cursor db [:ui/alert-add-height])]
      [:div.motion_wrapper
       [Motion
        {:style {:y (spring (if open? (- height) 0))}}
        (fn [y] (r/create-element animated-view-add #js {} y))]])))

(defn alerts-toolbar
  [s]
  (let [open? (not (or (:ui/alerts-edit @db) (:ui/alerts-add @db)))]
    (when open?
      [:div.portfolio_toolbar
       [:div
        {:style {:padding "0 10px"
                 :width "100%"}}
        [ui/button
         {:on-click #(open-add-alert-view)
          :color "#000"}
         "Add"]]])))

(defn alerts
  []
  [:div.alerts_container
   [header]
   [alerts-list]
   [alerts-toolbar]
   [detailed-view-edit]
   [detailed-view-add]])
