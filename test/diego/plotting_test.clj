(ns diego.plotting-test
  (:use clojure.test diego.plotting)
  (:require [diego.geoip :as geoip]))

(deftest can-parse-line
  (testing "parses key, ip, and timestamp"
    (let [[ip timestamp] (parse-line "the.key 127.0.0.1 1353073467")]
      (is (= "127.0.0.1" ip))
      (is (= 1353073467 timestamp))))
  (testing "parses with multiple spaces"
    (let [[ip timestamp] (parse-line "the.key   127.0.0.1     1353073467")]
      (is (= "127.0.0.1" ip))
      (is (= 1353073467 timestamp))))
  (testing "junk results in nils"
    (let [[ip timestamp] (parse-line "mary had a little lamb")]
      (is (= nil ip))
      (is (= nil timestamp))))
  (testing "timestamp needs to be good too"
    (let [[ip timestamp] (parse-line "the.key 127.0.0.1 this is not a date")]
      (is (= nil ip))
      (is (= nil timestamp))))
  (testing "empty lines result in nils"
    (let [[ip timestamp] (parse-line "")]
      (is (= nil ip))
      (is (= nil timestamp)))))

(deftest can-find-beginning-of-hour
  (is (= 1353099600 (beginning-of-hour 1353102243))))

(deftest can-store-data
  (testing "one record"
    (is (= {1353099600 #{"ip"}}
           (store-location {} "ip" 1353102243))))
  (testing "ignores duplicates"
    (is (= {1353099600 #{"ip"}}
           (store-location {1353099600 #{"ip"}} "ip" 1353102243))))
  (testing "multiple records"
    (is (= {1353099600 #{"1" "2"}}
           (store-location {1353099600 #{"1"}} "2" 1353102243))))
  (testing "only keeps 24 hours"
      (let [stored-locations (reduce #(store-location %1 "2" (- 1353102243 (* %2 3600))) {} (range 1 25))]
      (is (= 24 (count (keys stored-locations)))))))

(deftest store-mutates-state
  (testing "accepts good lines"
    (reset! ips-by-hour {})
    (store! "foo 127.0.0.1 1353102243")
    (is (= [#{"127.0.0.1"}] (vals @ips-by-hour))))
  (testing "drops bad lines"
    (reset! ips-by-hour {})
    (store! "")
    (is (= nil (vals @ips-by-hour)))))

(deftest can-generate-geo-json
  (geoip/build-database! "GeoLiteCity.dat")
  (testing "ip->point"
    (let [point (ip->point @geoip/db "98.101.166.2")]
      (is (= {:type "Feature"
              :properties {}
              :geometry {:type "Point"
                         :coordinates [(float -78.8326) (float 35.860107)]}}
             point))))
  (testing "ips->point"
    (let [points (ips->point @geoip/db #{"98.101.166.2"})]
      (is (= "FeatureCollection" (:type points)))
      (is (any? (:features points))))))
