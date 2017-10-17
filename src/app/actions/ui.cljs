(ns app.actions.ui
 (:require [app.db :refer [db router]]))

(defn to-screen [screen]
  (swap! router assoc-in [:screen] screen))

(defn expand-pair-row [pair market]
  (let [[p m] (:ui/expanded-row @db)]
    (swap! db assoc-in [:ui/expanded-row]
                       (if (and (= p pair) (= m market))
                           []
                           [pair market]))))
