(ns app.components.header
  (:require [reagent.core :as reagent]
            [app.db :refer [router]]
            [app.actions.ui :refer [to-screen]]))

(defn Header
  [items]
  (fn []
    (let [screen (get-in @router [:screen])]
      [:div#header [:div.title "1.0.0-beta.1"]
       [:ul.group_wrap
        (let [active? #(= screen
                          (-> %
                              .toLowerCase
                              keyword))]
          (doall (map-indexed (fn [idx text]
                                ^{:key text}
                                [:li.group_btn
                                 {:class (if (active? text) "active" ""),
                                  :on-click #(to-screen (-> text
                                                            .toLowerCase
                                                            keyword))} text])
                              items)))]])))
