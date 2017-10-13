(ns app.components.form
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [goog.functions]
            [app.components.ui :refer [Button]]
            [clojure.string :as s]
            [cljs.spec.alpha :as spec]
            [clojure.test :refer [is]]))

(defn dropdown
  [idx options node handler]
  (fn []
    (doall
      [:div.dropdown
        (for [opt options]
          ^{:key opt}
          [:div.pill
           {:on-click #(handler opt idx)}
           opt])])))

(def seps #{"." ","})

(defn valid-chars? [x]
       (apply str (filter #(re-matches #"^[0-9.,]" %) x)))

(defn length-ok? [v]
  (if (re-matches #"^[0-9.,]{1,15}$" v)
      v
      (subs v 0 15)))

(defn has-sep? [v]
  (let [[xs x] [(apply str (butlast v)) (last v)]]
    (if (and (some seps x)
             (some seps xs))
        xs
        v)))

(defn str->amount
  "Validates the input string to acceptable currency form"
  [v]
  (if (= v "00")
    "0"
    (if (and (= (count v) 1)
             (some seps v))
        (str 0 (valid-chars? v))
        (if (and (= (count v) 1)
                 (valid-chars? v))
           v
           (-> v
               valid-chars?
               length-ok?
               has-sep?)))))

(defn coll-suggest
  "Returns only elements of collection that starts with q"
  [coll q]
  (filter
   #(clojure.string/starts-with? % q)
   coll))

(defn update-field-with-opts [opts v]
  (let [valid-opts (filter #(s/starts-with? % v) opts)]
    valid-opts v))

(defn input-group
  "Generic input component. Takes care of validations and focusing DOM nodes in order"
  [cfgs]
  (let [store (r/atom (vec (replicate (count cfgs) {:node nil :value "" :valid false})))
        get-ref #(swap! store assoc-in [%2 :node] %1)
        focused-idx (r/atom 0)
        submit-ref (r/atom nil)
        on-change (fn [e idx validation-type]
                    (let [v (-> e .-target .-value)]
                      ;; TODO: different validations
                      (if validation-type
                       (when (str->amount v) (swap! store assoc-in [idx :value] v))
                       (swap! store assoc-in [idx :value] v))))
        ;; TODO: for some reason works 80% of the time
        on-select-opt #(do (swap! store assoc-in [%2 :value] %1)
                           (let [next-node (:node (get @store (inc %2)))]
                             (goog.functions.debounce
                              (if next-node (.focus next-node)
                                            (.focus @submit-ref))
                              10)))]
    (fn []
      [:div
       (doall
        (map-indexed
          (fn [idx cfg]
            (let [{:keys [node value]} (get @store idx)
                  {:keys [name options placeholder validation-type]} cfg]
              ^{:key name}
              [:div.add_rec_wrapper
               [:div
                [:input.input_item
                 {:type "text"
                  :placeholder placeholder
                  :autoFocus (= idx 0)
                  :on-change #(on-change % idx validation-type)
                  :ref #(get-ref % idx)
                  :on-focus #(reset! focused-idx idx)
                  :on-blur #(reset! focused-idx nil)
                  :value (:value (get @store idx))}]]
               (when (and (= idx @focused-idx)
                          (not (nil? options)))
                [dropdown idx options node on-select-opt])]))
         cfgs))
       [:div {:style {:padding "0 10px"}}
         [Button
           {:type "submit"
            :ref #(reset! submit-ref %)
            :on-click #(js/console.log @store)
            :color "#12D823"}
           "Add"]]])))

