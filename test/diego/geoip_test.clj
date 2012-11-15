(ns diego.geoip-test
  (:use clojure.test diego.geoip))

(deftest lookup-finds-information-for-ip
  (let [db (build-database "GeoLiteCity.dat")]
    (testing "can find lat/log"
      (let [{:keys [latitude longitude]} (lookup-ip db "98.101.166.2")]
       (is (= "35.860107" (str latitude)))
       (is (= "-78.8326" (str longitude)))))))
