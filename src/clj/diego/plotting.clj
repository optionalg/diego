(ns diego.plotting
  (:require [clojure.string :as s]
            [clojure.set :as cset]
            [diego.geoip :as geoip]))

(def ips-by-hour (atom {}))

(defn parse-line [line]
  (let [[_ ip timestamp] (s/split line #"\s+")]
    (if-not (nil? ip)
      [ip (read-string timestamp)]
      [nil nil])))

(defn beginning-of-hour [timestamp]
  (- timestamp (mod timestamp 3600)))

(defn store-location [locations ip timestamp]
  (update-in locations [(beginning-of-hour timestamp)] cset/union #{ip}))

(defn store [line]
  (let [[ip timestamp] (parse-line line)]
    (swap! ips-by-hour store-location ip timestamp)))

(defn locations []
  (reduce cset/union (vals @ips-by-hour)))

(defn ip->point [db ip]
  (let [location (geoip/lookup-ip db ip)]
    {:type "Feature"
     :properties {}
     :geometry {:type "Point"
                :coordinates [(:longitude location) (:latitude location)]}}))

(defn ips->geo-json [db ips]
  {:type "FeatureCollection"
   :features (map ip->point db ips)})
