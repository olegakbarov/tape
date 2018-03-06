(ns app.actions.ui
  (:require [app.db :refer [db router]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.api :refer [fetch-chart-data!]]))

(defn open-detailed-view
  [market pair]
  (fetch-chart-data! (name market) (name pair))
  (swap! db assoc-in [:ui/detailed-view] [market pair]))

(defn close-detailed-view
  []
  (swap! db assoc-in [:ui/detailed-view] nil))

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

(defn update-filter-q
  [q]
  (swap! db assoc-in [:ui/filter-q] q))

(defn toggle-filterbox
  []
  (let [open? (-> @db
                  :ui/filterbox-open?)]
    (swap! db assoc :ui/filterbox-open? (not open?))))

(defn toggle-edit-portfolio-view
  "Without params resets key in db to `nil` and thus closes the detailed view.
  With id provided opens view and populates fields with item with this id."
  ([]
   (swap! db assoc :ui/portfolio-edit-view nil))
  ([id]
   (let [{:keys [market currency amount id]} (-> @db :user :portfolio (get id))]
    (swap! db assoc :ui/portfolio-edit-view id)
    (swap! db update-in [:form/portfolio] merge {:market (name market)
                                                 :currency (name currency)
                                                 :amount amount
                                                 :id id}))))

(defn toggle-add-portfolio-view
  []
  (swap! db assoc :ui/portfolio-add-view
         (not (-> @db :ui/portfolio-add-view))))

(defn close-every-portfolio-view
  []
  ;; TODO: ugly
  (swap! db assoc :ui/portfolio-add-view false)
  (swap! db assoc :ui/portfolio-edit-view nil)
  (swap! db update-in [:form/portfolio] merge {:market ""
                                               :currency ""
                                               :amount ""}))

(defn to-screen
  [screen]
  (do (close-detailed-view)
      (close-every-portfolio-view)
      (swap! router assoc-in [:screen] screen)))

