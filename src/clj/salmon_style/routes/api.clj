(ns salmon-style.routes.api
  (:require [salmon-style.layout :as layout]
            [salmon-style.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [file-response]]
            ))

(defn get-user-inbox [user]
  (reverse (db/get-user-images {:user user})))              ; Reversing it I think will actually make it newest to oldest for some reason.

(defroutes api-routes
           (GET "/api/inbox/:user" [user] (get-user-inbox user)))
