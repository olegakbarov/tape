(ns app.logic.validation
 (:require [clojure.string :as s]))

(def seps #{"." ","})

(defn valid-chars [x]
 (apply str (filter #(re-matches #"^[0-9.]" %) x)))

(defn valid-length [v]
 (if (re-matches #"^[0-9.]{0,15}$" v)
     v
     (subs v 0 15)))

(defn only-one-dot
  "Filters out every special chars except number and one dot (separator)"
  [v]
  (reduce
   (fn [acc ch]
      (if (and (clojure.string/includes? acc ".")
               (= ch "."))
        acc
        (str acc ch)))
   (s/split v "")))

(defn str->amount
  "Validates the input string to acceptable currency form"
  [v]
  (if (zero? (count (valid-chars v)))
    ""
    (if (= v "00")
      "0"
      (if (and (= (count v) 1) (some seps v))
          (str 0 (valid-length v))
          (if (and (= (count v) 1) (valid-length v))
             v
             (-> v
                 valid-length
                 valid-chars
                 only-one-dot))))))

(defn str->item
 "Accepts string and coll of keywords, filters not starting with string"
 [s coll]
 (let [coll' (map name coll)]
  (filter
   #(clojure.string/starts-with? % s)
   coll')))

