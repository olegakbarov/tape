(ns app.actions.ui
  (:require [app.db :refer [db router]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.api :refer [fetch-chart-data!]]))

(defn close-detailed-view [] (swap! db assoc-in [:ui/detailed-view] nil))

(defn open-detailed-view
  [market pair]
  (fetch-chart-data! (name market) (name pair))
  (swap! db assoc-in [:ui/detailed-view] [market pair]))


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
  (reset! db
    (merge @db
       {:ui/detailed-view nil
        :ui/current-filter (if (= filter-str (:ui/current-filter @db))
                               nil
                               filter-str)})))


(defn update-filter-q [q]
  (swap! db assoc-in [:ui/filter-q] q))

(defn update-filter-market [m]
  (swap! db assoc-in [:ui/market-filter] m))

(defn toggle-filterbox
  []
  (let [open? (-> @db
                  :ui/filterbox-open?)]
    (swap! db assoc :ui/filterbox-open? (not open?))))


(defn toggle-edit-portfolio-view
  "Without params resets key in db to `nil` and thus closes the detailed view.
  With id provided opens view and populates fields with item with this id."
  ([] (swap! db assoc :ui/folio-edit nil))
  ([id]
   (let [{:keys [market currency amount id]} (-> @db
                                                 :user
                                                 :portfolio
                                                 (get id))
         new-m  {:market (name market)
                 :currency (name currency)
                 :amount amount
                 :id id}]
     (reset! db
       (merge @db
         {:ui/folio-edit id
          :form/portfolio new-m})))))


(defn open-add-portfolio-view
  []
  (swap! db assoc :ui/folio-add true))


(defn close-add-portfolio-view
  []
  (swap! db assoc :ui/folio-add true))

(defn close-every-portfolio-view
  []
  (let [folio (-> @db :form/portfolio)]
   (reset! db
    (merge @db
      {:ui/folio-add false
       :ui/folio-edit nil
       :form/portfolio {:market ""
                        :currency ""
                        :amount ""}}))))

;; ALERTS

(defn toggle-edit-alert-view
  "Without params resets key in db to `nil` and thus closes the detailed view.
  With id provided opens view and populates fields with item with this id."
  ([] (swap! db assoc :ui/alert-edit nil))
  ([id]
   (let [a (-> @db
               :user
               :alerts
               (get id))
         {:keys [market pair amount]} a
         new-alert  {:market market
                     :pair pair
                     :amount amount
                     :repeat (:repeat a)
                     :id id}]
     (reset! db (merge @db
                 {:ui/alert-edit id
                  :form/alerts new-alert})))))


(defn open-add-alert-view
  []
  (swap! db assoc :ui/alert-add true))


(defn close-add-alert-view
  []
  (swap! db assoc :ui/alert-add true))


(defn close-every-alert-view
  []
  (let [old-alert (-> @db :form/alert)
        new-alert (merge old-alert
                    {:market ""
                     :pair ""
                     :amount ""})]
    (reset! db
     (merge @db
      {:ui/alert-add false
       :ui/alert-edit nil
       :form/alert new-alert}))))


(defn to-screen
  [screen]
  (do (close-detailed-view)
      (close-every-portfolio-view)
      (close-every-alert-view)
      (swap! router assoc-in [:screen] screen)))



