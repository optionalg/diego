(ns diego.geoip-test
  (:use clojure.test diego.geoip))

(deftest lookup-finds-information-for-ip
  (build-database! "GeoLiteCity.dat")
  (testing "can find lat/log"
    (let [{:keys [latitude longitude]} (lookup-ip @db "98.101.166.2")]
     (is (= (float 35.860107) latitude))
     (is (= (float -78.8326) longitude))))
  (testing "returns [0,0] if the location is not found"
    (let [{:keys [latitude longitude]} (lookup-ip @db "127.0.0.1")]
      (is (= [0 0] [latitude longitude]))))
  (testing "returns [0,0] if the database has not been initialized"
    (let [{:keys [latitude longitude]} (lookup-ip :not-initialized "98.101.166.2")]
      (is (= [0 0] [latitude longitude])))))
