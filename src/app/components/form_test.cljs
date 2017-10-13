(ns app.components.form-test
  (:require [clojure.string :as s]
            [cljs.spec.alpha :as spec]
            [clojure.test :refer [is]]
            [app.components.form :refer [str->amount]]))

;; STR->AMOUNT

;; can't begin with two zeroes
(is (= (str->amount "00")
       "0"))

;; can't have two separators
(is (= (str->amount "0..")
       "0."))

(is (= (str->amount "0,.")
       "0,"))

;; can't be more than 15 chars
(is (= (str->amount (apply str (repeat 16 1)))
       (apply str (repeat 15 1))))

