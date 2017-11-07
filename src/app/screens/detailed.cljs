(ns app.screens.detailed
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [app.components.chart :refer [Chart]]
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
        favs (:user/favorites @db)
        content (get-in @db [:markets market pair])
        {:keys [high low
                sell buy
                currency-pair market
                timestamp
                avg last
                vol
                vol-cur]} content
        is-fav? (fav? favs [(keyword market) pair])]

    [:div#detailed
     [:div.header
      [:div.title pair
       [:div.star
          {:class (if is-fav? "faved" "")
           :on-click
            (if is-fav?
              #(remove-from-favs [(keyword market) (keyword pair)])
              #(add-to-favs [(keyword market) pair]))}]]
      [:div.close
       {:on-click #(close-detailed-view)}]]
     [:div.market " " market]
     [:div.prices
      [:div.item "High"
       [:div high]]
      [:div.item "Low"
       [:div low]]
      [:div.item "Buy"
       [:div buy]]
      [:div.item "Sell"
       [:div sell]]]
     [Chart]]))
