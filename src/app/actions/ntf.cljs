(ns app.actions.ntf
  (:require [app.db :refer [db]]))

(defn clear-ntf [] (swap! db assoc-in [:ui/ntf] nil))

(defn ntf-gone-offline
  []
  (let [n {:color "#ff0000"
           ;; red
           :text "Connection issues..."}]
    (swap! db assoc-in [:ui/ntf] n)))

(defn ntf-gone-online
  []
  (let [n {:color "#12D823" ;; green
           :text "Connected."}]
    (do (swap! db assoc-in [:ui/ntf] n) (js/setTimeout #(clear-ntf) 3000))))
