(ns app.main)

(def electron      (js/require "electron"))
(def app           (.-app electron))
(def BrowserWindow (.-BrowserWindow electron))
(def Tray (.-Tray electron))

(goog-define dev? false)

(def window (atom nil))
(def tray (atom nil))

(.hide (.-dock app))

(defn load-page
  "When compiling with `:none` the compiled JS that calls .loadURL is
  in a different place than it would be when compiling with optimizations
  that produce a single artifact (`:whitespace, :simple, :advanced`).

  Because of this we need to dispatch the loading based on the used
  optimizations, for this we defined `dev?` above that we can override
  at compile time using the `:clojure-defines` compiler option."
  [window]
  (if dev?
      (.loadURL window (str "file://" js/__dirname "/../../index.html"))
      (.loadURL window (str "file://" js/__dirname "/index.html"))))

(defn get-window-position []
  (let [window-bounds (.getBounds @window)
        tray-bounds (.getBounds @tray)
        x (.round js/Math (+ (- (.-x tray-bounds) (/ (.-width tray-bounds) 2)) (/ window-bounds 2)))
        y (.round js/Math (+ (.-x tray-bounds) (.-height tray-bounds) 4))]
    [x y]))

(defn show-window []
  (let [[x y] (get-window-position)]
    (do
      (.setPosition @window x y false)
      (.show @window)
      (.focus @window))))

(defn toggle-window []
  (.isVisible @window)
    (.hide @window))
    (show-window)

(def path (js/require "path"))

(defn make-window []
  (BrowserWindow. #js {:width 250
                       :height 400
                       :show true
                       :frame false
                       :fullscreenable false
                       :resizable true
                       :transparent true}))

(defn init-tray-icon []
  (reset! tray (Tray (.join path js/__dirname "../../resources/assets/flagTemplate.png"))
    (do
      (.on @tray "right-click" toggle-window)
      (.on @tray "double-click" toggle-window)
      (.on @tray "click" toggle-window (fn [e])))))

(defn init-browser []
  (reset! window (make-window))
  (load-page @window)
  (if dev? (.openDevTools @window))
  (.on @window "closed" #(reset! window nil)))

(defn init []
  (.on app "window-all-closed" #(when-not (= js/process.platform "darwin") (.quit app)))
  (.on app "ready"
    (do
      init-browser
      init-tray-icon))
  (set! *main-cli-fn* (fn [] nil)))
