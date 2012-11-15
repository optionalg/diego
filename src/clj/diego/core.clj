(ns diego.core
  (:require [diego.geoip :as geoip]))

(defn -main [& args]
  (let [geoip-db (geoip/build-database "GeoLiteCity.dat")]
    (prn (geoip/lookup-ip geoip-db "98.101.136.2"))))
