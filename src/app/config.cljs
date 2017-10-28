(ns app.config)

(def config
  {:env :dev
   :ws-endpoint "ws://localhost:8080/api/v1/tickers/stream"
   :http-endpoint "http://localhost:8081/api/v1"})


