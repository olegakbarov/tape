(ns app.config)

(def config
  {:env :dev
   :ws-endpoint "ws://127.0.0.1:8088/api/v1/events/stream"
   :http-endpoint "http://127.0.0.1:8088/api/v1"})
