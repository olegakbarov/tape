(ns app.screens.bestprice
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs]]
            [app.utils.core :refer [curr-symbol->name]]
            [app.actions.ui :refer [to-screen
                                    expand-pair-row]]
            [clojure.string :refer [split]]
            [app.components.header :refer [Header]]
            [app.components.colors :refer [green]]
            [app.components.ui :refer [Wrapper
                                       Container
                                       Icon]]
            [cljss.core :refer [defstyles]]
            [cljss.reagent :as rss :include-macros true]))

(rss/defstyled RowWrap :div
  {:background-color "#fff"
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
    [Swing " ▲ 0.00 %"]]]))

(defn render-rows []
 (fn []
  (let [markets (:markets @db)
        pairs (best-pairs markets)]
    [:div
     (for [pair pairs]
      ^{:key (str pair)}
      [Row pair])])))

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
     [render-rows]]]))

