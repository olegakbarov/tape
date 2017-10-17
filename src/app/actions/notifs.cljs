(ns actions.notifs)

(defn add-notif [title text]
  (js/Notification.
    title
    (clj->js {:body text})))

