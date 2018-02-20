(ns app.constants.domain
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as ts]))

(s/def ::fiat-symbols #{:RUB :USD})

(s/def ::curr-symbols
  #{:LEO :ADK :LRC :MAID :EOS :RISE :BNB :MIOTA :GAME :SC :TNT :XRP :MCAP :BAT
    :PART :XEL :STORJ :XMR :RUB :MCO :GBYTE :GNO :PLR :DCT :STRAT :RLC :DCR :FCT
    :PIVX :HSR :BLOCK :WINGS :XLM :BCH :WTC :DGB :MLN :DASH :DOGE :PPC :MGO
    :USDT :ZEC :STEEM :QTUM :BCN :ANT :REP :IOC :DGD :CVC :BTCD :TRIG :XAS :VTC
    :GNT :PPT :FRST :NEO :BQX :ETC :OMG :ETH :BTS :KMD :ADX :NXT :VERI :NLC2
    :WAVES :SNGLS :NLG :NXS :GAS :RDD :MTL :BTC :NAV :SYS :BNT :LSK :XVG :FUN
    :ICN :LKK :GXS :XEM :DNT :MTH :USD :CFI :ARK :BCC :ARDR :SNT :EMC :LTC :ZRX
    :UBQ :TKN :EDG :PAY})

(def symbs
  #{:LEO :ADK :LRC :MAID :EOS :RISE :BNB :MIOTA :GAME :SC :TNT :XRP :MCAP :BAT
    :PART :XEL :STORJ :XMR :RUB :MCO :GBYTE :GNO :PLR :DCT :STRAT :RLC :DCR :FCT
    :PIVX :HSR :BLOCK :WINGS :XLM :BCH :WTC :DGB :MLN :DASH :DOGE :PPC :MGO
    :USDT :ZEC :STEEM :QTUM :BCN :ANT :REP :IOC :DGD :CVC :BTCD :TRIG :XAS :VTC
    :GNT :PPT :FRST :NEO :BQX :ETC :OMG :ETH :BTS :KMD :ADX :NXT :VERI :NLC2
    :WAVES :SNGLS :NLG :NXS :GAS :RDD :MTL :BTC :NAV :SYS :BNT :LSK :XVG :FUN
    :ICN :LKK :GXS :XEM :DNT :MTH :USD :CFI :ARK :BCC :ARDR :SNT :EMC :LTC :ZRX
    :UBQ :TKN :EDG :PAY})


(take 5 (s/exercise ::curr-symbols))

(comment (s/exercise boolean?) (s/exercise string?) (s/exercise int?))

(gen/sample (s/gen ::curr-symbols))

(s/def ::btc-price
  (s/and #(> % 3000)
         #(< % 5100)
         int?))

(s/def ::big-even
  (s/and int?
         even?
         #(> % 1000)))

(gen/sample (s/gen ::btc-price))
(gen/sample (s/gen ::big-even))
(gen/sample gen/char)


;; from https://groups.google.com/forum/#!topic/clojure/fti0eJdPQJ8
(defmacro only-keys
  [&
   {:keys [req req-un opt opt-un]
    :as args}]
  `(s/merge (s/keys ~@(apply concat (vec args)))
            (s/map-of ~(set (concat req
                                    (map (comp keyword name) req-un)
                                    opt
                                    (map (comp keyword name) opt-un)))
                      any?)))

(s/def ::alert-id (s/and string? #(> (count %) 10)))

(s/def ::alert (only-keys :opt-un [::alert-id]))
; :market
; :pair
; :amount
; :archived
; :repeat]))

(gen/sample (s/gen ::alert))
;; org.mozilla.javascript.JavaScriptException: Error: Unable to construct gen
;; at: [] for: :app.constants.domain/alert
;; (.cljs_rhino_repl/goog/../cljs/spec/alpha.js


(s/valid? {:id "df"} ::alert)
;; false

(s/describe ::alert)

(s/exercise ::alert-id)


;; fdef
;;
(ts/instrument)

(defn add [a b] (+ a b))

(s/fdef add
 :args (s/cat :a number?
              :b number?)
 :ret number?)

(add 1 "a")
