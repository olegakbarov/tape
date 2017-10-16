(ns app.actions.db
  (:require [clojure.walk]
            [app.db :refer [db router]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn process-ws-event [t]
 (clojure.walk/keywordize-keys
  (into {}
   (for [[k v] t]
        [(->kebab-case k) v]))))

(defn update-ticker! [ticker]
  (let [t (process-ws-event ticker)
        {:keys [market currency-pair]} t]
    (swap! db assoc-in [:markets market currency-pair] t)))



