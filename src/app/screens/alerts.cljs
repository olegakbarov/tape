(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.logic.curr :refer [get-market-names
                                    get-crypto-currs]]))

(defn alerts []
 [:div#wrapper
   "alerts"])
