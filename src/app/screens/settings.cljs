(ns app.screens.settings
  (:require [reagent.core :as reagent]))

(defn wrapper [arg]
  (let [{:keys [children props]} arg]
    (js/console.log props)
    [:div children]))

(def w (reagent/reactify-component wrapper))

(defn outer [& p]
 (fn [] (reagent/create-element w #js {} p)))

(defn settings []
  [:div#wrapper
    [outer
      ^{:key "dsfd"}
      [:div "dsfdsf"]
      ^{:key "fsfds"}
      [:div "kkkkk"]]])
