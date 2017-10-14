(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.actions :as actions]
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
  :background-color colors/blue
  :color colors/white
  :display "flex"
  :justify-content "space-around"
  :align-items "center"
  :font-size "12px"
  :border-radius "6px 6px 0 0"})

(defn Header [Left Right items]
  (fn []
   (let [screen (get-in @db [:ui/screen])]
     [Hdr
      [:div#arrow]
      Left
      [GroupWrap
        (let [active? #(= screen (-> % .toLowerCase keyword))]
          (doall
           (map-indexed
            (fn [idx text]
             ^{:key text}
             [:div {:on-click #(actions/to-screen (-> text
                                                      .toLowerCase
                                                      keyword))
                    :style {:width "50%"}}
              [GroupBtn
               {:first? (= idx 0)
                :last? (= (inc idx) (count items))
                :active? (active? text)}
               text]])
            items)))]
      Right])))

