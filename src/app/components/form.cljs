(ns app.components.form
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [goog.functions]))

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

(defn validate-amount [v]
  (let [valid-chars "1234567890,."]
    (if (s/includes? valid-chars v) true false)))

(defn input-group [cfgs]
  (let [store (r/atom (vec (replicate (count cfgs) {:node nil :value "" :valid false})))
        get-ref #(swap! store assoc-in [%2 :node] %1)
        focused-idx (r/atom 0)
        submit-ref (r/atom nil)
        on-change (fn [e idx validation-type]
                    (let [v (-> e .-target .-value)]
                      ;; TODO: different validations
                      (if validation-type
                       (when (validate-amount v) (swap! store assoc-in [idx :value] v))
                       (swap! store assoc-in [idx :value] v))))
        ;; TODO: for some reason works 80% of the time
        on-select-opt #(do (swap! store assoc-in [%2 :value] %1)
                           (let [next-node (:node (get @store (inc %2)))]
                             (goog.functions.debounce
                              (if next-node (.focus next-node)
                                            (.focus @submit-ref))
                              100)))]
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
       [:button
         {:type "submit"
          :ref #(reset! submit-ref %)
          :on-click #(js/console.log @store)}
        [:img.folio_plus
         {:src (str "icons/plus-circle.svg")}]]])))

