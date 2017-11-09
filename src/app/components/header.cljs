(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [router]]
            [app.actions.ui :refer [to-screen]]
            [cljss.reagent :as rss :include-macros true]
            [app.components.colors :as colors]
            [app.components.ui :refer [Icon
                                       GroupWrap
                                       GroupBtn]]))

(rss/defstyled Hdr :div
 {:height "50px"
  :position "fixed"
  :top "7px"
  :width "100%"
  :background-color "white"
  :color colors/white
  :display "flex"
  :justify-content "space-around"
  :align-items "center"
  :font-size "12px"
  :border-bottom "1px solid #eaeaea"
  :border-radius "6px 6px 0 0"})

(defn Header [items]
  (fn []
   (let [screen (get-in @router [:screen])]
     [Hdr
      [:div#arrow]
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

