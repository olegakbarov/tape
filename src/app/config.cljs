(ns app.config
  (:require-macros [adzerk.env :as env]))

(env/def
  SENTRY nil
  WS_ENDPOINT :required
  HTTP_ENDPOINT :required
  ENV (or ENV :dev))

(def config
  {:env ENV
   :ws-endpoint WS_ENDPOINT
   :http-endpoint WS_ENDPOINT
   :sentry SENTRY})
