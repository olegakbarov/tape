(ns app.actions.storage
  (:require [clojure.walk]
            [app.db :refer [db router]]))

(def electron (js/require "electron"))
(def ipc (.-ipcRenderer electron))

(defn set-title! [text]
 (let [fmtd (str " $" text)]
   (.send ipc "set-title" fmtd)))


;; TODO
(defn read-portfolio! []
  (.send ipc "read-file" "poritfolio.edn"))
;; (swap! db assoc :portfolio (:portfolio contents)))))

(defn save-portfolio []
 (let [folio (:portfolio @db)]
  (.send ipc "write-file")))



;; TODO: refac
; (defn cache-state []
;   (let [dialog (.-dialog remote)
;         fs (js/require "fs")]
;     (.showSaveDialog dialog
;                      #js{:defaultPath "/Users"}
;                      (fn [filename]
;                        (.writeFile fs filename @db)))))
; ;; TODO: refac
; (defn save-market-state []
;   (let [fs (js/require "fs")
;         path (js/require "path")
;         current-dir (.resolve path ".")]
;     (.writeFile fs
;       (str current-dir "/db_state.edn"
;         (-> @db :markets)))))
