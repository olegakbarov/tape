(ns app.screens.settings
  (:require [reagent.core :as reagent]
            [app.actions.settings :refer [open-in-browser]]
            [app.components.header :refer [header]]))

(defn settings
  []
  [:div
   [header]
   [:div#settings
    [:div.group_title "Basic"]
    [:div.cell_group
     [:div.cell [:div.left "Theme"]
      [:div.right "LIGHT"]]
     [:div.cell]]
    [:div.group_title "Community"]
    [:div.cell_group
     [:div.cell {:on-click #(open-in-browser "https://t.me/joinchat/B0HztQ-ieVI8Zb3VcC1KwQ")}
      [:div.left.link "Telegram"]
      [:div.right ">"]]
     ;; TODO
     [:div.cell {:on-click #(open-in-browser "https://t.me/libmustdie")}
      [:div.left.link "Twitter"]
      [:div.right ">"]]
     ;; TODO
     [:div.cell {:on-click #(open-in-browser "https://t.me/libmustdie")}
      [:div.left.link "Reddit"]
      [:div.right ">"]]
     [:div.cell]]
    [:div.group_title "Source code"]
    [:div.cell_group
     [:div.cell {:on-click #(open-in-browser "https://github.com/cryptounicorns/tape")}
      [:div.left.link "GitHub"]
      [:div.right ">"]]]]])
