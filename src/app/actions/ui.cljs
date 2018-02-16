(ns app.actions.ui
  (:require [app.db :refer [db router]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.api :refer [fetch-chart-data!]]))

(defn open-detailed-view
  [market pair]
  (fetch-chart-data! (name market) (name pair))
  (swap! db assoc-in [:ui/detailed-view] [market pair]))

(defn close-detailed-view [] (swap! db assoc-in [:ui/detailed-view] nil))

(defn to-screen
  [screen]
  (do (close-detailed-view) (swap! router assoc-in [:screen] screen)))

(defn add-to-favs
  [tupl]
  (do (swap! db update-in [:user :favorites] #(into % (vector tupl)))
      (persist-user-currents!)))

(defn remove-from-favs
  [tupl]
  (do (swap! db update-in
        [:user :favorites]
        #(vec (filter (fn [pair]
                        (when (and (= (first pair) (first tupl))
                                   (= (last pair) (last tupl)))))
                      %)))
      (persist-user-currents!)))

(defn toggle-filter
  [filter-str]
  (do (swap! db assoc :ui/detailed-view nil)
      (swap! db update-in
        [:ui/current-filter]
        #(if (= filter-str (:ui/current-filter @db)) nil filter-str))))

(defn update-filter-q [q] (swap! db assoc-in [:ui/filter-q] q))
