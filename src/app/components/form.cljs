(ns app.components.form
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.components.ui :refer [Button]]
            [app.logic.validation :refer [str->amount
                                          str->item]]
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

(defn coll-suggest
 "Returns only elements of collection that starts with q"
 [coll q]
 (filter
  #(clojure.string/starts-with? % q)
  coll))

(defn update-field-with-opts [opts v]
 (let [valid-opts (filter #(s/starts-with? % v) opts)]
  valid-opts v))

(defn create-initial-state [cfgs]
 (r/atom (vec (replicate (count cfgs) {:node nil :value "" :valid false}))))

(defn input-group
 "Generic input component. Takes care of validations and focusing DOM nodes in order"
 [cfgs on-submit]
 (let [store (create-initial-state cfgs)
       get-ref #(swap! store assoc-in [%2 :node] %1)
       focused-idx (r/atom 0)
       submit-ref (r/atom nil)
       on-change (fn [e idx]
                   (let [v (-> e .-target .-value)
                         validated ((:valid-fn (get cfgs idx))
                                    v)]
                     (swap! store assoc-in [idx :value] validated)))
       on-select-opt #(do (swap! store assoc-in [%2 :value] %1)
                          (let [next-node (:node (get @store (inc %2)))]
                             (if next-node (.focus next-node)
                                           (.focus @submit-ref))))]
  (fn []
   [:div.form_wrap
    [:div.add_rec_wrapper
     (doall
      (map-indexed
       (fn [idx cfg]
        (let [{:keys [node value]} (get @store idx)
              {:keys [name options placeholder valid-fn]} cfg]
          ^{:key name}
          [:div.add_rec_input
           [:div.label (str name ":")]
           [:div.field
            [:input.input_item
             {:type "text"
              :autoFocus (= idx 0)
              :on-change #(on-change % idx)
              :ref #(get-ref % idx)
              :on-focus #(reset! focused-idx idx)
              :on-blur #(reset! focused-idx nil)
              :value (:value (get @store idx))}]]]))
           ; (when (= idx @focused-idx)
           ;   [dropdown idx options node on-select-opt])]))
       cfgs))]
    [Button
     {:type "submit"
      :on-click #(do
                  (on-submit @store)
                  (reset! store (create-initial-state cfgs))
                  (reset! focused-idx nil))
      :ref #(reset! submit-ref %)
      :color "#12D823"}
     "Add"]])))

