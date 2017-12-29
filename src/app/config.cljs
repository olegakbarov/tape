(ns app.config)

(def config
  {:env :dev
   ; :ws-endpoint "ws://localhost:8080/api/v1/tickers/stream"
   :ws-endpoint "wss://cryptounicorns.io/api/v1/tickers-changes/stream"
   :http-endpoint "https://cryptounicorns.io/api/v1/tickers-changes"})
