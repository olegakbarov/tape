(ns app.screens.detailed
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [app.actions.ui :refer [add-to-favs
                                    remove-from-favs
                                    close-detailed-view]]))
(comment
  {:high 3143.5286,
   :sell 3119.8,
   :buy 3081.6715,
   :vol-cur 98.522881,
   :low 3048.4535,
   :avg 3095.991,
   :market "yobit",
   :timestamp 1509279292,
   :currency-pair "LTC-RUB",
   :last 3070,
   :vol 304628.34})

(defn fav? [favs tupl]
  (reduce
   (fn [acc pair]
    (if (and (= (first pair) (first tupl))
             (= (last pair) (last tupl)))
      true
      acc))
   false
   favs))

(defn DetailsContent []
  (let [[market pair] (:ui/detailed-view @db)
        favs (:favorites @db)
        content (get-in @db [:markets market pair])
        {:keys [high low
                sell buy
                currency-pair market
                timestamp
                avg last
                vol
                vol-cur]} content]
    [:div
     [:h1 pair]
     [:h3 market]
     [:h4 "High: " high]
     [:h4 "Low: " low]
     [:h4 "Buy: " buy]
     [:h4 "Sell: " sell]
     [:h4 "Volume: " vol]
     [:h4 "Average (curr): " vol-cur]
     (if (fav? favs [(keyword market) pair])
       [:button
        {:on-click #(remove-from-favs [(keyword market) (keyword pair)])}
        "Remove from favorites"]
       [:button
        {:on-click #(add-to-favs [(keyword market) pair])}
        "Add to favorites"])
     [:button
      {:on-click #(close-detailed-view)}
      "Close"]]))
