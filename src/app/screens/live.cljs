(ns app.screens.live
  (:require-macros [app.macros :refer [profile]])
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs
                                    all-pairs
                                    user-favs
                                    by-query]]
            [app.motion :refer [Motion
                                spring
                                presets]]
            [app.utils.core :refer [curr-symbol->name]]
            [clojure.string :refer [split]]
            [app.components.colors :refer [green]]
            [cljss.core :refer [defstyles]]
            [goog.object :as gobj]
            [app.components.ui :refer [Wrapper]]
            [app.screens.detailed :refer [DetailsContent]]
            [app.screens.filterbox :refer [FilterBox]]
            [app.actions.ui :refer [open-detailed-view]]))

(defn Row [pair]
 (let [{:keys [market currency-pair last]} pair]
  [:div.row_wrap
   ^{:key "currency-pair"}
   [:div.left_cell
    [:div.title
     currency-pair]
    [:div.market market]]
   ^{:key "last-price"}
   [:div.right_cell
    last
    [:div.swing "+ 1.04 (0.002 %)"]]]))

(defn render-rows []
 (fn []
  (let [markets (:markets @db)
        favs (:user/favorites @db)
        q (:ui/filter-q @db)
        pairs (condp = (:ui/current-filter @db)
                :price @(r/track best-pairs markets)
                :favorites @(r/track user-favs markets favs)
                :volatile nil
                :query @(r/track by-query markets q)
                nil @(r/track all-pairs markets))]
   [:div
    (for [pair (remove empty? pairs)]
     (let [{:keys [market currency-pair]} pair]
       ^{:key (str pair)}
       [:div.row_animation_wrap
        {:on-click #(when (nil? (:ui/detailed-view @db))
                      (open-detailed-view (keyword market) (keyword currency-pair)))}
        [Row pair]]))])))

(defn Child
  [{c :children}]
  (let [y (gobj/get c "y")]
     [:div
      {:style
       {:position "absolute"
        :width "321px"
        :height "380px"
        :background-color "#fff"
        :z-index 99
        :border-radius "4px 4px 0 0"
        :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)"
        :-webkit-transform (str "translateY(" y "px)")
        :transform (str "translateY(" y "px)")}}
      [DetailsContent]]))

(def Child-comp (r/reactify-component Child))

(defn DetailedView []
  (fn []
   [:div {:style {:position "absolute" :bottom 0}}
    [Motion {:style {:y (spring (if (:ui/detailed-view @db)
                                    -380
                                    0))}}
     (fn [x]
      (r/create-element Child-comp #js {} x))]]))

(defn live-board []
  [Wrapper
   [FilterBox]
   [render-rows]
   [DetailedView]])
