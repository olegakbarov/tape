(ns app.eventloop
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [app.db :refer [db]]
            [app.logic.curr :refer [best-pairs]]
            [app.actions.tray :refer [set-title!]]
            [app.actions.notifs :refer [add-notif!]]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [mount.core :refer [defstate]]))

(def t (atom false))

(defn start-title-loop! []
 (reset! t true)
 (go
  (while @t
   (<! (timeout 3000))
   (let [m (:markets @db)
         btc (js/parseInt (best-pairs m :BTC-USD))]
     (set-title! btc)))))

(defn stop-title-loop! []
  (prn "Stopping title loop...")
  (reset! t false))

(defstate title-loop
  :start (start-title-loop!)
  :stop (stop-title-loop!))

;; --------------

(defonce n (atom false))

(defn has-notifs? []
 (pos? (-> @db
           :user/notifs
           count)))

(defn dispatch-notif?
 "Compares latest `snapshot` of markets with users'
 notifs and dispatches when conditions met"
 [markets notifs]
 (map
  (fn [ntf]
   ; (let [{:keys [active pair market price change]} ntf
   ;       p (get-in markets [market pair])]
    (js/console.log ntf)
    (when true
     (add-notif!
       (str "Title")
       (str "Text"))))
  notifs))

(defn start-notifs-loop! []
 (reset! n true)
 (when (and @n (has-notifs?))
  (go
   (while @n
    (prn "In notifs loop ...")
    (<! (timeout 3000))
    (let [markets (-> @db :markets)
          notifs (-> @db :user/notifs)]
     (dispatch-notif? markets notifs))))))

(defn stop-notifs-loop! []
 (reset! n false)
 (prn "Stopped notifs loop."))

(defstate notifs-loop
  :start (start-notifs-loop!)
  :stop (stop-notifs-loop!))
