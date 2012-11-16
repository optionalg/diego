(ns diego.http
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [aleph.http :as aleph]))

(def index
  "<html>
    <head>
      <title>Hi there</title>
    </head>
    <body>
      hi
    </body>
  </html>")

(compojure/defroutes main-routes
  (compojure/GET "/" [] index)
  (route/resources "/"))

(defn start []
  (aleph/start-http-server (aleph/wrap-ring-handler main-routes) {:port 8081 :websocket true}))
