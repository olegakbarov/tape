(ns app.constants.domain
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [cljs.spec.alpha :as s]))

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

(s/valid? ::curr-symbols)

(comment (s/exercise boolean?) (s/exercise string?) (s/exercise int?))

; (s/fdef add
;   {:args []
;    :ret number?
;    :fn})

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
