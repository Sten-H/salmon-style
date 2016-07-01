(ns salmon-style.routes.inbox
  (:require [salmon-style.db.core :as db]
            [salmon-style.routes.auth-routes :as auth-routes]
            [salmon-style.routes.image-upload :as image-upload]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [file-response]]))

(defn inbox-changed? [request]
  ; I think I could do something like this server side with (diff), but maybe it's better
  ; to let client side do it even if it's clunky?
  )

(defn get-user-inbox [request]
  "Returns a list of logged in user's image inbox. The most recent uploads will be ordered
  first (just by nature of the database order, not actually sorted, this might bug later?).
  This list is not for rendering as html, clientside javascript polls this and compares."
  (if-let [user (auth-routes/get-logged-in-user request)]
    (reverse (db/get-user-images {:user user}))))

(defn delete-image [request uri]
  "Delete image row from db and server disk if user is logged in and the owner of the image.
  returns {:delete-success true/false}"
   (if-let [user (auth-routes/get-logged-in-user request)]
     (let [image-row (db/get-image {:uri uri})
           rows-deleted (db/delete-image! {:uri uri :user user})]
       (if (= rows-deleted 1)                               ; if a row was deleted
         (do (image-upload/delete-image-from-disk (:original image-row))
            {:delete-success true})
         {:delete-success false}))))


(defroutes inbox-routes
           (GET "/inbox/get" request (get-user-inbox request))
           (GET "/inbox/delete/:uri" [uri] (fn [request] (delete-image request uri))))
