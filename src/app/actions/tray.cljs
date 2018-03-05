(ns app.actions.tray)

(def electron (js/require "electron"))
(def ipc (.-ipcRenderer electron))

(defn set-title!
  [text]
  (let [fmtd (str " $" text)]
    (.send ipc "set-title" fmtd)))
