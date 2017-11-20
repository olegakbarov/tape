(ns app.screens.live
  (:require-macros [app.macros :refer [profile]])
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs
                                    all-pairs
                                    user-favs
                                    by-query]]
            [app.utils.core :refer [curr-symbol->name]]
            [clojure.string :refer [split]]
            [app.screens.filterbox :refer [FilterBox]]
            [app.actions.ui :refer [open-detailed-view]]))

(defn Row [pair]
 (let [{:keys [market currency-pair last change]} pair
       {:keys [percent amount]} change]
  (js/console.log @db)
  [:div.row_wrap
   ^{:key "currency-pair"}
   [:div.left_cell
    [:div.title
     currency-pair]
    [:div.market market]]
   ^{:key "last-price"}
   [:div.right_cell
    [:div.row_animation_wrap last]
    [:div.swing (str (:amount change) " (" (:percent change) "%) ")]]]))

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
       ^{:key (str pair market)}
       [:div
        {:on-click #(when (nil? (:ui/detailed-view @db))
                     (open-detailed-view (keyword market) (keyword currency-pair)))}
        [Row pair]]))])))


(defn live-board []
 [:div#wrapper
  ; [FilterBox]
  [render-rows]])
