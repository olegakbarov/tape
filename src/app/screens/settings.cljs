(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.actions :as actions]
            [app.db :refer [db]]))

(defn settings []
  (let [screen (get-in @db [:ui/screen])]
    [:div#header.settings
      [:img.back_arr
       {:src "icons/arrow-left.svg"
        :on-click #(actions/to-screen :bestprice)}]]))
