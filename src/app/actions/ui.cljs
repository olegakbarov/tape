(ns app.actions.ui
 (:require [app.db :refer [db router]]))

(defn to-screen [screen]
  (swap! router assoc-in [:screen] screen))

