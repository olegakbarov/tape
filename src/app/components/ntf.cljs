(ns app.components.ntf
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.motion :refer [Motion spring presets]]
            [goog.object :as gobj]))

(def height 30)

(defn ntf-comp
  [n]
  (let [{:keys [color text]} (-> @db
                                 :ui/ntf)]
    (when (and text color) [:div {:style {:background-color color}} text])))

(defn view
  [{c :children}]
  (let [y (gobj/get c "y")]
    [:div
     {:style {:position "fixed"
              :width "100%"
              :height (str height "px")
              :line-height (str height "px")
              :color "#fff"
              :z-index 100
              :text-align "center"
              :font-size "12px"
              :-webkit-transform (str "translateY(" y "px)")
              :transform (str "translateY(" y "px)")}}
     [ntf-comp]]))

(def animated-comp (reagent/reactify-component view))

(defn ntf
  []
  (fn []
    [:div
     {:style {:position "absolute"
              :bottom 0
              :height (str height "px")
              :width "100%"
              :z-index 100}}
     [Motion
      {:style {:y (spring (if (:ui/ntf @db) height 0))}}
      (fn [x] (reagent/create-element animated-comp #js {} x))]]))
