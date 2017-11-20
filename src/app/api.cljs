(ns app.api
 (:require-macros [cljs.core.async.macros :as a])
 (:require [clojure.string :as string :refer [split-lines]]
           [clojure.walk]
           [cljs.core.async :as a :refer [<! >! chan timeout]]
           [cljs-http.client :as http]
           [haslett.client :as ws]
           [app.db :refer [db]]
           [app.config :refer [config]]
           [app.actions.tray :refer [set-title!]]
           [app.actions.api :refer [evt->db]]
           [app.logic.curr :refer [best-pairs]]
           [mount.core :refer [defstate]]))

(defonce t (atom false))

(defonce retries (atom 0))

(declare create-ws-conn!)

(defn reconnect-ws  [w]
 (if (< @retries 10)
     (.setTimeout js/window
      (fn []
       (.close w
        (create-ws-conn!)))
      3000)))
      ;; dispatch notif here

(defn heart-beat []
 (.setTimeout js/window
              heart-beat
              2000))

(defn ->msg [c e]
 (a/go
  (>! c (.-data e))))

(defn ->open [w e]
 (.send w (pr-str "ping"))
 (heart-beat)
 (js/console.log "ws conn is open"))

(defn ->close [w e]
 (js/console.log "closing ws conn...")
 (reconnect-ws w))

(defn ->error [w e]
 (reconnect-ws w))

(defn create-ws-conn! []
 (let [w (js/WebSocket. (:ws-endpoint config))
       _ (swap! retries inc)
       c (chan (a/sliding-buffer 1))]
   (set! (.-onmessage w) #(->msg c %))
   (set! (.-onopen w) #(->open w %))
   (set! (.-onclose w) #(->close w %))
   (set! (.-onerror w) #(->error w %))
   c))

(defn listen-ws! []
 (reset! t true)
 (a/go
  (let [endpoint (:ws-endpoint config)
        stream (create-ws-conn!)]
   (a/go-loop []
    (let [msg (<! stream)]
        ; (js/console.log msg))
     (evt->db (js->clj (js/JSON.parse msg))))
    (recur)))))

(defn stop-ws! []
  (prn "Stopping ws ...")
  (reset! t false))

(defstate ws-loop :start (listen-ws!)
                  :stop (stop-ws!))

(defn fetch-market-info [market]
 (a/go
  (let [endpoint (str (:http-endpoint config) "/data/markets/" market)
        response (<! (http/get endpoint {:with-credentials? false}))]
    (:body response))))

