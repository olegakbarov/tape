(ns app.screens.bestprice
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.components.header :refer [header]]
            [cljsjs.moment]
            [app.logic :as logic]
            [app.actions :as actions]
            [clojure.string :refer [split]]))

(defn t-head []
  [:div.thead_wrapper
    (for [i ["Pair" "Price" "Change" "Market"]]
     ^{:key i}
     [:div.thead_item
       [:span.thead_clickable
         (str "▼ " i)]])])

(defn expanded-row [m]
  (let [[key value] m
        {:keys [market currency-pair avg low high timestamp]} value
        [left right] (split currency-pair "-")]
    (js/console.log m)
    ^{:key currency-pair}
    [:div.unfolded_item_wrapper
     {:on-click #(actions/expand-pair-row nil nil)}
     [:img.currpic {:src (str "images/" left ".png")
                    :style {:height "25px"}}]
     [:img.currpic {:src (str "images/" right ".png")
                    :style {:height "25px"}}]
     [:h1 currency-pair]]))

(defn folded-row [m]
 (let [[key value] m
       {:keys [market currency-pair last]} value]
    ^{:key currency-pair}
    [:div.titem_wrapper
      {:on-click #(actions/expand-pair-row key market)}
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

