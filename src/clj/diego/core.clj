(ns diego.core
  (:use [clojure.tools.cli :only [cli]])
  (:require [diego.geoip :as geoip]
            [diego.plotting :as plotting]
            [diego.http :as http]
            [diego.data-socket :as data-socket]))

(def geoip-db)

(defn -main [& args]
  (let [[options [geoip-file] banner] (cli args
                                   ["-p" "--http-port" "HTTP port to listen upon" :parse-fn #(Integer. %) :default 8081]
                                   ["-t" "--tcp-port" "TCP data port to listen upon" :parse-fn #(Integer. %) :default 10000])]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (when (nil? geoip-file)
      (println "Usage: diego [options] GEOIP_FILE")
      (println banner)
      (System/exit 1))
    (geoip/build-database! geoip-file)
    (http/start (:http-port options))
    (data-socket/start plotting/store! (:tcp-port options))))
