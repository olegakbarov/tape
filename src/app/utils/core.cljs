(ns app.utils.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [app.constants.currs :as c]
            [cljs.core.async :refer [put! chan <! >! timeout close!]]))

(defn curr-symbol->name [s]
  (:name
   (first
    (filter
     (fn [p] (= (:symbol p) s))
     c/pairs))))

(defn debounce [somefunc ms]
  (let [in (chan)
        out (chan)]
    (go-loop [last-val nil
              timer (timeout ms)]
      (let [val (if (nil? last-val) (<! in) last-val)
            [new-val ch] (alts! [in timer])]
        (condp = ch
          timer (do (>! out val) (recur nil (timeout ms)))
          in (if new-val (recur new-val timer) (close! out)))))

    ; call debounced function on the given function/handler
    (go-loop []
       (let [val (<! out)]
         (somefunc val)
         (recur)))

    ;return in event channel
    in))
