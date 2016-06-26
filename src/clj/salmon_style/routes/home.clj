(ns salmon-style.routes.home
  (:require [salmon-style.layout :as layout]
            [salmon-style.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
    ;[ring.middleware [multipart-params :as mp]]
            [ring.util.response :refer [file-response]]
            [clojure.java.io :as io]
    ;[bouncer.validators :as v]
            ))

(def original-img-folder "upload/original/")

(defn uri-occupied? [uri]
  "Returns true if html is found on url, false if nothing is found."
  (try (slurp uri)
       true
       (catch Exception e false)))

(defn generate-uri []
  "Concatenates two random words of length 5 and tries if url is occupied, then does it again"
  ;(slurp "http://randomword.setgetgo.com/get.php") gets a random word
  (let [w1 (future (slurp "http://randomword.setgetgo.com/get.php?len=5"))
        w2 (future (slurp "http://randomword.setgetgo.com/get.php?len=5"))
        uri (str (clojure.string/capitalize @w1)
                 (clojure.string/capitalize @w2))]
    (if (uri-occupied? uri)
      (generate-uri)
      uri)))

(defn upload-image! [img-file]
  "saves image to disk and saves img path to database"
  (let [{:keys [tempfile filename]} img-file
        uri (generate-uri)]
    (io/copy tempfile (java.io.File. filename))             ; Cope
    (db/save-image! {:uri uri :altered "alt", :original filename, :user "usr"}) ; This creates an error
    (response/found "/")))

(defn show-image [uri]
  "If uri exists in images table of db it is rendered, otherwise 404"
  (if-let [query-row (db/get-image {:uri uri})]
      (do
        (layout/render "imageview.html" query-row))
      (layout/render "error.html" {:status "404" :title "Not found title" :message "image not found"})))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn user-page [user]
  (layout/render "user.html"))

(defn login-user [params]
  (let [{:keys [name password]} params]
    (if-let [query-row (db/get-user {:name name})]
      (if (= password (:password query-row))
            (response/found (str "/user/" name))
            (spit "crap.txt" query-row)))))

(defn register-user! [params]
  (let [{:keys [name email password]} params]               ; this will probably be used later
    (db/register-user! params)
    (response/found "/login")))

(defroutes home-routes
           (GET "/" [] (home-page))

           (GET "/user/:user" [user] (user-page user))

           (GET "/register" [] (layout/render "register.html"))

           (POST "/register" request
             (register-user! (get-in request [:params])))

           (GET "/login" [] (layout/render "login.html"))

           (POST "/login" request (login-user (get-in request [:params])))

           (POST "/upload" request (upload-image! (get-in request [:params :file])))

           (GET "/dream/:uri" [uri] (show-image uri))

           (GET "/original/:filename" [filename]
             (file-response (str "upload/original/" filename)))

           (GET "/about" [] (about-page)))


