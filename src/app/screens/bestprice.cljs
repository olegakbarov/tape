(ns app.screens.bestprice
  (:require-macros [app.macros :refer [profile]])
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs
                                    all-pairs]]
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

(def applied-filter (r/atom nil))

(defn toggle-filter
  "k - keyword of filter applied"
  [k]
  (reset! applied-filter (if (= k @applied-filter)
                             nil
                             k)))

(rss/defstyled RowWrap :div
  {:display "flex"
   :align-items "top"
   :padding "10px 0"
   :&:hover {:background-color "rgba(151, 151, 151, .05)"
             :cursor "pointer"}})

(rss/defstyled LeftCell :div
 {:width "50%"
  :white-space "nowrap"
  :overflow "hidden"
  :text-overflow "ellipsis"
  :padding-left "12px"})

(rss/defstyled Title :div
 {:line-height "17px"
  :font-size "15px"})

(rss/defstyled Swing :div
 {:line-height "14px"
  :color green
  :font-size "11px"})

(rss/defstyled Market :div
 {:line-height "17px"
  :text-transform "uppercase"
  :color "#ccc"})

(rss/defstyled RightCell :div
 {:width "50%"
  :text-align "right"
  :font-size "17px"
  :line-height "17px"
  :vertical-align "top"
  :padding-right "12px"})

(defn Row [pair]
 (let [{:keys [market currency-pair last]} pair]
  [RowWrap
   ^{:key "currency-pair"}
   [LeftCell
    [Title currency-pair]
    [Market market]]
   ^{:key "last-price"}
   [RightCell
    last
    [Swing "+ 1.04 (0.002 %)"]]]))

(defn filter-box []
  [:div.filter_box
   [:div.filter_item
    {:class (if (= :favorites @applied-filter) "selected" "")
     :on-click #(toggle-filter :favorites)}
    "favorites"]
   [:div.filter_item
    {:class (if (= :price @applied-filter) "selected" "")
     :on-click #(toggle-filter :price)}
    "lowest price"]
   [:div.filter_item
    {:class (if (= :volatile @applied-filter) "selected" "")
     :on-click #(toggle-filter :volatile)}
    "volatile"]])

(defn render-rows []
 (fn []
  (let [markets (:markets @db)
        pairs (condp = @applied-filter
                :price @(r/track best-pairs markets)
                :favorites nil
                :volatile nil
                nil @(r/track all-pairs markets))]
   (if (every? empty? pairs)
    nil
    [:div
     (for [pair pairs]
      ^{:key (str pair)}
      [:div.row_animation_wrap {:on-click #(reset! open (not @open))}
       [Row pair]])]))))

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
     [filter-box]
     [render-rows]
     [one-pair-view]]]))
