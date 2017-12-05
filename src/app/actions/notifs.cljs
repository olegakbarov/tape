(ns app.actions.notifs
  (:require [app.db :refer [db]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.utils.core :refer [generate-uuid]]))
(comment {:id "uuid-uuid"
          :market :bitfinex
          :pair :BTC-LTC
          :change :below
          :price 5000
          :archived false
          :repeat true})

(defn render-notif!
  [title text]
  (js/Notification. title (clj->js {:body text})))

(defn create-notif
  "Adds notif to state and persists it to disk"
  [ntf]
  (let [id (generate-uuid)]
    (do (swap! db assoc-in [:user/notfis id] (merge ntf {:id id}))
        (persist-user-currents!))))

(defn notif->archived
  [id]
  (do (swap! db update-in [:user/notifs id] #(merge % {:archived true}))
      (persist-user-currents!)))
