(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [router]]
            [app.actions.ui :refer [to-screen]]
            [app.components.colors :as colors]
            [app.components.ui :refer [Icon
                                       GroupWrap
                                       GroupBtn]]))

(defn Header [items]
 (fn []
  (let [screen (get-in @router [:screen])]
   [:div#header
    [GroupWrap
     (let [active? #(= screen (-> % .toLowerCase keyword))]
      (doall
       (map-indexed
        (fn [idx text]
         ^{:key text}
         [:div {:on-click #(to-screen (-> text
                                          .toLowerCase
                                          keyword))
                 :style {:width "50%"}}
           [GroupBtn
            {:active? (active? text)}
             text]])
        items)))]])))

