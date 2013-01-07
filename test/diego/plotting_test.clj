(ns diego.plotting-test
  (:use clojure.test diego.plotting))

(deftest can-parse-line
  (build-geoip-database! "GeoLiteCity.dat")
  (testing "parses key, ip, and timestamp"
    (let [[long-lat timestamp] (parse-line "the.key 98.101.166.2 1353073467")]
      (is (= [(float -78.8326) (float 35.860107)] long-lat))
      (is (= 1353073467 timestamp))))
  (testing "parses with multiple spaces"
    (let [[long-lat timestamp] (parse-line "the.key   127.0.0.1     1353073467")]
      (is (= [0 0] long-lat))
      (is (= 1353073467 timestamp))))
  (testing "junk results in nils"
    (let [[long-lat timestamp] (parse-line "mary had a little lamb")]
      (is (= [0 0] long-lat))
      (is (= nil timestamp))))
  (testing "timestamp needs to be good too"
    (let [[long-lat timestamp] (parse-line "the.key 127.0.0.1 this is not a date")]
      (is (= [0 0] long-lat))
      (is (= nil timestamp))))
  (testing "empty lines result in nils"
    (let [[long-lat timestamp] (parse-line "")]
      (is (= [0 0] long-lat))
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
      (let [stored-locations (reduce #(store-location %1 "2" (- 1353102243 (* %2 3600))) {} (range 1 26))]
      (is (= 24 (count (keys stored-locations)))))))

(deftest store-mutates-state
  (build-geoip-database! "GeoLiteCity.dat")
  (testing "accepts good lines"
    (reset! long-lats-by-hour {})
    (store! "foo 98.101.166.2 1353102243")
    (is (= [#{[(float -78.8326) (float 35.860107)]}] (vals @long-lats-by-hour))))
  (testing "drops bad lines"
    (reset! long-lats-by-hour {})
    (store! "")
    (is (= nil (vals @long-lats-by-hour)))))

(deftest can-generate-geo-json
  (testing "long-lat->point"
    (let [point (long-lat->point [10 10])]
      (is (= {:type "Feature"
              :properties {}
              :geometry {:type "Point"
                         :coordinates [10 10]}}
             point))))
  (testing "ips->geo-json"
    (let [points (long-lats->geo-json #{[10 10]})]
      (is (= "FeatureCollection" (:type points)))
      (is (not (empty? (:features points)))))))
