(ns app.api
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string :refer [split-lines]]
            [clojure.walk]
            [cljs.core.async :as a :refer [<! >! chan timeout sliding-buffer]]
            [cljs-http.client :as http]
            [mount.core :refer [defstate]]
            [app.db :refer [db]]
            [app.config :refer [config]]
            [app.actions.tray :refer [set-title!]]
            [app.actions.api :refer [evt->db state->db chart-data->db]]
            [app.logic.curr :refer [best-pairs]]))

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
                   (evt->db (js->clj (js/JSON.parse msg))))
                 (recur)))))

(defn stop-ws! [] (prn "Stopping ws ...") (reset! t false))

(defstate ws-loop :start (listen-ws!) :stop (stop-ws!))

(defn set-initial-data-fetching []
  (swap! db assoc :ui/fetching-init-data? true))

(defn unset-initial-data-fetching []
  (swap! db assoc :ui/fetching-init-data? false))

(defn fetch-state!
  []
  (go (let [_ (set-initial-data-fetching)
            endpoint (str (:http-endpoint config) "/events")
            response (<! (http/get endpoint {:with-credentials? false}))]
        (state->db (:body response))
        (unset-initial-data-fetching))))

