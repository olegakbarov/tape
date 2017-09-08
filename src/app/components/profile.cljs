(ns app.components.profile
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.actions :as actions]))

(defn header []
  (let [screen (get-in @db [:ui/screen])]
    [:div#header.profile
      [:img.back_arr
       {:src "icons/arrow-left.svg"
        :on-click #(actions/to-screen :bestprice)}]
      [:div#toggle
        [:div.toggle_btn.bestprice_btn
         {:on-click #(actions/to-screen :portfolio)
          :class (if (= screen :portfolio)
                     :active)}
         "Portfolio"]
        [:div.toggle_btn.market_btn
         {:on-click #(actions/to-screen :alerts)
          :class (if (= screen :alerts)
                     :active)}
         "Alerts"]]
      [:div#settings
        {:on-click #(actions/to-screen :settings)}
        [:img.currpic {:src (str "icons/settings.svg")}]]]))
