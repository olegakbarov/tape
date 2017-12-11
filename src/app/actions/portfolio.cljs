(ns app.actions.portfolio
  (:require [app.db :refer [db]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.utils.core :refer [generate-uuid]]))

(defn create-portfolio-record
  "Adds notif to state and persists it to disk"
  [a]
  (let [id (generate-uuid)]
    (do (swap! db
               assoc-in
               [:user :portfolio id]
               (merge a
                      {:id id
                       :market (-> a
                                   :market
                                   keyword)
                       :currency (-> a
                                     :currency
                                     keyword)}))
        (persist-user-currents!))))

(defn update-portfolio-record
  [updated]
  (let [{:keys [id]} updated]
    (do (swap! db update-in [:user :portfolio id] updated))))

(defn remove-portfolio-record
  [id]
  (do (swap! db update-in [:user :portfolio] dissoc id)
      (persist-user-currents!)))
