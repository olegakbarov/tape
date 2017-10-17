(ns app.actions.storage
  (:require [clojure.walk]
            [app.db :refer [db router]]
            [cljs.reader :as reader]))

(def electron (js/require "electron"))
(def remote (.-remote electron))

(defn read-file! [name]
  (let [fs (js/require "fs")
        p (.getPath (.-app remote) "userData")]
    (try
      (let [raw-file (.readFileSync fs (str p "/" name) "utf-8")
            contents (cljs.reader/read-string raw-file)]
        (js/console.log contents)
        (swap! db assoc :portfolio (:portfolio contents)))
      (catch :default e e
        (js/console.log e)))))

; ;; TODO: refac
; ; (defn cache-state []
; ;   (let [dialog (.-dialog remote)
; ;         fs (js/require "fs")]
; ;     (.showSaveDialog dialog
; ;                      #js{:defaultPath "/Users"}
; ;                      (fn [filename]
; ;                        (.writeFile fs filename @db)))))
; ; ;; TODO: refac
; ; (defn save-market-state []
; ;   (let [fs (js/require "fs")
; ;         path (js/require "path")
; ;         current-dir (.resolve path ".")]
; ;     (.writeFile fs
; ;       (str current-dir "/db_state.edn"
; ;         (-> @db :markets)))))
