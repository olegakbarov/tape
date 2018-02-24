(ns app.screens.live
  (:require-macros [app.macros :refer [profile]]
                   [klang.core :refer [info! warn! erro! crit! fata! trac!]])
  (:require [reagent.core :as r]
            [clojure.string :as s]
            [goog.object :as gobj]
            [cljsjs.moment]
            [cljsjs.react-select]
            [app.db :refer [db]]
            [app.motion :refer [Motion spring presets]]
            [app.utils.core :refer [curr-symbol->name]]
            [app.components.ui :as ui]
            [app.components.header :refer [Header]]
            [app.components.chart :refer [Chart]]
            [app.actions.ui :refer [add-to-favs
                                    remove-from-favs
                                    close-detailed-view]]
            [app.logic.ui :refer [get-chart-points]]
            [app.logic.curr :refer [best-pairs
                                    all-pairs
                                    user-favs
                                    by-query
                                    pairs-by-query]]
            [app.actions.ui :refer [toggle-filter
                                    update-filter-q
                                    open-detailed-view
                                    toggle-filterbox]]))

(defn render-row
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
         (str  (.toFixed percent 5) "% "  (.toFixed amount 5))
         "n/a")]]]))

(defn row
 [pair]
 (r/create-class
  {:reagent-render #(render-row pair)
   ; :component-did-update   update-comp
   ; :component-did-mount    update-comp
   :should-component-update
    (fn [this]
     (js/console.log "next-props" (r/props this)))}))

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
          [dt-m dt-p] (-> @db
                          :ui/detailed-view)
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
            [row pair]]))])))

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

(defn toggle
  []
  (let [open? (-> @db
                  :ui/filterbox-open?)
        q (-> @db
              :ui/filter-q)
        f (:ui/current-filter @db)]
    [:div.filterbox-toggle {:on-click toggle-filterbox}
     (when (not open?) [:div.input_label "Filters applied:"])
     (when (and (not open?) (> (count q) 0))
       [:div.pill.query (str "Query: " q)])
     (when (and f (not open?)) [:div.pill.filter (name f)])
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
       (when (-> @db
                 :ui/filterbox-open?)
         [:div
          [ui/text-input
           {:on-change on-change
            :value #(-> @db
                        :ui/filter-q)
            :label "search"}]
          [ui/input-wrap "Filter" [select-q {:key "filter"}]]])
       [toggle]])))

;; -- Detailed view

(comment {:high 3143.5286
          :sell 3119.8
          :buy 3081.6715
          :vol-cur 98.522881
          :low 3048.4535
          :avg 3095.991
          :market "yobit"
          :timestamp 1509279292
          :currency-pair "LTC-RUB"
          :last 3070
          :vol 304628.34})

(defn fav?
  [favs tupl]
  (reduce (fn [acc pair]
            (if (and (= (first pair) (first tupl)) (= (last pair) (last tupl)))
              true
              acc))
          false
          favs))

(defn pair-detailed
  []
  (let [[market pair] (:ui/detailed-view @db)
        favs (-> @db
                 :user
                 :favorites)
        content (get-in @db [:markets market pair])
        {:keys [high
                low
                sell
                buy
                currency-pair
                market
                timestamp
                avg
                last
                vol
                vol-cur]}
        content
        is-fav? (fav? favs [market pair])
        points @(r/track get-chart-points market pair)]
    (when (:ui/detailed-view @db)
      [:div#detailed
       [:div.header
        [:div.title
         pair
         [:div.fav
          {:class (if is-fav? "faved" "")
           :on-click (if is-fav?
                       #(remove-from-favs [(keyword market) (keyword pair)])
                       #(add-to-favs [(keyword market) (keyword pair)]))}
          (if is-fav? "saved" "save")]]
        [:div.close {:on-click #(close-detailed-view)}]]
       [:div.market " " market]
       [:div.labels
        (for [i ["High" "Low" "Buy" "Sell"]] ^{:key i} [:div.item i])]
       [:div.prices.last
        (for [i [high low buy sell]]
          ^{:key (* 1000 (.random js/Math i))} ;; nothing to be proud about here
          [:div.item (js/parseFloat i)])]
       (when points [Chart points])])))

(def height 400)

(defn view
  [{c :children}]
  (let [y (gobj/get c "y")]
    [:div
     {:style {:position "fixed"
              :width "321px"
              :height (str height "px")
              :background-color "#fff"
              :z-index 999
              :border-radius "4px 4px 0 0"
              :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)"
              :-webkit-transform (str "translateY(" y "px)")
              :transform (str "translateY(" y "px)")}}
     [pair-detailed]]))

(def animated-comp (r/reactify-component view))

(defn detailed-view
  []
  (fn []
    [:div
      {:style {:position "absolute"
               :bottom 0
               :display (if (:ui/detailed-view @db) "block" "none")}}
      [Motion
       {:style {:y (spring (if (:ui/detailed-view @db) (- height) 0))}}
       (fn [x] (r/create-element animated-comp #js {} x))]]))

(defn live-board
  []
  [:div
   [Header]
   [:div#wrapper
    [filter-box]
    [render-rows]]
   [detailed-view]])
