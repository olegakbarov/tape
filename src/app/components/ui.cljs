(ns app.components.ui
  (:require [reagent.core :as r]
            [cljss.core :refer [defstyles]]
            [cljss.reagent :as rss :include-macros true]))

(def blue "#657AF3")
(def green "#12D823")

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
