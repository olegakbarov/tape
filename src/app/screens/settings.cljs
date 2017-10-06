(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.actions :as actions]
            [app.db :refer [db]]
            [app.components.ui :refer [Button
                                       Wrapper]]))

(defn settings []
  (let [screen (get-in @db [:ui/screen])]
    [:div
      [:div#header.settings
        [:img.back_arr
         {:src "icons/arrow-left.svg"
          :on-click #(actions/to-screen :bestprice)}]]
      [Wrapper
       [:div {:style {:padding "0 10px"}}
         [Button
          {:type "submit"
           :on-click #(js/console.log "saved!")
           :color "#12D823"}
          "Save"]]]]))

