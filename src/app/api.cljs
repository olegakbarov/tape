(ns app.api
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [taoensso.timbre :refer [log  error  fatal]])
  (:require [clojure.string :as string :refer [split-lines]]
            [clojure.walk]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [taoensso.timbre :as timbre]
            [haslett.client :as ws]
            [app.db :refer [db]]
            [app.config :refer [config]]
            [app.actions :as actions]))

(defn start-loop! []
  (go
    ;; TODO handle conn errors
    (let [endpoint (:ws-endpoint config)
          stream (<! (ws/connect endpoint  {:source (chan)}))]
      (go-loop []
        (let [msg (<! (:source stream))
              clj-msg (clojure.walk/keywordize-keys (js->clj (js/JSON.parse msg)))]

          ;; (js/console.log (str (:markets @db)))
          (actions/update-db-with-ticker clj-msg))
        (recur)))))

