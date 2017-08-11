(ns app.renderer
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [taoensso.timbre :refer [log  trace  debug  info  warn  error  fatal]])
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string :refer [split-lines]]
            [clojure.walk]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [taoensso.timbre :as timbre]
            [haslett.client :as ws]))

(defn init []
  (js/console.log "Starting Application"))

(defonce state        (atom {}))

(go
  (let [stream (<! (ws/connect "ws://127.0.0.1:8080" {:source (chan 5)}))]
    (go-loop []
      (let [msg (<! (:source stream))
            clj-msg (clojure.walk/keywordize-keys (js->clj (js/JSON.parse msg)))]
        (swap! state assoc (:Market clj-msg) clj-msg)
        (js/console.log (clj->js @state)))
      (recur))))

(defn curr-pair-row [data key]
  (let [pair (:CurrencyPair data)
        avg (:Avg data)]
     (str pair " : " avg)))

(defn root-component []
  [:div
   (for [[name info] @state]
     ^{:key key}
     [:div [:strong name] (str " " (curr-pair-row info key))])])

(reagent/render
  [root-component]
  (js/document.getElementById "container"))
