(ns app.components.form
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.components.ui :refer [Button]]
            [clojure.string :as s]
            [cljs.spec.alpha :as spec]))

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
        on-change (fn [e idx]
                    (let [v (-> e .-target .-value)]
                      (swap! store assoc-in [idx :value] (str->amount v))))
        on-select-opt #(do (swap! store assoc-in [%2 :value] %1)
                           (let [next-node (:node (get @store (inc %2)))]
                              (if next-node (.focus next-node)
                                            (.focus @submit-ref))))]
    (fn []
     [:div
      [:div.add_rec_wrapper
       (doall
        (map-indexed
          (fn [idx cfg]
            (let [{:keys [node value]} (get @store idx)
                  {:keys [name options placeholder validation-type]} cfg]
              ^{:key name}
              [:div.add_rec_input
               [:div
                [:input.input_item
                 {:type "text"
                  :placeholder placeholder
                  :autoFocus (= idx 0)
                  :on-change #(on-change % idx)
                  :ref #(get-ref % idx)
                  :on-focus #(reset! focused-idx idx)
                  :on-blur #(reset! focused-idx nil)
                  :value (:value (get @store idx))}]]]))
               ; [dropdown idx options node on-select-opt]]))
         cfgs))]
      [:div {:style {:padding "0 10px"}}
        [Button
          {:type "submit"
           :on-click #(js/console.log @store)
           :ref #(reset! submit-ref %)
           :color "#12D823"
           :text "Add"}]]])))

