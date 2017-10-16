(ns app.actions
  (:require [clojure.walk]
            [app.db :refer [db router]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

;; Router
(defn to-screen [screen]
  (swap! router assoc-in [:screen] screen))

;; UI
(defn expand-pair-row [pair market]
  (let [[p m] (:ui/expanded-row @db)]
    (swap! db assoc-in [:ui/expanded-row]
                       (if (and (= p pair) (= m market))
                           []
                           [pair market]))))

;; Notifs
(defn test-notif [title text]
  (js/Notification.
    title
    (clj->js {:body text})))

