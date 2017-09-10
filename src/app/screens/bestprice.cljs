(ns app.screens.bestprice
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.components.header :refer [header]]
            [cljsjs.moment]
            [app.logic :as logic]
            [app.utils.core :refer [curr-symbol->name]]
            [app.actions :as actions]
            [clojure.string :refer [split]]))

(defn row-unfolded? []
  (some
    #(not (nil? %))
    (:ui/expanded-row @db)))

(defn t-head []
  [:div.thead_wrapper
    {:style {:opacity (if (row-unfolded?) 0.3 1)}}
    (for [i ["Pair" "Price" "Change" "Market"]]
     ^{:key i}
     [:div.thead_item
       [:span.thead_clickable i]])])

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
    ^{:key currency-pair}
    [:div.titem_wrapper
      {:style {:opacity (if (row-unfolded?) .3 1)}
       :on-click #(actions/expand-pair-row key market)}
      ^{:key "curr-pair"}
      [:div.titem_cell currency-pair]
      ^{:key "last-price"}
      [:div.titem_cell last]
      ^{:key "dynamcis"}
      [:div.titem_cell "todo"]
      ^{:key "market-name"}
      [:div.titem_cell.market market]]))

(defn render-row [m]
   (let [[key value] m
         {:keys [market currency-pair avg low high timestamp]} value
         [left right] (split currency-pair "-")
         [t-pair t-market] (:ui/expanded-row @db)]
    (if (and (= t-pair key) (= t-market market))
        (expanded-row m)
        (folded-row m))))

(defn bestprice []
  (let [markets (:markets @db)]
   [:div
     [header]
     [t-head]
     [:div.bestprice_wrapper
       (doall
         (for [pair (logic/get-best-pairs)]
           (render-row pair)))]]))

