(ns app.config (:require-macros [adzerk.env :as env]))

(env/def
  SENTRY nil
  WS_ENDPOINT (or WS_ENDPOINT "wss://cryptounicorns.io/api/v1/events/stream")
  HTTP_ENDPOINT (or HTTP_ENDPOINT "https://cryptounicorns.io/api/v1")
  AUTO_UPDATE_ENDPOINT (or AUTO_UPDATE_ENDPOINT "https://hazel-server-hmfiviliok.now.sh")
  ENV (or ENV :dev))

(def config
  {:env ENV
   :ws-endpoint WS_ENDPOINT
   :http-endpoint HTTP_ENDPOINT
   :update-endpoint AUTO_UPDATE_ENDPOINT
   :sentry SENTRY})
