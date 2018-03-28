(ns app.actions.settings)

(def electron (js/require "electron"))
(def ipc (.-ipcRenderer electron))

(defn open-in-browser [link] (.send ipc "open-external" link))
