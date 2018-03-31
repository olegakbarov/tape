(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [db router]]
            [app.actions.ui :refer [to-screen]]
            [app.components.ntf :refer [ntf]]
            [goog.object :as gobj]))

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

(defn header
  []
  (fn []
    (let [screen (get-in @router [:screen])]
      [:div#header
       [:div.title "Probe beta_0"]
       [nav]
       [ntf]])))
