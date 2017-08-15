(ns app.api
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [taoensso.timbre :refer [log  error  fatal]])
  (:require [clojure.string :as string :refer [split-lines]]
            [clojure.walk]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [taoensso.timbre :as timbre]
            [haslett.client :as ws]
            [app.db :refer [db]]
            [app.actions :as actions]))

(go
  (let [stream (<! (ws/connect "ws://127.0.0.1:8080" {:source (chan 5)}))]
    (go-loop []
      (let [msg (<! (:source stream))
            clj-msg (clojure.walk/keywordize-keys (js->clj (js/JSON.parse msg)))]
        (actions/update-db-with-ticker clj-msg))
      (recur))))

