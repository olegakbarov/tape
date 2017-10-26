(ns app.screens.live
  (:require-macros [app.macros :refer [profile]])
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs
                                    all-pairs]]
            [app.utils.core :refer [curr-symbol->name]]
            [clojure.string :refer [split]]
            [app.motion :refer [Motion
                                spring
                                presets]]
            [app.components.colors :refer [green]]
            [cljss.core :refer [defstyles]]
            [cljss.reagent :as rss :include-macros true]
            [goog.object :as gobj]
            [app.components.ui :refer [Wrapper]]))

(def open (r/atom false))

(def applied-filter (r/atom nil))

(defn toggle-filter
  "k - keyword of filter applied"
  [k]
  (reset! applied-filter (if (= k @applied-filter)
                             nil
                             k)))

(defn Row [pair]
 (let [{:keys [market currency-pair last]} pair]
  [:div.row_wrap
   ^{:key "currency-pair"}
   [:div.left_cell
    [:div.title currency-pair]
    [:div.market market]]
   ^{:key "last-price"}
   [:div.right_cell
    last
    [:div.swing "+ 1.04 (0.002 %)"]]]))

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
     (for [pair (remove empty? pairs)]
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

(defn live-board []
  [Wrapper
   [filter-box]
   [render-rows]
   [one-pair-view]])
