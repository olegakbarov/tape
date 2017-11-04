(ns app.api
  (:require-macros [cljs.core.async.macros :as a])
  (:require [clojure.string :as string :refer [split-lines]]
            [clojure.walk]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [cljs-http.client :as http]
            [haslett.client :as ws]
            [app.db :refer [db]]
            [app.config :refer [config]]
            [app.actions.tray :refer [set-title!]]
            [app.actions.api :refer [ticker->db!]]
            [app.logic.curr :refer [best-pairs]]
            [mount.core :refer [defstate]]))

(defonce t (atom false))

(defn listen-ws! []
 (reset! t true)
 (a/go
  (let [endpoint (:ws-endpoint config)
        stream (<! (ws/connect endpoint {:source (chan (a/sliding-buffer 1))}))]
     (a/go-loop []
      (when @t
       (let [msg (<! (:source stream))
             cmsg (clojure.walk/keywordize-keys (js->clj (js/JSON.parse msg)))]
        (ticker->db! cmsg)
       (recur)))))))

(defn stop-ws! []
  (prn "Stopping ws ...")
  (reset! t false))

(defstate ws-loop :start (listen-ws!)
                  :stop (stop-ws!))

(defn fetch-market-info [market]
 (a/go
  (let [endpoint (str (:http-endpoint config) "/data/markets/" market)
        response (<! (http/get endpoint {:with-credentials? false}))]
    (:body response))))

