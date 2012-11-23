(ns diego.http
  (:require [clojure.tools.logging :as log]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [aleph.formats :as formats]
            [hiccup.core :as hiccup]
            [diego.plotting :as plotting]))

(def ^:dynamic *custom-style* "/css/empty.css")

(defn index []
  {:status 200
   :headers {}
   :body (hiccup/html
           [:head
             [:title "Diego"]
             [:link {:href "/css/main.css" :rel "stylesheet" :type "type/css"}]
             [:link {:href *custom-style* :rel "stylesheet" :type "type/css"}]]
           [:body
             [:div#globe]
             [:script {:src "/js/v/d3.v2.min.js" :type "text/javascript"}]
             [:script {:src "/js/globe.js" :type "text/javascript"}]])})

(defn points []
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (formats/encode-json->string (plotting/geo-json))})

(compojure/defroutes main-routes
  (compojure/GET "/" [] (index))
  (compojure/GET "/points" [] (points))
  (route/resources "/")
  (compojure.route/not-found "message"))

(defn start [port]
  (log/info "Starting HTTP server on port " port)
  (jetty/run-jetty main-routes {:port port}))
