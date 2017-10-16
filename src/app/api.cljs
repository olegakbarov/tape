(ns app.api
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string :refer [split-lines]]
            [clojure.walk]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [haslett.client :as ws]
            [app.db :refer [db]]
            [app.config :refer [config]]
            [app.actions.tray :refer [set-title!]]
            [app.actions.db :refer [update-ticker!]]
            [app.logic.curr :refer [best-pairs]]))

(defn start-loop! []
 (go
  ;; TODO handle conn errors
  (let [endpoint (:ws-endpoint config)
        stream (<! (ws/connect endpoint  {:source (chan)}))]
    (go-loop []
      (let [msg (<! (:source stream))
            clj-msg (clojure.walk/keywordize-keys (js->clj (js/JSON.parse msg)))]
       (update-ticker! clj-msg))
      (recur)))))

