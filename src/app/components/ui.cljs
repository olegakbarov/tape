(ns app.components.ui
  (:require [reagent.core :as r]))

(defn Button
  [params text]
  (let [{:keys [on-click type ref color]} params]
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
  [value on-change]
  ;; generage custom "for"
  [:div
   [:input#tray.checkbox {:type "checkbox" :on-change on-change}]
   [:label {:for "tray"}]])

(defn EmptyList [items]
  [:div.form_empty_list
    (str "You haven't added any " items " yet")])

