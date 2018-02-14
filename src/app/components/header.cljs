(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.actions.ui :refer [to-screen]]
            [app.motion :refer [Motion spring presets]]
            [goog.object :as gobj]))

(def height 30)

(defn ntf
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
     [ntf]]))

(def animated-comp (reagent/reactify-component view))

(defn ntf-view
  []
  (fn []
    [:div
     {:style {:position "absolute"
              :bottom 0
              :height (str height "px")
              :width "100%"
              :z-index 100
              :display (if (:ui/ntf @db) "block" "none")}}
     [Motion
      {:style {:y (spring (if (:ui/ntf @db) height 0))}}
      (fn [x] (reagent/create-element animated-comp #js {} x))]]))

(defn nav
  []
  (let [toggle-items ["Live" "Portfolio" "Alerts" "Settings"]
        screen (get-in @router [:screen])]
    [:ul.group_wrap
     (let [active? #(= screen
                       (-> %
                           .toLowerCase
                           keyword))]
       (doall (map-indexed (fn [idx text]
                             ^{:key text}
                             [:li.group_btn
                              {:class (if (active? text) "active" "")
                               :on-click #(to-screen (-> text
                                                         .toLowerCase
                                                         keyword))}
                              text])
                           toggle-items)))]))

(defn Header
  []
  (fn []
    (let [screen (get-in @router [:screen])]
      [:div#header
       [:div
        {:style {:background-color "white"
                 :width "100%"
                 :height "100%"
                 :position "absolute"
                 :z-index "101"}}]
       [:div {:style {:z-index 101}} [:div.title "1.0.0-beta.1"] [nav]]
       [ntf-view]])))
