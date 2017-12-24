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

(defn- to-dollar
  "Returns dollar price of curr on this market"
  [amount curr market]
  ;; TODO handle case where altcoin doesn't have dollar value (if any)
  (let [pair (keyword (str (name curr) "-USD"))]
    (* amount
       (-> @db
           :markets
           market
           pair
           :last))))

(defn get-total-worth
  []
  (let [folio (-> @db
                  :user
                  :portfolio)]
    (reduce (fn [acc [id item]]
              (let [{:keys [amount currency market]} item]
                (+ acc (to-dollar (js/parseFloat amount) currency market))))
            0
            folio)))
