(ns app.actions
  (:require [clojure.walk]
            [app.db :refer [db]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

;; navigate to another screen
(defn to-screen [screen]
  (swap! db assoc-in [:ui :screen] screen))

;; event from websocket keywordized & kebabcased
(defn process-ws-event [t]
  (clojure.walk/keywordize-keys
    (into {}
      (for [[k v] t]
        [(->kebab-case k) v]))))

;; adds ticket to db
(defn update-db-with-ticker [ticker]
  (let [t (process-ws-event ticker)
        {:keys [market currency-pair]} t]
    (js/console.log t)
    (swap! db assoc-in [:markets market currency-pair] t)))

;; periodically saves the markets state
(defn cache-state [])


