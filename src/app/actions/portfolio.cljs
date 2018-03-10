(ns app.actions.portfolio
  (:require [reagent.core :as r]
            [app.db :refer [db]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.utils.core :refer [generate-uuid]]))

(defn record-exists?
  "Checks if record with this :market and :currency already exists in db"
  [rec]
  (let [{:keys [market currency]} rec
        folio @(r/cursor db [:user :portfolio])]
    (reduce
     (fn [acc [k v]]
       (if (and (= (:market v) market) (= (:currency v) currency)) true acc))
     false
     folio)))

(defn create-portfolio-record
  "Adds notif to state and persists it to disk"
  [a]
  (let [id (generate-uuid)]
    (do (swap! db assoc-in
          [:user :portfolio id]
          (merge a
                 {:id id
                  :added (.now js/Date)
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
    (swap! db update-in [:user :portfolio id] merge updated)))

(defn remove-portfolio-record
  [id]
  (do (swap! db update-in [:user :portfolio] dissoc id)
      (persist-user-currents!)))

