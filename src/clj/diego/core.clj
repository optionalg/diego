(ns diego.core
  (:require [diego.geoip :as geoip]
            [diego.index :as index]
            [diego.data-socket :as data-socket]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [aleph.http :as aleph]))

(compojure/defroutes main-routes
  (compojure/GET "/" [] (index/page))
  (route/resources "/"))

(defn -main [& args]
  (let [geoip-db (geoip/build-database "GeoLiteCity.dat")]
    (prn (geoip/lookup-ip geoip-db "98.101.136.2")))
  (data-socket/start prn)
  (aleph/start-http-server (aleph/wrap-ring-handler main-routes) {:port 8081 :websocket true}))
