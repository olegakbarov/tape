(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]
            [app.actions :as actions]))

(defn header []
  (let [screen (get-in @db [:ui/screen])]
    [:div#header
      [:div#arrow]
      [:div#profile
         {:on-click #(actions/to-screen :portfolio)}
         [:img.currpic {:src (str "icons/user.svg")}]]
      [:div#toggle
        [:div.toggle_btn.bestprice_btn
         {:on-click #(actions/to-screen :bestprice)
          :class (if (= screen :bestprice)
                     :active)}
         "Bestprice"]
        [:div.toggle_btn.market_btn
         {:on-click #(actions/to-screen :markets)
          :class (if (= screen :markets)
                     :active)}
         "Markets"]]
      [:div#settings
        {:on-click #(actions/to-screen :settings)}
        [:img.currpic {:src (str "icons/settings.svg")}]]]))
