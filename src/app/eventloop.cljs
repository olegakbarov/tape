(ns app.eventloop
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [app.db :refer [db]]
            [app.logic.curr :refer [best-pairs]]
            [app.actions.tray :refer [set-title!]]
            [cljs.core.async :as a :refer [<! >! chan timeout]]))

(def t (atom false))

(defn start-title-updater! []
 (reset! t true)
 (go
  (while @t
   (<! (timeout 3000))
   (let [m (:markets @db)
         btc (js/parseInt (best-pairs m :BTC-USD))]
     (set-title! btc)))))

(defn stop-title-updater! []
  (reset! t false))

; (defstate title-loop :start (start-title-updater!)
;                      :stop (stop-title-updater!))
