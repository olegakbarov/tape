(ns app.screens.markets
  (:require [reagent.core :as reagent :refer [atom]]
            [app.db :refer [db]]
            [app.actions :as actions]
            [app.logic :as logic]
            [app.utils.core :refer [get-markets]]
            [app.constants.currs :refer [pairs]]
            [app.components.header :refer [Header]]
            [app.components.ui :refer [Wrapper
                                       Container
                                       Icon]]))

(defn render-markets []
  (let [markets (get-markets)]
   [:div
     (for [m markets]
        (let [{:keys [name pairs-num]} m]
          ^{:key name}
          [:div.market_row
            [:div.item
             [:div.name name]
             [:div.number "BTC Volume"]
             [:div.number "Number of pairs:"]]
            [:div.item
             [:div.name.right.green "●"]
             [:div.number.right "100000"]
             [:div.number.right pairs-num]]]))]))


(defn markets []
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
     [render-markets]]]))
