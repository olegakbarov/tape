(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.actions :as actions]
            [app.db :refer [db]]
            [app.components.header :refer [Header]]
            [app.components.ui :refer [Button
                                       Wrapper
                                       Container
                                       Icon]]))

(defn settings []
  [Container
   [Header
    [Icon
     #(actions/to-screen :bestprice)
     "icons/arrow-left.svg"]]
   [Wrapper
    [:div {:style {:padding "0 10px"}}
      [Button
       {:type "submit"
        :on-click #(js/console.log "saved!")
        :color "#12D823"}
       "Save"]]]])

