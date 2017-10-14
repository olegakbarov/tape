(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.components.header :refer [Header]]
            [app.components.dropdown :refer [dropdown]]
            [app.actions :as actions]
            [app.db :refer [db]]
            [app.logic :refer [get-market-names
                               get-crypto-currs]]
            [goog.functions]
            [app.components.ui :refer [Wrapper
                                       Container
                                       Icon]]))

(defn alerts []
  [Container
   [Header
    [Icon
     #(actions/to-screen :bestprice)
     "icons/arrow-left.svg"]]
   [Wrapper
     "alerts"]])
