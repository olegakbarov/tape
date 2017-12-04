(ns app.screens.alerts
  (:require [reagent.core :as r]
            [clojure.string :as s]
            ; [cljsjs.classnames]
            ; [cljsjs.react-input-autosize]
            [cljsjs.react-select]
            [app.actions.ui :refer [to-screen]]
            [app.db :refer [db]]
            [app.logic.curr :refer [get-market-names get-crypto-currs]]))

(defonce value (r/atom nil))

; (def cn
;  (js/require "classNames"))

; (aset js/window "classNames" cn)

(defn select-ui
  []
  [:> js/window.Select
   {:value @value,
    :options #js
              [#js {:value "a", :label "alpha"} #js {:value "b", :label "beta"}
               #js {:value "c", :label "gamma"}],
    :onChange #(reset! value (aget % "value"))}])

(defn alerts [] [:div#wrapper [select-ui]])
