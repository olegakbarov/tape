(ns app.config)

(def config
  {:env :dev
   :ws-endpoint "wss://cryptounicorns.io/api/v1/tickers-changes/stream"
   :http-endpoint "https://cryptounicorns.io/api/v1"})
