(ns diego.geoip
  (:require [clojure.tools.logging :as log])
  (:import [com.maxmind.geoip LookupService]))

(def db (atom :not-initialized))

(defn build-database! [file-path]
  (log/info (str "Building GeoIP database from " file-path))
  (swap! db (fn [_] (LookupService. file-path))))

(defn lookup-ip [db ip-address]
  (if (= db :not-initialized)
    (do (log/warn "BUG! uninitialized GeoIP DB")
        {:latitude 0 :longitude 0})
    (let [location (.getLocation db ip-address)]
      {:latitude (.latitude location)
       :longitude (.longitude location)})))
