(ns app.utils.core
  (:require [app.db :refer [db]]
            [compact-uuids.core :as uuid]))

(defn get-markets
  []
  (reduce (fn [acc [key val]]
            (conj acc
                  {:name key
                   :pairs-num (count (keys val))}))
          []
          (:markets @db)))

(defn generate-uuid
  "Creates more compact and less ambigous uuid"
  []
  (uuid/str (.toString (random-uuid))))
