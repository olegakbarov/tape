(ns app.components.ui
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.db :refer [db]]))

(defn spinner
  []
  [:div.orbit-spinner
   (for [i [1 2 3]] ^{:key i} [:div.orbit])])

(defn button
  [params text]
  (let [{:keys [color]} params]
    [:button.button
     (merge params (when color {:style {:background-color color}}))
     text]))

(defn checkbox
  [legend value on-change]
  ;; generage custom "for"
  [:div.checkbox_wrapper
   [:div.checkbox_legend legend]
   [:input#tray.checkbox
    {:type "checkbox"
     :on-change on-change}]
   [:label {:for "tray"}]])

(defn empty-list
  [items]
  [:div.form_empty_list
   [:div (str "You haven't added any " items " yet")]])

(defn input-wrap
  "Wraps the input and provides label"
  [label & children]
  [:div.input_wrapper [:div.input_label label] children])

(defn text-input
  "Generic text/number input"
  []
  ;; TODO: spec it
  (fn [cfg]
    (let [{:keys [on-change value label]} cfg]
      [:div.input_wrapper
       (when label [:div.input_label label])
       [:input.input_item
        {:type "text"
         :autoFocus false
         :onChange on-change
         :value value}]])))

(defn close
  [klass on-click]
  [:div.common_close
   {:class klass
    :on-click on-click}])

(defn burger-menu
  [x on-click]
  [:div.burger-menu
   {:class x
    :on-click on-click}])
