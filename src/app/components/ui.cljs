(ns app.components.ui
  (:require [reagent.core :as r]
            [cljss.core :refer [defstyles]]
            [cljss.reagent :as rss :include-macros true]
            [app.components.colors :as c]))

(rss/defstyled Button :button
  {:padding "10px"
   :width "100%"
   :background-color :color
   :color "#fff"
   :type :type
   :ref :ref
   :on-click :on-click
   :border "none"
   :border-radius "4px"
   :text-transform "uppercase"
   :font-size "13px"})

(rss/defstyled Container :div
  {:height "100%"})

(rss/defstyled Wrapper :div
  {:margin-top "50px"
   :background-color "white"
   :height "100%"})

(rss/defstyled IconImg :img
  {:width "20px"
   :height "20px"
   :&:hover {:cursor "pointer"}
   :&:active {:opacity ".5"}
   :-webkit-user-select "none"})

(defn Icon [on-click src]
  [:div
   {:on-click on-click}
   [IconImg {:src src}]])

(rss/defstyled GroupWrap :div
  {:display "flex"
   :justify-content "space-around"
   :width "60%";
   :-webkit-user-select "none"})

(rss/defstyled GroupBtn :div
  {:padding "6px 10px"
   :text-align "center"
   :border "1px solid white"
   :border-right (with-meta #(if % "none" "1px solid white") :first?)
   :border-radius (with-meta #(if % "0 4px 4px 0" "4px 0 0 4px") :last?)
   :background-color (with-meta #(if % c/white "transparent") :active?)
   :color (with-meta #(if % c/blue c/white) :active?)
   :&:hover {:cursor "pointer"}})

