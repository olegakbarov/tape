(ns app.components.dropdown
  (:require [reagent.core :as reagent]
            [app.db :refer [db]]))

(defn dropdown
  "Abstract selectable list. Accepts coll of strings and select fn
  which is called on click event"
  [props]
  (fn []
    (let [{:keys [items value handler]} props]
      (doall
        [:div.dropdown
         (for [item items]
           (let [on-click #(reset! value item)]
             ^{:key item} [:div.pill {:on-click #(handler item)} item]))]))))
