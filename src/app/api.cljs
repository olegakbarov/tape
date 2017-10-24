(ns app.api
  (:require-macros [cljs.core.async.macros :as a])
  (:require [clojure.string :as string :refer [split-lines]]
            [clojure.walk]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [haslett.client :as ws]
            [app.db :refer [db]]
            [app.config :refer [config]]
            [app.actions.tray :refer [set-title!]]
            [app.db :refer [update-ticker!]]
            [app.logic.curr :refer [best-pairs]]))

(defn listen-ws! []
 (a/go
  (let [endpoint (:ws-endpoint config)
        stream (<! (ws/connect endpoint  {:source (chan 1)}))]
    (a/go-loop []
      (let [msg (<! (:source stream))
            cmsg (clojure.walk/keywordize-keys (js->clj (js/JSON.parse msg)))]
         (update-ticker! cmsg))
      (recur)))))

