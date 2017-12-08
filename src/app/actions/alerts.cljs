(ns app.actions.alerts
  (:require [app.db :refer [db]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.utils.core :refer [generate-uuid]]))

(comment {:id "uuid-uuid"
          :market :bitfinex
          :pair :BTC-LTC
          ; :change :below
          :amount 5000
          :archived false
          :repeat true})

(defn render-alert!
  [title text]
  (js/Notification. title (clj->js {:body text})))

(defn create-alert
  "Adds notif to state and persists it to disk"
  [a]
  (let [id (generate-uuid)]
    (do (swap! db assoc-in [:user :alerts id]
          (merge a {:id id
                    :archived false
                    :market (-> a
                                :market
                                keyword)
                    :pair (-> a
                              :pair
                              keyword)}))
        (js/console.log a)
        (persist-user-currents!))))

(defn alert->archived
  [id]
  (do (swap! db update-in [:user :notifs id] #(merge % {:archived true}))
      (persist-user-currents!)))