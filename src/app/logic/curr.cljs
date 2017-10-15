(ns app.logic.curr)

(defn best-pairs
  "Returns pairs with lowest prices across "
  [markets]
  (let [market-items (vals markets)]
    (reduce
     (fn [acc item]
      (into {}
        (map
         (fn [item]
          (let [[key val] item
                acc-pair (get acc key)]
            (if-not acc-pair
                (assoc acc key val)
                (if (< (:last val)
                       (:last acc-pair))
                    (assoc acc key val)
                    acc))))
         item)))
     {}
     market-items)))

