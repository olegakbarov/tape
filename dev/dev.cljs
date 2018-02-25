(ns dev
  (:require [clojure.pprint :refer [pprint]]
            [mount.core :as mount]
            [app.renderer]))

(enable-console-print!)

(defn start [] (mount/start))

(defn go [] (mount/stop))

(defn reset [] (go))

