; (ns app.components.detailed_view
;   (:require
;     [reagent.core :as r]
;     [goog.object :as gobj]))

; (def height 355)

; (defn view
;   [args]
;   (let [c (:children args)
;         y (gobj/get c "y")]
;     [:div
;      {:style {:position "fixed"
;               :width "321px"
;               :height (str height "px")
;               :background-color "#fff"
;               :z-index 999
;               :border-radius "4px 4px 0 0"
;               :box-shadow "0px -5px 5px -5px rgba(107,107,107,.4)"
;               :-webkit-transform (str "translateY(" y "px)")
;               :transform (str "translateY(" y "px)")}}
;      [item-add]]))

; (def animated-comp (r/reactify-component view))

; (defn detailed-view
;   []
;   (fn []
;     [:div
;       {:style {:position "absolute"
;                :bottom 0
;                :display (if (:ui/detailed-view @db) "block" "none")}}
;       [Motion
;        {:style {:y (spring (if (:ui/detailed-view @db) (- height) 0))}}
;        (fn [x] (r/create-element animated-comp #js {} x))]])))
