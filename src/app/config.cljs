(ns app.config
  (:require-macros [adzerk.env :as env]))

(env/def
  SENTRY nil
  WS_ENDPOINT (or WS_ENDPOINT "wss://cryptounicorns.io/api/v1/events/stream")
  HTTP_ENDPOINT (or HTTP_ENDPOINT "https://cryptounicorns.io/api/v1")
  ENV (or ENV :dev))

(def config
  {:env ENV
   :ws-endpoint WS_ENDPOINT
   :http-endpoint HTTP_ENDPOINT
   :sentry SENTRY})
