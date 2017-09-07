(ns app.actions
  (:require [clojure.walk]
            [app.db :refer [db]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(def electron (js/require "electron"))
(def remote (.-remote electron))

;; navigate to another screen
(defn to-screen [screen]
  (swap! db assoc-in [:ui/screen] screen))

;; event from websocket keywordized & kebabcased
(defn process-ws-event [t]
  (clojure.walk/keywordize-keys
    (into {}
      (for [[k v] t]
        [(->kebab-case k) v]))))

;; adds ticker to db
(defn update-db-with-ticker [ticker]
  (let [t (process-ws-event ticker)
        {:keys [market currency-pair]} t]
    (swap! db assoc-in [:markets market currency-pair] t)))

;; Notifs
(defn test-notif [title text]
  (js/Notification.
    title
    (clj->js {:body text})))

;; Filestorage
(defn cache-state []
  (let [dialog (.-dialog remote)
        fs (js/require "fs")]
    (.showSaveDialog dialog
                     #js{:defaultPath "/Users"}
                     (fn [filename]
                       (.writeFile fs filename @db)))))

(defn save-market-state []
  (let [fs (js/require "fs")
        path (js/require "path")
        current-dir (.resolve path ".")]
    (.writeFile fs
      (str current-dir "/db_state.edn"
        (-> @db :markets)))))

(defn expand-pair-row [pair market]
  (let [[p m] (:ui/expanded-row @db)]
    (swap! db assoc-in [:ui/expanded-row]
                       (if (and (= p pair) (= m market))
                           []
                           [pair market]))))
