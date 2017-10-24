(def project
  {:name "cryptounicorns" :version "0.2.0"})

(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :dependencies '[[org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                 [org.clojure/test.check "0.9.0" :scope "test"]
                 [com.cemerick/piggieback "0.2.1" :scope "test"]
                 [weasel "0.7.0" :scope "test"]
                 [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                 [adzerk/boot-reload "0.4.13" :scope "test"]
                 [powerlaces/boot-figreload "LATEST" :scope "test"]
                 [binaryage/devtools "0.9.4" :scope "test"]
                 [binaryage/dirac "1.2.16" :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]
                 ;[metosin.forks/reagent "0.6.1-SNAPSHOT"]
                 [reagent "0.8.0-alpha2" :exclusions [cljsjs/react]]
                 [camel-snake-kebab "0.4.0"]
                 [haslett "0.1.0"]
                 [datascript "0.16.2"]
                 [cljsjs/react "16.0.0-0"]
                 [cljsjs/react-dom "16.0.0-0"]
                 [cljsjs/moment "2.10.6-0"]
                 [cljsjs/react-motion "0.5.0-0"]
                 [org.roman01la/cljss "1.5.5"]])

(require
 '[adzerk.boot-cljs              :refer [cljs]]
 '[adzerk.boot-cljs-repl         :refer [cljs-repl start-repl]]
 '[powerlaces.boot-figreload     :refer [reload]]
 '[powerlaces.boot-cljs-devtools :refer [cljs-devtools dirac]])

(deftask prod-build []
  (comp (cljs :ids #{"main"}
              :optimizations :simple)
        (cljs :ids #{"renderer"}
              :optimizations :advanced)))

(deftask dev-build []
  (comp
    (speak)
    (cljs-devtools)
    (dirac)
    (cljs-repl :ids #{"renderer"})
    (reload    :ids #{"renderer"}
               :ws-host "localhost"
               ; :on-jsload 'app.renderer/init
               :target-path "target")
    (cljs      :ids #{"renderer"})
    ;; path.resolve(".") which is used in CLJS's node shim
    ;; returns the directory `electron` was invoked in and
    ;; not the directory our main.js file is in.
    ;; Because of this we need to override the compilers `:asset-path option`
    ;; See http://dev.clojure.org/jira/browse/CLJS-1444 for details.
    (cljs      :ids #{"main"}
               :compiler-options {:asset-path "target/main.out"
                                  :closure-defines {'app.main/dev? true }})

    (target)))
