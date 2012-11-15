(ns diego.geoip
  (:import [com.maxmind.geoip LookupService]))

(defn build-database [file-path]
  (LookupService. file-path))

(defn lookup-ip [db ip-address]
  (let [location (.getLocation db ip-address)]
    {:latitude (.latitude location)
     :longitude (.longitude location)}))
