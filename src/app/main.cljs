(ns app.main)

(def electron      (js/require "electron"))
(def app           (.-app electron))
(def BrowserWindow (.-BrowserWindow electron))
(def Tray (.-Tray electron))
(def ipc (.-ipcMain electron))

(goog-define dev? false)
(def log (.-log js/console))

(def window (atom nil))
(def tray (atom nil))

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

;; Math.round(trayBounds.x + (trayBounds.width / 2) - (windowBounds.width / 2))

(defn get-window-position []
  (let [window-bounds (.getBounds @window)
        tray-bounds (.getBounds @tray)
        x (.round js/Math (- (+ (.-x tray-bounds)
                                (/ (.-width tray-bounds) 2))
                             (/ (.-x window-bounds) 2)))
        y (.round js/Math (apply + [(.-y tray-bounds) (.-height tray-bounds) 4]))]
    [x y]))

(defn show-window []
  (let [[x y] (get-window-position)]
    (.setPosition @window x y false)
    (.show @window)
    (.focus @window)))

(defn toggle-window []
  (if (.isVisible @window)
    #(.hide @window)
    show-window))

(def path (js/require "path"))

(defn make-window []
  (BrowserWindow. #js {:width 250
                       :height 400
                       :show true
                       :frame false
                       :fullscreenable false
                       :resizable true
                       :transparent false}))

(defn init-tray-icon []
  (let [p (.join path js/__dirname "../../../resources/assets/flagTemplate.png")]
    (reset! tray (Tray. p))
    (do
      (.on @tray "right-click" toggle-window)
      (.on @tray "double-click" toggle-window)
      (.on @tray "click" toggle-window))))

(defn init-browser []
  (reset! window (make-window))
  (do
    (load-page @window)
    (when dev?
        (.openDevTools @window #js {:mode "undocked"}))
    (.on @window "closed" #(reset! window nil))))

(defn init []
  (.hide (.-dock app))
  (.on app "window-all-closed" #(when-not (= js/process.platform "darwin") (.quit app)))
  (.on app "ready" init-browser)
  (.on app "ready" init-tray-icon)
  (.on ipc "show-window" show-window)
  (set! *main-cli-fn* (fn [] nil)))
