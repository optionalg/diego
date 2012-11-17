(ns diego.http
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [aleph.http :as aleph]
            [aleph.formats :as formats]
            [diego.plotting :as plotting]))

(defn index []
  {:status 200
   :headers {}
   :body
  "<html>
    <head>
      <title>Hi there</title>
      <link href='/css/main.css' rel='stylesheet' type='text/css' />
    </head>
    <body>
      <div id='globe'></div>
      <script src='/js/v/d3.v2.min.js' type='text/javascript'></script>
      <script src='/js/globe.js' type='text/javascript'></script>
    </body>
  </html>"})

(defn points []
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (formats/encode-json->string (plotting/geo-json))})

(compojure/defroutes main-routes
  (compojure/GET "/" [] (index))
  (compojure/GET "/points" [] (points))
  (route/resources "/")
  (compojure.route/not-found "message"))

(defn start []
  (aleph/start-http-server (aleph/wrap-ring-handler main-routes) {:port 8081}))
