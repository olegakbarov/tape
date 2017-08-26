(ns app.actions
  (:require [app.db :refer [db]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn to-screen [screen]
  (swap! db assoc-in [:ui :screen] screen))

(defn process-ws-event [t]
  (into {}
    (for [[k v] t]
      [(->kebab-case k) v])))

(defn update-db-with-ticker [ticker]
  (let [t (process-ws-event ticker)
        {:keys [market currency-pair]} t]
    (js/console.log (clj->js (@db :markets)))
    (swap! db assoc-in [:markets market currency-pair] t)))

