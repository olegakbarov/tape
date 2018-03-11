(ns app.eventloop
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as a :refer [<! >! chan timeout]]
            [reagent.core :as r]
            [klang.core :refer-macros [info! warn! erro! crit! fata! trac!]]
            [app.db :refer [db]]
            [app.logic.curr :refer [best-pairs]]
            [app.actions.tray :refer [set-title!]]
            [app.actions.alerts :refer [render-alert! alert->archived]]
            [mount.core :refer [defstate]]))

;; title loop switch
(defonce t (atom false))

(def timeout-ms 3000)

(defn start-title-loop!
  "Continuosly updates title with latest bitcoin(TODO) price "
  []
  ; (reset! t true)
  (go (while @t
             (<! (timeout timeout-ms))
             (let [m @(r/cursor db [:markets])
                   btc (js/parseInt (best-pairs m :BTC-USD))]
               (set-title! btc)))))

(defn stop-title-loop! []
  (info! "Stopping title loop...")
  (reset! t false))

(defstate title-loop
  :start (start-title-loop!)
  :stop (stop-title-loop!))

;; notifs loop switch
(defonce n (atom false))


(defn dispatch-notif?
  "Compares latest `snapshot` of markets with users'
 notifs and dispatches when conditions met
 ---
 markets - hashmap
 notifs - hashmap"
  [markets notifs]
  (doall
   (map
    (fn [ntf]
      (let [{:keys [archived pair market price change id]} ntf
            p (get-in markets [market pair])]
        (when-not archived
          (do (render-alert!
               (str (name market) " " (name pair) " price " price)
               (str pair " price crossed " change " with the price of " price))
              (alert->archived id)))))
    notifs)))

(defn start-notifs-loop!
  []
  (reset! n true)
  (info! "Notifs loop started ...")
  (let [has-notifs? (r/track #(-> @db
                                  :user/alerts
                                  count))]
    (when (and @n has-notifs?)
      (go (while @n
                 (<! (timeout 3000))
                 (let [markets @(r/cursor db [:markets])
                       notifs @(r/track #(-> @db
                                             :user/notifs
                                             vals))]
                   (dispatch-notif? markets notifs)))))))

(defn stop-notifs-loop! []
  (reset! n false)
  (info! "Stopped notifs loop."))

(defn start-offline-watch-loop!
  [online-cb offline-cb]
  (.addEventListener js/window "offline" #(offline-cb))
  (.addEventListener js/window "online" #(online-cb)))

(defstate notifs-loop
  :start (start-notifs-loop!)
  :stop (stop-notifs-loop!))
