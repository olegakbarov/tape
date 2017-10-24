(ns app.motion
  (:require [cljsjs.react-motion]
            [reagent.core :as reagent]))

(def Motion (reagent/adapt-react-class js/ReactMotion.Motion))
(def StaggeredMotion (reagent/adapt-react-class js/ReactMotion.StaggeredMotion))
(def TransitionMotion (reagent/adapt-react-class js/ReactMotion.TransitionMotion))

(def spring js/ReactMotion.spring)
(def presets js/ReactMotion.presets)
