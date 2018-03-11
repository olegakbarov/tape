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


;; from https://groups.google.com/forum/#!topic/clojure/fti0eJdPQJ8
(defmacro only-keys
  [& {:keys [req req-un opt opt-un]
      :as args}]
  `(s/merge (s/keys ~@(apply concat (vec args)))
            (s/map-of ~(set (concat req
                                    (map (comp keyword name) req-un)
                                    opt
                                    (map (comp keyword name) opt-un)))
                      any?)))

