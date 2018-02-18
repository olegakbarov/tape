(ns app.macros)

(defmacro profile
  [k & body]
  `(let [k# ~k]
     (.time js/console k#)
     (let [res# (do ~@body)]
       (.timeEnd js/console k#)
       res#)))

(defmacro with-preserved-ctx
  [ctx & body]
  `(let [ctx# ~ctx]
     (.save ctx#)
     (let [rc# (do ~@body)]
       (.restore ctx#)
       rc#)))

(defmacro unless [condition & body] `(when (not ~condition) ~@body))
