(ns diego.plotting
  (:require [clojure.string :as s]
            [clojure.set :as cset]
            [clojure.tools.logging :as log]
            [contessa.core :as geoip]))

(def geoip-db (atom nil))

(def long-lats-by-hour (atom {}))

(def ^:dynamic *hours-to-keep* 24)

(defn build-geoip-database! [file]
  (reset! geoip-db (geoip/build-database file)))

(defn parse-line [line]
  (let [[_ ip timestamp] (s/split line #"\s+")]
    (if (and (not (nil? ip))
             (re-matches #"\A[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\z" ip)
             (re-matches #"\A[0-9]+\z" timestamp))
      (let [location (geoip/lookup-ip @geoip-db ip)]
        (if (= {} location)
          [[0 0] (read-string timestamp)]
          [(map location [:longitude :latitude]) (read-string timestamp)]))
      [[0 0] nil])))

(defn beginning-of-hour [timestamp]
  (- timestamp (mod timestamp 3600)))

(defn store-location [locations long-lat timestamp]
  (let [new-locations (update-in locations [(beginning-of-hour timestamp)] cset/union #{long-lat})
        ks (keys new-locations)]
    (if (> (count ks) *hours-to-keep*)
      (dissoc new-locations (first (sort ks)))
      new-locations)))

(defn store! [line]
  (try
    (log/trace "storing line")
    (let [[long-lat timestamp] (parse-line line)]
      (log/debug (str "adding location: " long-lat))
      (if (= [0 0] long-lat)
        (log/warn "dropping invalid input " line)
        (do
          (swap! long-lats-by-hour store-location long-lat timestamp)
          (log/debug "added location: " long-lat))))
    (catch Exception e (log/error "caught execption " e))))

(defn long-lat->point [long-lat]
  {:type "Feature"
   :properties {}
   :geometry {:type "Point"
              :coordinates long-lat }})

(defn long-lats->geo-json [long-lats]
  {:type "FeatureCollection"
   :features (map long-lat->point long-lats)})

(defn geo-json []
  (long-lats->geo-json (reduce cset/union (vals @long-lats-by-hour))))
