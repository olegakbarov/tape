(ns app.eventloop
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as a :refer [<! >! chan timeout sliding-buffer]]
            [reagent.core :as r]
            [mount.core :refer [defstate]]
            [klang.core :refer-macros [info! warn! erro! crit! fata! trac!]]
            [app.db :refer [db]]
            [app.logic.curr :refer [best-pairs]]
            [app.actions.tray :refer [set-title!]]
            [app.actions.alerts :refer [render-alert! alert->archived]]
            [app.actions.api :refer [evt->db state->db chart-data->db]]
            [app.config :refer [config]]))

(defonce t (atom false))

(defonce retries (atom 0))

(declare create-ws-conn!)

(defn reconnect-ws
  [w]
  (if (< @retries 10)
    (.setTimeout js/window (fn [] (.close w (create-ws-conn!))) 3000)))

(defn heart-beat [] (.setTimeout js/window heart-beat 2000))

(defn ->msg [c e] (go (>! c (.-data e))))

(defn ->open
  [w e]
  (.send w (pr-str "ping"))
  (heart-beat)
  (js/console.log "ws conn is open"))

(defn ->close [w e] (js/console.log "closing ws conn...") (reconnect-ws w))

(defn ->error [w e] (reconnect-ws w))

(defn create-ws-conn!
  []
  (let [w (js/WebSocket. (:ws-endpoint config))
        _ (swap! retries inc)
        c (chan (sliding-buffer 1))]
    (set! (.-onmessage w) #(->msg c %))
    (set! (.-onopen w) #(->open w %))
    (set! (.-onclose w) #(->close w %))
    (set! (.-onerror w) #(->error w %))
    c))

(defn listen-ws!
  []
  (reset! t true)
  (go (let [endpoint (:ws-endpoint config)
            stream (create-ws-conn!)]
        (go-loop []
          (let [msg (<! stream)]
            (try
              (evt->db (js->clj (js/JSON.parse msg)))
              (catch :default e))
                ;; TODO log and report
                ;(js/console.log e))))
            (recur))))))

(defn stop-ws! [] (prn "Stopping ws ...") (reset! t false))

(defstate ws-loop :start (listen-ws!) :stop (stop-ws!))

(def timeout-ms 3000)

(defn start-title-loop!
  "Continuosly updates title with latest bitcoin(TODO) price "
  []
  ; (reset! t true)
  (go (while @t
             (<! (timeout timeout-ms))
             (let [m @(r/cursor db [:markets])
                   btc (js/parseInt (best-pairs m :BTC-USD))]))))
               ;(set-title! btc)))))

(defn stop-title-loop! [] (info! "Stopping title loop...") (reset! t false))

(defstate title-loop :start (start-title-loop!) :stop (stop-title-loop!))

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
  ;; TODO track -> cursor
  (let [has-notifs? (r/track #(-> @db
                                  :user/alerts
                                  count))]
    (when (and @n has-notifs?)
      (go (while @n
                 (<! (timeout 3000))
                 (let [markets @(r/cursor db [:markets])
                       ;; TODO track -> cursor
                       notifs @(r/track #(-> @db
                                             :user/notifs
                                             vals))]
                   (dispatch-notif? markets notifs)))))))

(defn stop-notifs-loop! [] (reset! n false) (info! "Stopped notifs loop."))

(defn start-offline-watch-loop!
  [online-cb offline-cb]
  (.addEventListener js/window "offline" #(offline-cb))
  (.addEventListener js/window "online" #(online-cb)))

(defstate notifs-loop :start (start-notifs-loop!) :stop (stop-notifs-loop!))
