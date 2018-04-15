(ns app.config (:require-macros [adzerk.env :as env]))

(goog-define commit "n/a")

(env/def
  VERSION "0.1.0"
  COMMIT commit
  SENTRY nil
  WS_ENDPOINT (or WS_ENDPOINT "wss://cryptounicorns.io/api/v1/events/stream")
  HTTP_ENDPOINT (or HTTP_ENDPOINT "https://cryptounicorns.io/api/v1")
  AUTO_UPDATE_ENDPOINT (or AUTO_UPDATE_ENDPOINT "https://hazel-server-hmfiviliok.now.sh")
  ENV (or ENV :dev))

(def config
  {:version VERSION
   :commit COMMIT
   :env ENV
   :ws-endpoint WS_ENDPOINT
   :http-endpoint HTTP_ENDPOINT
   :update-endpoint AUTO_UPDATE_ENDPOINT
   :sentry SENTRY})

(js/console.log config)
