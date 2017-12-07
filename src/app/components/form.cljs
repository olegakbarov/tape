(ns app.components.form
  (:require [clojure.string :as s]
            [cljs.spec.alpha :as spec]
            [clojure.string :as s]
            [reagent.core :as r]
            [app.components.ui :refer [Button]]
            [app.logic.validation :refer [str->amount str->item]]))

(defn- on-change
  [e idx store cfgs]
  (let [v
        (-> e
            .-target
            .-value)]))

(defn input-group [])
; (defn input-group
;   "Generic input component. Takes care of validations and focusing DOM nodes
;   in correct order"
;   [cfgs on-submit]
;   (let [store (create-initial-state cfgs)
;         get-ref #(swap! store assoc-in [%2 :node] %1)
;         focused-idx (r/atom 0)
;         submit-ref (r/atom nil)
;         submit-fn (fn [_]
;                     (when (->> @store
;                                (map :value)
;                                (every? #(not (s/blank? %))))
;                       (do (on-submit @store)
;                           (reset! store (map #(merge % {:value ""}) @store))
;                           (reset! focused-idx nil))))]
;     (fn []
;       [:div.form_wrap
;        [:div.add_rec_wrapper
;         (doall
;          (map-indexed
;           (fn [idx cfg]
;             (let [{:keys [node value]} (get @store idx)
;                   {:keys [name get-options-fn placeholder valid-fn]} cfg]
;               ^{:key name}
;               [:div.add_rec_input
;                [:div.label (str name ":")]
;                [:div.field
;                 [:input.input_item
;                  {:type "text"
;                   :autoFocus (= idx 0)
;                   :on-change #(on-change % idx store cfgs)
;                   :ref #(get-ref % idx)
;                   :on-focus #(reset! focused-idx idx)
;                   :on-blur #(reset! focused-idx nil)
;                   :value (:value (get @store idx))}]]
;                (when (and (= idx @focused-idx) (:get-options-fn (get cfgs
;                idx)))
;                  [dropdown idx get-options-fn on-select submit-ref store])]))
;           cfgs))]
;        [Button
;         {:type "submit"
;          :on-click submit-fn
;          :ref #(reset! submit-ref %)
;          :color "#12D823"}
;         "Add"]])))
