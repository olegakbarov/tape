(ns app.components.ui
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.db :refer [db]]))

(defn spinner []
  [:div.orbit-spinner
   (for [i [1 2 3]] ^{:key i} [:div.orbit])])

(defn button
  [params text]
  (let [{:keys [on-click type ref disabled color]} params]
    [:button.button
     (merge (when color {:style {:background-color color}})
            {:on-click on-click
             :ref ref
             :type type})
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
  [:div.form_empty_list (str "You haven't added any " items " yet")])

(defn input-wrap
  "Wraps the input and provides label"
  [label & children]
  [:div.input_wrapper [:div.input_label label] children])

(defn text-input
  "Generic text/number input"
  [cfg]
  ;; TODO: spec it
  (fn []
    (let [{:keys [on-change value label]} cfg]
      [:div.input_wrapper
       (when label [:div.input_label label])
       [:input.input_item
        {:type "text"
         :autoFocus false
         :onChange on-change
         :value (value)}]])))

(defn close [style on-click]
  [:div.common_close {:style style
                      :on-click on-click}])

