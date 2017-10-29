(ns app.listeners
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [app.db :refer [db]]
            [app.logic.curr :refer [best-pairs]]
            [app.actions.tray :refer [set-title!]]
            [cljs.core.async :as a :refer [<! >! chan timeout]]))

(def t (atom false))

(defn start-listeners! []
 (reset! t true)
 (go
  (while @t
   (<! (timeout 3000))
   (let [m (:markets @db)
         btc (js/parseInt (best-pairs m :BTC-USD))]
     (set-title! btc)))))

(defn stop-listeners! []
  (reset! t false))

