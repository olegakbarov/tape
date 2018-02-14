(ns app.actions.storage
  (:require [clojure.walk]
            [app.db :refer [db router]]
            [cljs.reader :as reader]
            [mount.core :refer [defstate]]
            [klang.core :refer-macros [info! warn! erro! crit! fata! trac!]]))

(def electron (js/require "electron"))
(def remote (.-remote electron))

(def default-file {:portfolio {} :favorites {} :settings {}})

(def data-file-name "/data-file.edn")

(defn- update-db [k data] (swap! db assoc k data))

(defn ->file!
  "Writes arg to user data file"
  [content]
  (let [fs (js/require "fs")
        path (js/require "path")
        p (str (.getPath (.-app remote) "userData") data-file-name)]
    (try (.writeFile fs p content #(info! "Data persisted to disk"))
         (catch :default e e (erro! e)))))

(defn read-data-file!
  "File with following signature
   {:portfolio {...}
    :favorites {...}
    :settings {...}}"
  []
  (let [fs (js/require "fs")
        p (.getPath (.-app remote) "userData")]
    ; (js/console.log p)
    (try (let [raw-file (.readFileSync fs (str p data-file-name) "utf-8")
               contents (cljs.reader/read-string raw-file)]
           (update-db :user contents))
         (catch :default e
           e
           (when (= "ENOENT" (.-code e)) (->file! default-file))))))

(defn persist-user-currents!
  "Saves current users' state"
  []
  (->file! (-> @db
               :user)))

(defstate user-data :start (read-data-file!))
