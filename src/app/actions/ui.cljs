(ns app.actions.ui
 (:require [app.db :refer [db router]]
           [app.actions.storage :refer [persist-user-currents!]]))

(defn to-screen [screen]
 (swap! router assoc-in [:screen] screen))

(defn add-to-favs [tupl]
 (do
  (swap! db update-in [:user :favorites] conj tupl)
  (persist-user-currents!)))

(defn remove-from-favs [id]
 (do
  (swap! db update-in [:user :favorites] dissoc id)
  (persist-user-currents!)))

(defn open-detailed-view [market pair]
 (swap! db assoc-in [:ui/detailed-view] [market pair]))

(defn close-detailed-view []
 (swap! db assoc-in [:ui/detailed-view] nil))

(defn toggle-filter
 "k - keyword of applied filter"
 [k]
 (do
  (swap! db assoc :ui/detailed-view nil)
  (swap! db update-in [:ui/current-filter]
    #(if (= k (:ui/current-filter @db))
         nil
         k))))

(defn update-filter-q [q]
 (do
  (swap! db assoc-in [:ui/filter-q] q)
  (swap! db assoc-in [:ui/current-filter] (when-not (= q "") :query))))
