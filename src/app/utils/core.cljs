(ns app.utils.core
  (:require [app.constants.currs :as c]))

(defn curr-symbol->name [s]
  (:name
   (first
    (filter
     (fn [p] (= (:symbol p) s))
     c/pairs))))

