(ns app.actions.portfolio
  (:require [app.db :refer [db]]
            [app.actions.storage :refer [persist-user-currents!]]))

(defn add-item [rec]
 (let [id (.toString (random-uuid))]
  (do
   (swap! db assoc-in [:user :portfolio id] (merge rec {:id id}))
   (persist-user-currents!))))

(defn set-editing-item [id]
 (swap! db assoc-in [:ui/portfolio-editing] id))

(defn edit-item [updated]
 (let [{:keys [id]} updated]
  (do
   (swap! db update-in [:user :portfolio id] updated))))

(defn remove-item [id]
  (do
   (swap! db update-in [:user :portfolio] dissoc id)
   (persist-user-currents!)))

