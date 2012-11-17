(ns diego.plotting
  (:require [clojure.string :as s]
            [clojure.set :as cset]))

(def locations-by-hour (atom {}))

(defn parse-line [line]
  (let [[_ ip timestamp] (s/split line #"\s+")]
    [ip timestamp]))

(defn beginning-of-hour [timestamp]
  (- timestamp (mod timestamp 3600)))

(defn store-location [locations ip timestamp]
  (update-in locations [(beginning-of-hour timestamp)] cset/union #{ip}))

(defn store [ip timestamp]
  (swap! locations-by-hour store-location ip timestamp))

(defn locations []
  (reduce cset/union (vals @locations-by-hour)))
