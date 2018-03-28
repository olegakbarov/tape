(def project
  {:name "cryptounicorns"
   :version "0.2.0"})

(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies
          '[[org.clojure/clojure "1.9.0"]
            [org.clojure/clojurescript "1.9.946"]
            [org.clojure/tools.nrepl "0.2.13" :scope "test"]
            [org.clojure/tools.namespace "0.3.0-alpha1" :scope "test"]
            [org.clojure/test.check "0.9.0" :scope "test"]
            [com.cemerick/piggieback "0.2.2" :scope "test"]
            [weasel "0.7.0" :scope "test"]
            [adzerk/boot-cljs "2.1.4" :scope "test"]
            [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
            [adzerk/boot-reload "0.5.2" :scope "test"]
            [boot-deps "0.1.9" :scope "test"]
            [boot-fmt/boot-fmt "0.1.8" :scope "test"]
            [powerlaces/boot-figreload "0.5.14" :scope "test"]
            [binaryage/devtools "0.9.9" :scope "test"]
            [binaryage/dirac "1.2.29" :scope "test"]
            [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]
            [tolitius/boot-check "0.1.6" :scope "test"]
            [pandeiro/boot-http "0.7.6" :scope "test"]
            ;
            [klang "0.5.13" :scope "test"]
            [cljs-http "0.1.44"]
            [compact-uuids "0.2.0"]
            [mount "0.1.12"]
            [com.andrewmcveigh/cljs-time "0.5.2"]
            [reagent "0.8.0-alpha2" :exclusions [cljsjs.react]]
            [adzerk/env "0.4.0"]
            [cljsjs/react-select "1.0.0-rc.10-1"]
            [cljsjs/react "16.0.0-0"]
            [cljsjs/react-dom "16.0.0-0"]
            [cljsjs/moment "2.10.6-0"]
            [cljsjs/react-virtualized "9.18.5-1" :exclusions [cljsjs.react]]
            [cljsjs/react-motion "0.5.0-0"]
            [cljsjs/chartjs "2.6.0-0"]
            [cljsjs/highstock "5.0.14-0"]
            [cljsjs/highcharts-css "5.0.10-0"]
            [cljsjs/dygraph "2.1.0-0"]
            [cljsjs/raven "3.22.1-0"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[powerlaces.boot-figreload :refer [reload]]
         '[powerlaces.boot-cljs-devtools :refer [cljs-devtools dirac]]
         '[tolitius.boot-check :as check]
         '[boot-fmt.core :refer [fmt]])
; '[pandeiro.boot-http :refer [serve]])

; (task-options! repl {:middleware '[cemerick.piggieback/wrap-cljs-repl]})

(deftask prod-build
         []
         (comp (cljs :ids #{"main"} :optimizations :simple)
               (cljs :ids #{"renderer"} :optimizations :simple)))

(deftask check-sources
         []
         (set-env! :source-paths #{"src"})
         (comp (check/with-yagni)
               (check/with-eastwood)
               (check/with-kibit)
               (check/with-bikeshed)))

(deftask
 dev-build
 []
 (set-env! :source-paths #(conj % "dev"))
 (comp ;(serve)
       (speak :theme "ordinance")
       (cljs-devtools)
       (dirac)
       (cljs-repl)
       (reload :ids #{"renderer"} :ws-host "localhost" :target-path "target")
       (cljs :ids #{"renderer" "mount"}
             :compiler-options {:parallel-build true})
       ;; path.resolve(".") which is used in CLJS's node shim
       ;; returns the directory `electron` was invoked in and
       ;; not the directory our main.js file is in.
       ;; Because of this we need to override the compilers `:asset-path
       ;; option`
       ;; See http://dev.clojure.org/jira/browse/CLJS-1444 for details.
       (cljs :ids #{"main"}
             :compiler-options {:asset-path "target/main.out"
                                :closure-defines {'app.main/dev? true}
                                :parallel-build true
                                :external-config
                                {:devtools/config
                                 {:features-to-install [:formatters :hints]
                                  :fn-symbol "λ"
                                  :print-config-overrides true}}})
       (target)))
