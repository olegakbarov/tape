(ns app.db
  (:require [reagent.core :as r]))

(defonce router (r/atom {:screen :live}))

(defonce chart-data (r/atom {}))

(defonce db
         (r/atom {:ui/detailed-view nil
                  :ui/portfolio-add-view false
                  :ui/portfolio-edit-view nil
                  :ui/alerts-add-view false
                  :ui/alerts-edit-view nil
                  :ui/current-filter :bestprice
                  :ui/filter-q ""
                  :ui/filterbox-open? false
                  :ui/ntf nil
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
