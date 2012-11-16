(ns diego.data-socket
  (:require [lamina.core :as lamina]
            [gloss.core :as gloss]
            [aleph.tcp :as tcp]))

(def each-line-frame (gloss/string :utf-8 :delimiters ["\n"]))

(defn start [line-handler]
  (tcp/start-tcp-server
    (fn [ch client-info] (lamina/receive-all ch line-handler))
    {:port 10000, :frame each-line-frame}))
