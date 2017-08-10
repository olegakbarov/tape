(ns app.renderer
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string :refer [split-lines]]
            [cljs.core.async :as a :refer [<! >! chan timeout]]
            [haslett.client :as ws]))

(defn init []
  (js/console.log "Starting Application"))

(def join-lines (partial string/join "\n"))

(defonce state        (atom 0))
(defonce shell-result (atom ""))
(defonce command      (atom ""))

(go
  (let [stream (<! (ws/connect "ws://127.0.0.1:8080" {:source (chan 5)}))]
    (go-loop []
      (let [msg (<! (:source stream))]
        (js/console.log msg))
      (recur))))

(defonce proc (js/require "child_process"))

(defn append-to-out [out]
  (swap! shell-result str out))

(defn run-process []
  (when-not (empty? @command)
    (println "Running command" @command)
    (let [[cmd & args] (string/split @command #"\s")
          js-args (clj->js (or args []))
          p (.spawn proc cmd js-args)]
      (.on p "error" (comp append-to-out
                           #(str % "\n")))
      (.on (.-stderr p) "data" append-to-out)
      (.on (.-stdout p) "data" append-to-out))
    (reset! command "")))

(defn root-component []
  [:div
   [:pre "Versions:"
    [:p (str "Node     " js/process.version)]
    [:p (str "Electron " ((js->clj js/process.versions) "electron"))]
    [:p (str "Chromium " ((js->clj js/process.versions) "chrome"))]]
   [:button
    {:on-click #(swap! state inc)}
    (str "Clicked " @state " times")
  [:form
   {:on-submit (fn [^js/Event e]
                 (.preventDefault e)
                 (run-process))}
   [:input#command
    {:type :text
     :on-change (fn [^js/Event e]
                  (reset! command
                          ^js/String (.-value (.-target e))))
     :value @command
     :placeholder "type in shell command"}]]]
   [:pre (join-lines (take 100 (reverse (split-lines @shell-result))))]])

(reagent/render
  [root-component]
  (js/document.getElementById "container"))
