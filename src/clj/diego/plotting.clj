(ns diego.plotting
  (:require [clojure.string :as s]
            [clojure.set :as cset]
            [clojure.tools.logging :as log]
            [diego.geoip :as geoip]))

(def ips-by-hour (atom {}))

(defn parse-line [line]
  (let [[_ ip timestamp] (s/split line #"\s+")]
    (if (and (not (nil? ip))
             (re-matches #"\A[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\z" ip)
             (re-matches #"\A[0-9]+\z" timestamp))
      [ip (read-string timestamp)]
      [nil nil])))

(defn beginning-of-hour [timestamp]
  (- timestamp (mod timestamp 3600)))

(defn store-location [locations ip timestamp]
  (update-in locations [(beginning-of-hour timestamp)] cset/union #{ip}))

(defn store! [line]
  (try
    (log/info "storing line")
    (let [[ip timestamp] (parse-line line)]
      (log/info (str "adding ip: " ip))
      (if (nil? ip)
        (log/warn "dropping invalid input " line)
        (do
          (swap! ips-by-hour store-location ip timestamp)
          (log/info "added ip: " ip))))
    (catch Exception e (log/error "caught execption " e))))

(defn ip->point [db ip]
  (let [location (geoip/lookup-ip db ip)]
    {:type "Feature"
     :properties {}
     :geometry {:type "Point"
                :coordinates [(:longitude location) (:latitude location)]}}))

(defn ips->geo-json [db ips]
  {:type "FeatureCollection"
   :features (map (partial ip->point db) ips)})

(defn geo-json []
  (ips->geo-json @geoip/db (reduce cset/union (vals @ips-by-hour))))
