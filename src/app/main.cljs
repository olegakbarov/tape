(ns app.main
  (:require [app.config :refer [config]]))

(def electron (js/require "electron"))
(def path (js/require "path"))

(def app (.-app electron))
(def BrowserWindow (.-BrowserWindow electron))
(def Tray (.-Tray electron))
(def ipc (.-ipcMain electron))

(goog-define dev? true)

(def webframe (.-webFrame electron))
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

(defn get-window-position []
  (let [window-bounds (.getBounds @window)
        tray-bounds (.getBounds @tray)
        x (.round js/Math (- (.-x window-bounds)
                             (+ (/ (get tray-bounds "x") 2))))
        y (.round js/Math (apply + [10 (.-y tray-bounds) (.-height tray-bounds)]))]
    [x y]))

(defn show-window []
  (let [[x y] (get-window-position)]
    (.setPosition @window x y false)
    (.show @window)
    (.focus @window)))

(defn toggle-window []
  (if (.isVisible @window)
    (.hide @window)
    (show-window)))

(defn make-window []
  (BrowserWindow. #js {:x 763
                       :y 10
                       :width 321
                       :height 600
                       :show true
                       :frame false
                       :fullscreenable false
                       :resizable dev?
                       :transparent true}))

(defn set-tray! []
 (let [p (.join path js/__dirname "../../../resources/assets/btc1w.png")]
  (reset! tray (Tray. p))))

(defn set-tray-event-handlers []
  (do
    (.on @tray "double-click" toggle-window)
    (.on @tray "click" toggle-window)))

(defn init-browser []
  (reset! window (make-window))
  (do
    (load-page @window)
    (when dev? (.openDevTools @window #js {:mode "undocked"}))
    (.on @window "closed" #(reset! window nil))))

(defn set-title! [e text]
  (.setTitle @tray text))

(defn disable-resize! []
  (.setVisualZoomLevelLimits webframe 1 1)
  (.setLayoutZoomLevelLimits webframe 0 0))

(defn init []
  (.hide (.-dock app))
  (.on app "window-all-closed" #(when-not (= js/process.platform "darwin")
                                          (.quit app)))
  (.on app "ready" init-browser)
  (.on app "ready" set-tray!)
  (.on app "ready" set-tray-event-handlers)
  (.on ipc "show-window" show-window)
  (.on ipc "set-title" set-title!)
  ;; TODO
  (when-not dev? (disable-resize!))
  (set! *main-cli-fn* (fn [] nil)))

