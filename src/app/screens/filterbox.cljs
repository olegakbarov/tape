(ns app.screens.filterbox
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [app.actions.ui :refer [toggle-filter
                                    update-filter-q]]))

(defn FilterBox []
  [:div])

(defn FilterBox- []
 (let [q (:ui/filter-q @db)
       f (:ui/current-filter @db)]
  [:div
    [:input {:type "text"
             :placeholder "search.."
             :on-change #(update-filter-q (-> % .-target .-value))}]
    [:div.filter_box
     [:div.filter_item
      {:class (if (= :favorites f) "selected" "")
       :on-click #(toggle-filter :favorites)}
      "favorites"]
     [:div.filter_item
      {:class (if (= :price f) "selected" "")
       :on-click #(toggle-filter :price)}
      "lowest price"]
     [:div.filter_item
      {:class (if (= :volatile f) "selected" "")
       :on-click #(toggle-filter :volatile)}
      "volatile"]]]))

