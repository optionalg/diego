(ns diego.geoip
  (:require [clojure.tools.logging :as log])
  (:import [com.maxmind.geoip LookupService]))

(def db (atom :not-initialized))

(defn build-database! [file-path]
  (log/info (str "Building GeoIP database from " file-path))
  (swap! db (fn [_] (LookupService. file-path))))

(defn lookup-ip [db ip-address]
  (let [location (.getLocation db ip-address)]
    {:latitude (.latitude location)
     :longitude (.longitude location)}))
