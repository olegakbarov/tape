(ns app.actions
  (:require [app.db :refer [db]]))

(defn to-screen [screen]
  (swap! db assoc-in [:ui :screen] screen))

