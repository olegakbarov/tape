(ns app.actions.api
  (:require [clojure.walk]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [app.db :refer [db]]))

(defn process-ws-event [t]
 (clojure.walk/keywordize-keys
  (into {}
   (for [[k v] t]
        [(->kebab-case k) v]))))

(defn ticker->db! [ticker]
  (let [t (process-ws-event ticker)
        {:keys [market currency-pair]} t]
    (swap! db assoc-in [:markets (keyword market) (keyword currency-pair)] t)))

