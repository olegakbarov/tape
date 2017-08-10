#!/bin/bash

for ((COUNT = 1; COUNT <= 3000; COUNT++)); do
  echo '{"High":45.000001,"Low":41.35,"Avg":43.175001,"Vol":13157.821,"VolCur":302.6894,"Last":44.7,"Buy":44.749501,"Sell":44.978,"Timestamp":1501939951,"CurrencyPair":"LTC-USD","Market":"yobit"}'
  sleep 2
done
