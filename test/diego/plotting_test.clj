(ns diego.plotting-test
  (:use clojure.test diego.plotting))

(deftest can-parse-line
  (testing "parses key, ip, and timestamp"
    (let [[ip timestamp] (parse-line "the.key 127.0.0.1 1353073467")]
      (is (= "127.0.0.1" ip))
      (is (= "1353073467" timestamp))))
  (testing "parses with multiple spaces"
    (let [[ip timestamp] (parse-line "the.key   127.0.0.1     1353073467")]
      (is (= "127.0.0.1" ip))
      (is (= "1353073467" timestamp))))
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
  (store "1" 1353102243)
  (is (= #{"1"} (locations))))