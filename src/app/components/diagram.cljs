(ns app.components.diagram)

(def colors
  ["#27AE60"
   "#657AF3"
   "#EB5757"
   "#F2994A"
   "#BB6BD9"
   "#56CCF2"])

(defn diagram
  "Draws diagram for (count colors) items. When provided data
  length exceeds this number it cuts the 'tail' and mark it as 'other'"
  [title data]
  (let [idx (count colors)
        rst (- (count data) (count colors))
        sdata (sort-by second > data) ;; clojure <3
        pdata (if (> (count sdata) idx)
                  (concat (take idx sdata) [[:other rst idx]]) ;; adding last item as 'other'
                  sdata)
        c (reduce + (map second pdata))]
    [:div.diagram_wrapper
      [:h3 title]
      [:div.diagram
       ;; [x - item name, n - number of occurences, i - index]
        (for [[x n i] pdata]
          [:div {:key x
                 :style {:background-color (get colors i)
                         :width (str (* 100 (/ n c)) "%")}}])]
      [:div.legend
       (for [[x n i] pdata]
        [:div.legend_item {:key x}
         [:div.circle {:style {:background-color (get colors i)}}]
         (str (.toUpperCase (name x)) " " (.toFixed (* 100 (/ n c)) 2)) "%"])]]))
