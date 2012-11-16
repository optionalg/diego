(ns diego.core
  (:require [diego.geoip :as geoip]
            [diego.http :as http]
            [diego.data-socket :as data-socket]))

(defn -main [& args]
  (let [geoip-db (geoip/build-database "GeoLiteCity.dat")]
    (prn (geoip/lookup-ip geoip-db "98.101.136.2")))
  (http/start)
  (data-socket/start prn))
