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
(defn map-keys-to-keywords [m]
  "Takes a map and makes all its keys into keywords (from strings usually).
  The cookie from browser has some keys as strings, this fixes it"
  (into {} (for [[k v] m]
          [(keyword k) v])))

(defn get-template-map [request]
  "Creates a map for the Selmer templating tool to use,
  map is created from the request map. The map consists of :cookies and :flash
  these values will almost always be needed for templating"
  (merge
    (map-keys-to-keywords (select-keys request [:cookies])) ; Get rid of string keys. "user" -> :user
    (select-keys request [:flash])))

(defn user-submitted-images [user]
  "Returns a list (?) of all users submitted images, sorted by latest first"
  ;fixme the time is in sqlie3 datetime, need to convert it to java date or something?
  ; (reverse (sort-by :time
  (db/get-user-images {:user user}))



(defn show-image [uri]
  "If uri exists in images table of db it is rendered, otherwise 404"
  (if-let [query-row (db/get-image {:uri uri})]
    (layout/render "imageview.html" query-row)
    (layout/render "error.html" {:status "404" :title "Not found title" :message "image not found"})))

(defn about-page []
  (layout/render "about.html"))

(defn register-page [{:keys [flash]}]
  (layout/render "register.html" (select-keys flash [:errors])))

(defn login-page [request]
  (layout/render "login.html" (get-template-map request)))

(defn user-page [request user]
  (layout/render "user.html"
                 (merge
                   {:images (user-submitted-images user)}
                   (get-template-map request))))

(defn home-page [request]
  (layout/render
    "home.html" (get-template-map request)))

(defroutes home-routes
           (GET "/" request (home-page request))

           (GET "/login" request (login-page request))

           (GET "/register" request (register-page request))

           (GET "/user/:user" [user] (fn [request] (user-page request user)))

           (GET "/dream/:uri" [uri] (show-image uri))

           ; Serves static images
           (GET "/original/:uri" [uri]
             (file-response (str "upload/original/" uri))))


