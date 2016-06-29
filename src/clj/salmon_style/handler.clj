(ns salmon-style.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [salmon-style.layout :refer [error-page]]
            [salmon-style.routes.home :refer [home-routes]]
            [salmon-style.routes.auth-routes :refer [auth-routes]]
            [salmon-style.routes.image-upload :refer [upload-routes]]
            [salmon-style.routes.api :refer [api-routes]]
            [compojure.route :as route]
            [salmon-style.env :refer [defaults]]
            [mount.core :as mount]
            [salmon-style.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'auth-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'upload-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'api-routes
            (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
