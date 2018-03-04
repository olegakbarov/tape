(ns app.components.ntf
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.motion :refer [Motion spring presets]]
            [goog.object :as gobj]))

(def h 30)

(defn ntf-comp
  [n]
  (let [{:keys [color text]} (-> @db
                                 :ui/ntf)]
    (when (and text color)
          [:div {:style {:background-color color}} text])))

(defn view
  [{c :children}]
  (let [height (gobj/get c "height")]
    [:div
     {:style {:position "fixed"
              :width "100%"
              :height (str height "px")
              :line-height (str height "px")
              :color "#fff"
              :text-align "center"
              :font-size "12px"}}
     [ntf-comp]]))

(def animated-comp (reagent/reactify-component view))

(defn ntf
  []
  (fn []
    [:div
     {:style {:position "absolute"
              :bottom "-30px"
              :height "30px"
              :width "100%"
              :z-index 100}}
     [Motion
      {:style {:height (spring (if (:ui/ntf @db) h 0))}}
      (fn [x] (reagent/create-element animated-comp #js {} x))]]))
