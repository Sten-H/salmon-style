(ns salmon-style.routes.home
  (:require [salmon-style.layout :as layout]
            [salmon-style.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [file-response]]
            [clojure.java.io :as io]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [buddy.hashers :as hashers]
            ))

(defn show-image [uri]
  "If uri exists in images table of db it is rendered, otherwise 404"
  (if-let [query-row (db/get-image {:uri uri})]
    (layout/render "imageview.html" query-row)
    (layout/render "error.html" {:status "404" :title "Not found title" :message "image not found"})))

(defn about-page []
  (layout/render "about.html"))

(defn register-page [{:keys [flash]}]
  (layout/render "register.html" (select-keys flash [:errors])))

(defn login-page [{:keys [flash]}]
  (layout/render "login.html" (select-keys flash [:success :name :errors])))

(defn user-page [user]
  (layout/render "user.html"
      {:images (reverse
                 (sort-by :time
                          (db/get-user-images {:user "usr"})))}))

(defn home-page [{:keys [flash]}]
  (layout/render
    "home.html" (select-keys flash [:success])))

(defroutes home-routes
           (GET "/" request (home-page request))

           (GET "/login" request (login-page request))

           (GET "/register" request (register-page request))

           (GET "/dream/:uri" [uri] (show-image uri))

           (GET "/user/:user" [user] (user-page user))

           (GET "/original/:uri" [uri]
             (file-response (str "upload/original/" uri)))) ; Serves static images


