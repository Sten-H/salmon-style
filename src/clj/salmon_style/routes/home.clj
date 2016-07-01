(ns salmon-style.routes.home
  (:require [salmon-style.layout :as layout]
            [salmon-style.db.core :as db]
            [salmon-style.routes.auth-routes :refer [get-logged-in-user]]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [file-response]]
            [clojure.java.io :as io]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [buddy.hashers :as hashers]
            [clj-time.coerce :as coerce]
            [clj-time.format :as date-format]
            ))

(defn get-template-map [request]
  "Creates a map for the Selmer templating tool to use,
  map is created from the request map. The map consists of :session and :flash
  these values will almost always be needed for templating"
  (let [template-map (select-keys request [:flash :session])]
    (if-let [user (auth-routes/get-logged-in-user request)]
      (assoc template-map
        :flash {:new-image-count (db/get-new-image-count {:user user})})
      template-map)))


(defn user-submitted-images [user]
  "Returns a list of user's submitted images, sorted by newest first, also creates
  java.util.date from sqlite datetime. This list is explicitly for rendering as html"
  ;fixme the time is in sqlie3 datetime, need to convert it to java date or something?
  ; (reverse (sort-by :time
  (let [image-rows (db/get-user-images {:user user})]
    (reverse (sort-by :time
                      (for [image image-rows]
                        (assoc image :timestamp (coerce/to-date (:timestamp image))))))))

(defn show-image [request uri]
  "If uri exists in images table of db it is rendered, otherwise 404"
  (let [user (get-logged-in-user request)]
    (if-let [query-row (db/get-image {:uri uri})]
      (do (if (= user (:user query-row))
            (db/update-image-viewed! {:uri uri :uploader-viewed "yes"}))
          (layout/render "imageview.html" (merge
                                          (get-template-map request) query-row)))
      (layout/render "error.html" {:status "404" :title "Not found title" :message "image not found"}))))

(defn register-page [request]
  (layout/render "register.html" (get-template-map request)))

(defn login-page [request]
  (layout/render "login.html" (get-template-map request)))

(defn inbox-page [request]
  (if-let [user (get-logged-in-user request)]
    (layout/render "inbox.html" (merge
                                  {:images (user-submitted-images user)}
                                  (get-template-map request)))
    (-> (response/found "/login")
        (assoc :flash {:errors "You need to log in first!"}))))

(defn home-page [request]
  (layout/render
    "home.html" (get-template-map request)))

(defroutes home-routes
           (GET "/" request (home-page request))

           (GET "/login" request (login-page request))

           (GET "/register" request (register-page request))

           (GET "/inbox" request (inbox-page request))

           (GET "/dream/:uri" [uri] (fn [request] (show-image request uri)))

           ; Serves static images
           (GET "/original/:uri" [uri]
             (file-response (str "upload/original/" uri))))


