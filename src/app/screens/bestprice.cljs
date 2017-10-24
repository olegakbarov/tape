(ns app.screens.bestprice
  (:require-macros [app.macros :refer [profile]])
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs]]
            [app.utils.core :refer [curr-symbol->name]]
            [app.actions.ui :refer [to-screen]]
            [clojure.string :refer [split]]
            [app.components.header :refer [Header]]
            [app.motion :refer [Motion
                                spring
                                presets]]
            [app.components.colors :refer [green]]
            [app.components.ui :refer [Wrapper
                                       Container
                                       Icon]]
            [cljss.core :refer [defstyles]]
            [cljss.reagent :as rss :include-macros true]
            [goog.object :as gobj]))

(def open (r/atom false))

(rss/defstyled RowWrap :div
  {
   ; :background-color "#fff"
   :display "flex"
   :align-items "top"
   :padding "10px 0"
   :&:hover {:background-color "rgba(151, 151, 151, .05)"
             :cursor "pointer"}})

(rss/defstyled LeftCell :div
 {:width "50%"
  :white-space "nowrap"
  :overflow "hidden"
  :text-overflow "ellipsis"
  :padding-left "8px"})

(rss/defstyled Title :div
 {:line-height "17px"
  :font-size "15px"})

(rss/defstyled Swing :div
 {:line-height "14px"
  :color green
  :font-size "11px"})

(rss/defstyled Market :div
 {:line-height "17px"
  :text-transform "uppercase"})

(rss/defstyled RightCell :div
 {:width "50%"
  :text-align "right"
  :font-size "17px"
  :line-height "17px"
  :vertical-align "top"
  :padding-right "8px"})

(defn Row [pair]
 (let [[pair-str info] pair
       {:keys [market currency-pair last]} info]
  [RowWrap
   ^{:key "currency-pair"}
   [LeftCell
    [Title currency-pair]
    [Market market]]
   ^{:key "last-price"}
   [RightCell
    last
    [Swing "+ 0.00 %"]]]))

(defn render-rows []
 (fn []
  (let [markets (:markets @db)
        ; pairs (profile "best-pairs" (best-pairs markets))
        pairs @(r/track best-pairs markets)]
    [:div
     (for [pair pairs]
      ^{:key (str pair)}
      [:div.row_animation_wrap {:on-click #(reset! open (not @open))}
       [Row pair]])])))

(defn Child
  [{c :children}]
  (let [y (gobj/get c "y")]
     [:div
      {:style
       {:position "absolute"
        :width "321px"
        :height "320px"
        :background-color "#fff"
        :z-index 99
        :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)"
        :-webkit-transform (str "translateY(" y "px)")
        :border-radius "4px 4px 0 0"
        :transform (str "translateY(" y "px)")}}]))

(def Child-comp (r/reactify-component Child))

(defn one-pair-view []
  (fn []
   [:div {:style {:position "absolute" :bottom 0}}
    [Motion {:style {:y (spring (if @open
                                     -320
                                     0))}}
     (fn [x]
      (r/create-element Child-comp #js {} x))]]))

(defn bestprice []
  (let [toggle-items ["Bestprice" "Markets"]]
   [Container
    [Header
      [Icon
       #(to-screen :portfolio)
       "icons/user.svg"]
      [Icon
        #(to-screen :settings)
        "icons/settings.svg"]
      toggle-items]
    [Wrapper
     [render-rows]
     [one-pair-view]]]))



