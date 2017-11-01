(ns app.actions.storage
  (:require [clojure.walk]
            [app.db :refer [db router]]
            [cljs.reader :as reader]))

(def electron (js/require "electron"))
(def remote (.-remote electron))

(def default-file {:portfolio []
                   :favorites []
                   :settings {}})

(defn- update-db [k data]
 (swap! db assoc k data))

(defn save-data-to-file! [content]
  (let [fs (js/require "fs")
        path (js/require "path")
        p (str (.getPath (.-app remote) "userData") "/portfolio.edn")]
   (try
    (.writeFile fs p content
      #(js/console.log "done."))
    (catch :default e e
      (js/console.log e)))))

(defn read-data-file!
  "TODO describe data file"
  [name]
  (let [fs (js/require "fs")
        p (.getPath (.-app remote) "userData")]
    (try
      (let [raw-file (.readFileSync fs (str p "/" name) "utf-8")
            contents (cljs.reader/read-string raw-file)
            {:keys [portfolio settings favorites]} contents]
       (do
        (update-db :user/portfolio portfolio)
        (update-db :user/favorites favorites)
        (update-db :user/settings  settings)))
      (catch :default e e
        (when (= "ENOENT" (.-code e))
              (save-data-to-file! default-file))))))

