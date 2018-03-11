(ns app.actions.alerts
  (:require [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as ts]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [app.db :refer [db]]
            [app.actions.storage :refer [persist-user-currents!]]
            [app.utils.core :refer [generate-uuid]]
            [app.config :refer [config]]
            [klang.core :refer-macros [info! erro!]]))

(when (= :dev (:env config))
  (do (info! "Spec validation activated...")
      (ts/instrument)))

(s/fdef render-alert!
  :args (s/cat :k keyword? :v string?))

(defn render-alert!
  [title text]
  (js/Notification. title (clj->js {:body text})))

(s/def ::market keyword?)
(s/def ::pair keyword?)
(s/def ::amount string?)
(s/def ::id (s/and string? #(= (count %) 26)))
(s/def ::archived boolean?)

(s/def ::alert
  ;; required and optional unnamespaced keys
  (s/keys :req-un [::market ::pair ::amount ::archived]
          :opt-un [::id]))

(s/valid? ::alert
  {:market :bitfinex
   :pair :XRP-USD
   :amount "1.33"
   :id "bhrnck4b6ekv9b2k0krj4nj027"
   :archived false})

; TODO: this accepts alert from ui, not keywordized
; (s/fdef create-alert-record
;   :args (s/cat :a ::alert))

(defn create-alert-record
  "Adds notif to state and persists it to disk"
  [a]
  (let [id (generate-uuid)]
    (do (swap! db assoc-in
          [:user :alerts id]
          (merge a
                 {:id id
                  :archived false
                  :market (-> a
                              :market
                              keyword)
                  :pair (-> a
                            :pair
                            keyword)}))
        (persist-user-currents!))))

(s/fdef update-alert-record
  :args (s/cat :updated ::alert))

(defn update-alert-record
  [updated]
  (let [{:keys [id]} updated]
    (swap! db update-in [:user :alerts id] merge updated)))

(s/fdef remove-alert-record
  :args (s/cat :id ::id))

(defn remove-alert-record
  [id]
  (do (swap! db update-in [:user :alerts] dissoc id)
      (persist-user-currents!)))

(s/fdef alert->archived
  :args (s/cat :id ::id))

(defn alert->archived
  [id]
  (do (swap! db update-in [:user :alerts id] #(merge % {:archived true}))
      (persist-user-currents!)))
