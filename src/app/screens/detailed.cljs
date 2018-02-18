(ns app.screens.detailed
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [app.components.chart :refer [Chart]]
            [app.actions.ui :refer
             [add-to-favs
              remove-from-favs
              close-detailed-view]]
            [app.logic.ui :refer [get-chart-points]]))

(comment {:high 3143.5286
          :sell 3119.8
          :buy 3081.6715
          :vol-cur 98.522881
          :low 3048.4535
          :avg 3095.991
          :market "yobit"
          :timestamp 1509279292
          :currency-pair "LTC-RUB"
          :last 3070
          :vol 304628.34})

(defn fav?
  [favs tupl]
  (reduce (fn [acc pair]
            (if (and (= (first pair) (first tupl)) (= (last pair) (last tupl)))
              true
              acc))
          false
          favs))

(defn pair-detailed
  []
  (let [[market pair] (:ui/detailed-view @db)
        favs (-> @db
                 :user
                 :favorites)
        content (get-in @db [:markets market pair])
        {:keys [high
                low
                sell
                buy
                currency-pair
                market
                timestamp
                avg
                last
                vol
                vol-cur]}
        content
        is-fav? (fav? favs [market pair])
        points @(r/track get-chart-points market pair)]
    (when (:ui/detailed-view @db)
      [:div#detailed
       [:div.header
        [:div.title
         pair
         [:div.fav
          {:class (if is-fav? "faved" "")
           :on-click (if is-fav?
                       #(remove-from-favs [(keyword market) (keyword pair)])
                       #(add-to-favs [(keyword market) (keyword pair)]))}
          (if is-fav? "saved" "save")]]
        [:div.close {:on-click #(close-detailed-view)}]]
       [:div.market " " market]
       [:div.labels
        (for [i ["High" "Low" "Buy" "Sell"]] ^{:key i} [:div.item i])]
       [:div.prices.last
        (for [i [high low buy sell]]
          ^{:key (* 1000 (.random js/Math i))} ;; nothing to be proud about here
          [:div.item (js/parseFloat i)])]
       (when points [Chart points])])))
