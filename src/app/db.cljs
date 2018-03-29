(ns app.db
  (:require [reagent.core :as r]))

(defonce router (r/atom {:screen :live}))

(defonce chart-data (r/atom {}))

(defonce db
         (r/atom {:ui/fetching-init-data? true ;; hack
                  :ui/current-filter :market
                  :ui/market-filter :bitfinex
                  :ui/filter-q ""
                  :ui/filterbox-open? false
                  :ui/ntf nil
                  :ui/detailed-view nil
                  :ui/window-size [320 720]
                  :ui/folio-add false
                  :ui/folio-add-height nil ;; !!
                  :ui/folio-edit nil
                  :ui/folio-edit-height nil ;; !!
                  :ui/alert-add false
                  :ui/alert-add-height nil ;; !!
                  :ui/alert-edit nil
                  :ui/alert-edit-height nil ;; !!
                  :ui/alerts-add-view false
                  :ui/alerts-edit-view nil
                  :ui/current-graph nil
                  :form/alerts {:market ""
                                :pair ""
                                :amount ""
                                :repeat false}
                  :form/portfolio {:market ""
                                   :currency ""
                                   :amount ""}
                  :user {:portfolio {}
                         :favorites {}
                         :alerts {}
                         :settings {}}
                  :markets {}}))
