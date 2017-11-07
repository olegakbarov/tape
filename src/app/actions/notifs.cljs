(ns app.actions.notifs
 (:require [app.db :refer [db]]
           [app.actions.storage :refer [persist-user-currents]]))

(defn render-notif! [title text]
 (js/Notification.
  title
  (clj->js {:body text})))

(defn create-notif
 "Adds notif to state and persists it to disk"
 [ntf]
 (let [id (random-uuid)]
  (do
   (swap! db assoc-in [:user/notfis id]
    (merge ntf {:id id}))
   (persist-user-currents :notifs (-> @db :user/notifs)))))

(defn notif->archived [id]
 (do
  (swap! db update-in [:user/notifs id]
   #(merge % {:archived true}))
  (persist-user-currents :notifs (-> @db :user/notifs))))

