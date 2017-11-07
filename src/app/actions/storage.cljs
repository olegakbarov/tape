(ns app.actions.storage
  (:require [clojure.walk]
            [app.db :refer [db router]]
            [cljs.reader :as reader]
            [mount.core :refer [defstate]]))

(def electron (js/require "electron"))
(def remote (.-remote electron))

(def default-file {:portfolio []
                   :favorites []
                   :settings {}})

(def data-file-name "data-file.edn")

(defn- update-db [k data]
 (swap! db assoc k data))

(defn ->file!
  "Writes arg to user data file"
  [content]
  (let [fs (js/require "fs")
        path (js/require "path")
        p (str (.getPath (.-app remote) "userData") data-file-name )]
   (try
    (.writeFile fs p content
      #(js/console.log "done."))
    (catch :default e e
      (js/console.log e)))))

(defn read-data-file!
  "File with following signature
   {:portfolio [...]
    :favorites [...]
    :settings {...}}"
  []
  (let [fs (js/require "fs")
        p (.getPath (.-app remote) "userData")]
    (try
      (let [raw-file (.readFileSync fs (str p data-file-name) "utf-8")
            contents (cljs.reader/read-string raw-file)
            {:keys [portfolio settings favorites]} contents]
        (update-db :user/portfolio portfolio)
        (update-db :user/favorites favorites)
        (update-db :user/settings  settings)
        (update-db :user/notifs    notifs))
      (catch :default e e
        (when (= "ENOENT" (.-code e))
              (->file! default-file))))))

(defn persist-user-currents
  "Saves updated users' state. Accpets :key and _updated_ content"
  [key content]
  (let [portfolio (:user/portoflio @db)
        settings (:user/settings @db)
        favorites (:user/favorites @db)
        notifs (:user/notifs @db)]
    (->file!
     (merge
      {:portfolio portfolio
       :favorites favorites
       :settings settings
       :notifs notifs}
      {key content}))))

(defstate user-data :start (read-data-file!))

