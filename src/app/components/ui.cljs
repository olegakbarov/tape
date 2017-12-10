(ns app.components.ui
  (:require [reagent.core :as r]
            [clojure.string :as s]))

(defn Button
  [params text]
  (let [{:keys [on-click type ref disabled color]} params]
    [:button.button
     (merge (when color {:style {:background-color color}})
            {:on-click on-click :ref ref :type type})
     text]))

(defn Icon
  [on-click src]
  [:img
   {:src src
    :on-click on-click
    :style {:width "20px"
            :height "20px"
            ; :&:hover {:cursor "pointer"}
            ; :&:active {:opacity ".5"}
            :-webkit-user-select "none"}}])

(defn Checkbox
  [legend value on-change]
  ;; generage custom "for"
  [:div.checkbox_wrapper
   [:div.checkbox_legend legend]
   [:input#tray.checkbox {:type "checkbox" :on-change on-change}]
   [:label {:for "tray"}]])

(defn EmptyListCompo
  [items]
  [:div.form_empty_list (str "You haven't added any " items " yet")])

(defn InputWrapper
  "Wraps the input and provides label"
  [label & children]
  [:div.input_wrapper
   [:div.input_label label] children])
