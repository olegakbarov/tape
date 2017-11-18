(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [router]]
            [app.actions.ui :refer [to-screen]]))

(defn Header [items]
 (fn []
  (let [screen (get-in @router [:screen])]
   [:div#header
    [:ul.group_wrap
     (let [active? #(= screen (-> % .toLowerCase keyword))]
      (doall
       (map-indexed
        (fn [idx text]
         ^{:key text}
         [:li.group_btn
           {:class (if (active? text) "active" "")
            :on-click #(to-screen (-> text
                                          .toLowerCase
                                          keyword))}
           text])
        items)))]])))

