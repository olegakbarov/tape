(ns app.screens.live
  (:require-macros [app.macros :refer [profile]]
                   [klang.core :refer [info! warn! erro! crit! fata! trac!]])
  (:require
   [reagent.core :as r]
   [clojure.string :as s]
   [app.db :refer [db]]
   [cljsjs.moment]
   [app.logic.curr
    :refer
    [best-pairs all-pairs user-favs by-query pairs-by-query]]
   [app.utils.core :refer [curr-symbol->name]]
   [clojure.string :refer [split]]
   [app.actions.ui :refer [toggle-filter update-filter-q open-detailed-view]]
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
     [:div.left_cell [:div.title currency-pair] [:div.market market]]
     ^{:key "last-price"}
     [:div.right_cell
      ;; TODO
      [:span {:class "price_down"} last]
      [:div.swing
       (if (and (not (nil? amount)) (not (nil? percent)))
         (str amount " (" percent "%) ")
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
                  :volatile nil
                  nil @(r/track all-pairs markets))
          filtered (pairs-by-query pairs q)]
      [:div
       (for [pair filtered]
         (let [{:keys [market currency-pair]} pair]
           ^{:key (str pair market)}
           [:div
            {:on-click #(when (nil? (:ui/detailed-view @db))
                         (open-detailed-view (keyword market)
                                             (keyword currency-pair)))}
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

(defn filter-box
  []
  (let [q (:ui/filter-q @db)
        f (:ui/current-filter @db)
        on-change #(update-filter-q (-> %
                                        .-target
                                        .-value))]
    (fn [] [:div.form_wrap
            [TextInput
             {:on-change on-change
              :value #(-> @db
                          :ui/filter-q)
              :label "search"}]
            [InputWrapper "Filter" [select-q {:key "filter"}]]])))

(defn live-board [] [:div#wrapper [filter-box] [render-rows]])
