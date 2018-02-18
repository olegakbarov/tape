(ns app.screens.live
  (:require-macros [app.macros :refer [profile]]
                   [klang.core :refer [info! warn! erro! crit! fata! trac!]])
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [app.db :refer [db]]
            [cljsjs.moment]
            [app.logic.curr :refer [best-pairs
                                    all-pairs
                                    user-favs
                                    by-query
                                    pairs-by-query]]
            [app.utils.core :refer [curr-symbol->name]]
            [clojure.string :refer [split]]
            [app.actions.ui :refer [toggle-filter
                                    update-filter-q
                                    open-detailed-view
                                    toggle-filterbox]]
            [app.components.ui :refer [InputWrapper TextInput]]
            [cljsjs.react-select]))

;; TODO: handle updates properly
; (defn Row [pair]
;  (reagent/create-class
;   {:reagent-render         #(render-row pair)
;    :component-did-update   update-comp
;    :component-did-mount    update-comp
;    :should-component-update
;     (fn [this]
;      (println "next-props" (reagent/props this)))}))

(defn keyword<->str
  [v]
  (if (string? v)
    (-> v
        (s/replace " " "")
        .toLowerCase
        keyword)
    (condp = v
      :bestprice "Best price"
      :favorites "Favorites"
      nil (erro! (str "Not a string/keyword " v)))))

(defn Row
  [pair]
  (let [{:keys [market currency-pair last change]} pair
        {:keys [percent amount]} change]
    [:div.row_wrap
     ^{:key "currency-pair"}
     [:div.left_cell
      [:div.title currency-pair]
      [:div.market market]]
     ^{:key "last-price"}
     [:div.right_cell
      [:span {:class "price_down"} last]
      [:div.swing
       (if (and (not (nil? amount)) (not (nil? percent)))
         (str amount " (" percent "%)")
         "n/a")]]]))

(defn render-rows
  []
  (fn []
    (let [markets (:markets @db)
          favs (-> @db
                   :user
                   :favorites)
          q (:ui/filter-q @db)
          pairs (condp = (:ui/current-filter @db)
                  :bestprice @(r/track best-pairs markets)
                  :favorites @(r/track user-favs markets favs)
                  nil @(r/track all-pairs markets))
          [dt-m dt-p] (-> @db :ui/detailed-view)
          filtered (pairs-by-query pairs q)]
      [:div
       (for [pair filtered]
         (let [{:keys [market currency-pair]} pair
               [kw-m kw-p] (mapv keyword [market currency-pair])]
           ^{:key (str pair market)}
           [:div
            {:on-click #(open-detailed-view kw-m kw-p)
             :style {:background-color (if (and (= dt-m kw-m) (= dt-p kw-p))
                                           "rgba(0, 126, 255, 0.04)"
                                           "transparent")}}
            [Row pair]]))])))

(defn select-q
  []
  (let [opts ["Favorites" "Best price"]
        v (-> @db
              :ui/current-filter)
        on-change #(if % (toggle-filter (keyword<->str (aget % "value"))))]
    [:>
     js/window.Select
     {:value (keyword<->str v)
      :onChange on-change
      :options (clj->js (map #(zipmap [:value :label] [% %]) opts))}]))

(defn toggle []
  (let [open? (-> @db :ui/filterbox-open?)
        q (-> @db :ui/filter-q)
        f (:ui/current-filter @db)]
    [:div.filterbox-toggle {:on-click toggle-filterbox}
      (when (not open?)
        [:div.input_label "Filters applied:"])
      (when (and (not open?)
                 (> (count q) 0))
        [:div.pill.query (str "Query: " q)])
      (when (and f (not open?))
        [:div.pill.filter (name f)])
      (if open?
       [:div.open]
       [:div.close])]))

(defn filter-box
  []
  (let [q (:ui/filter-q @db)
        f (:ui/current-filter @db)
        open? (:ui/filterbox-open? @db)
        on-change #(update-filter-q (-> %
                                        .-target
                                        .-value))]
    (fn []
      [:div#filter_box.form_wrap
        (when (-> @db :ui/filterbox-open?)
          [:div
            [TextInput
             {:on-change on-change
              :value #(-> @db
                          :ui/filter-q)
              :label "search"}]
            [InputWrapper "Filter" [select-q {:key "filter"}]]])
        [toggle]])))

(defn live-board []
  [:div#wrapper
   [filter-box]
   [render-rows]])
