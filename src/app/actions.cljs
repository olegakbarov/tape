(ns app.actions
  (:require [clojure.walk]
            [app.db :refer [db router]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(def electron (js/require "electron"))
(def remote (.-remote electron))

;; Router
(defn to-screen [screen]
  (swap! router assoc-in [:screen] screen))

;; API
(defn process-ws-event [t]
  (clojure.walk/keywordize-keys
    (into {}
      (for [[k v] t]
        [(->kebab-case k) v]))))

(defn update-db-with-ticker [ticker]
  (let [t (process-ws-event ticker)
        {:keys [market currency-pair]} t]
    (swap! db assoc-in [:markets market currency-pair] t)))

;; UI
(defn expand-pair-row [pair market]
  (let [[p m] (:ui/expanded-row @db)]
    (swap! db assoc-in [:ui/expanded-row]
                       (if (and (= p pair) (= m market))
                           []
                           [pair market]))))

;; Notifs
(defn test-notif [title text]
  (js/Notification.
    title
    (clj->js {:body text})))

;; Portofolio
(defn add-record [rec]
  (swap! db update-in [:portfolio] conj rec))

(defn save-portfolio []
  (let [fs (js/require "fs")
        p (.getPath (.-app remote) "userData")
        folio (:portfolio @db)]
    (.writeFile fs (str p "/portfolio.edn") {:portfolio folio})))

(defn read-local-portfolio! []
  (let [fs (js/require "fs")
        p (.getPath (.-app remote) "userData")]
    (try
      (let [raw-file (.readFileSync fs (str p "/portfolio.edn") "utf-8")
            contents (cljs.reader/read-string raw-file)]
        (swap! db assoc :portfolio (:portfolio contents)))
      (catch :default e e
        (js/console.log e)))))

;; Filestorage
(defn cache-state []
  (let [dialog (.-dialog remote)
        fs (js/require "fs")]
    (.showSaveDialog dialog
                     #js{:defaultPath "/Users"}
                     (fn [filename]
                       (.writeFile fs filename @db)))))

(defn log-folio []
  (js/console.log (:portfolio @db)))

(defn save-market-state []
  (let [fs (js/require "fs")
        path (js/require "path")
        current-dir (.resolve path ".")]
    (.writeFile fs
      (str current-dir "/db_state.edn"
        (-> @db :markets)))))

