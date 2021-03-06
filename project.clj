(defproject diego "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [compojure "1.1.3"]
                 [aleph "0.3.0-beta7"]
                 [hiccup "1.0.2"]
                 [contessa "0.1.0"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [org.clojure/tools.cli "0.2.2"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :main diego.core)
