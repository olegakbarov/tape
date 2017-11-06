(ns app.actions.notifs
 (:require [app.db :refer [db]]))

(defn render-notif! [title text]
 (js/Notification.
  title
  (clj->js {:body text})))

(defn create-notif [ntf]
 (assert (map #(not (nil? %)) ntf))
 (let [id (random-uuid)]
  (swap! db assoc-in [:user/notfis id]
   (merge ntf {:id id}))))

(defn notif->archived [id]
 (swap! db update-in [:user/notifs id]
  #(merge % {:archived true})))

