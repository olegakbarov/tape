(ns app.screens.bestprice
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs]]
            [app.utils.core :refer [curr-symbol->name]]
            [app.actions :as actions]
            [clojure.string :refer [split]]
            [app.components.header :refer [Header]]
            [app.components.ui :refer [Wrapper
                                       Container
                                       Icon]]))

(defn row-unfolded? []
  (some
    #(not (nil? %))
    (:ui/expanded-row @db)))

(defn expanded-row [m]
  (let [[key value] m
        {:keys [market currency-pair avg low high last timestamp]} value
        [left right] (split currency-pair "-")]
    ^{:key currency-pair}
    [:div.unfolded_item_wrapper
     {:on-click #(actions/expand-pair-row nil nil)}
     [:div.unfolded_pair_row
       [:div.toprow_box
        [:h2.symbol left]
        [:h4.name (curr-symbol->name left)]]
       [:div.toprow_box.imgbox
         [:img.currpic {:src (str "images/" left ".png")
                        :style {:height "35px"}}]]
       [:div.toprow_box.imgbox
         [:img.currpic {:src (str "images/" right ".png")
                        :style {:height "35px"}}]]
       [:div.toprow_box
        [:h2.symbol right]
        [:h4.name (curr-symbol->name right)]]]
     [:div.unfolded_pair_row.last
       [:div.bottomrow_box
        [:div.subtitle "Last:"]
        [:div.value last]]
       [:div.bottomrow_box
        [:div.subtitle "High:"]
        [:div.value high]]
       [:div.bottomrow_box
        [:div.subtitle "Low:"]
        [:div.value low]]
       [:div.bottomrow_box
        [:div.subtitle "Market:"]
        [:div.value (.toUpperCase market)]]]]))

(defn folded-row [m]
 (let [[key value] m
       {:keys [market currency-pair last]} value]
    (if (some nil? [market currency-pair last])
      nil ;; dont render if got falsy
      ^{:key (str currency-pair "x" market)}
      [:div.titem_wrapper
        {:style {:opacity (if (row-unfolded?) .3 1)}
         :on-click #(actions/expand-pair-row key market)}
        ^{:key "curr-pair"}
        [:div.titem_cell
          [:div.pair currency-pair
            [:span.dynamics.green "  ▲ 0.00 %"]]
          [:div.market market]]
        ^{:key "last-price"}
        [:div.titem_cell.price last]])))

(defn render-row [m t-pair t-market]
 (fn [m t-pair t-market]
   (let [[key value] m
         {:keys [market currency-pair avg low high timestamp]} value
         [left right] (split currency-pair "-")]
    (if (and (= t-pair key) (= t-market market))
        [expanded-row m]
        [folded-row m]))))

(defn render-pairs []
  (fn [t-pair t-market]
   (let [markets (:markets @db)
         pairs (best-pairs markets)
         [t-pair t-market] (:ui/expanded-row @db)]
    [:div
     (for [pair pairs]
      ^{:key (str pair)}
      [render-row pair t-pair t-market])])))

(defn bestprice []
  (let [toggle-items ["Bestprice" "Markets"]]
   [Container
    [Header
      [Icon
       #(actions/to-screen :portfolio)
       "icons/user.svg"]
      [Icon
        #(actions/to-screen :settings)
        "icons/settings.svg"]
      toggle-items]
    [Wrapper
     [render-pairs]]]))

