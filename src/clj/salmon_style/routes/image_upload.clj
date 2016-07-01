(ns salmon-style.routes.image-upload
  (:require [salmon-style.layout :as layout]
            [salmon-style.db.core :as db]
            [salmon-style.routes.auth-routes :refer [get-logged-in-user]]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.core.async :as async
                      :refer [go thread timeout]]
            ))

(def original-img-folder "upload/original/")
(def altered-img-folder "altered/original/")

(defn delete-image-from-disk [filename]
  (future (io/delete-file (str original-img-folder filename)))
  (future (io/delete-file (str altered-img-folder filename))))

(defn uri-occupied? [uri]
  "Returns true if html is found on url, false if nothing is found."
  (try (slurp uri)
       true
       (catch Exception e false)))

(defn generate-uri []
  "Concatenates two random words of total length 10, will redo if uri is occupied."
  (let [word-total-length 10
        word1-len (+ 3 (rand-int (/ word-total-length 2)))  ; word1 can be max length (total-length/2) + 2
        word2-len (- word-total-length word1-len)
        w1 (future (slurp (str "http://randomword.setgetgo.com/get.php?len=" word1-len)))
        w2 (future (slurp (str "http://randomword.setgetgo.com/get.php?len=" word2-len)))
        uri (str (clojure.string/capitalize @w1)
                 (clojure.string/capitalize @w2))]
    (if (uri-occupied? uri)
      (generate-uri)
      uri)))

(defn generate-altered-image-mock-up [uri original-filename]
  "Simulates an image being processed, updates database entry when done"
  (Thread/sleep (+ 5000 (rand-int 10000)))
  ; FIXME it is technically possible that this finishes before the database entry has been made
  (db/update-image-altered! {:uri uri :altered original-filename}))

(defn upload-image! [request]
  "Saves image to disk and saves img path to database."
  (let [{:keys [tempfile filename]}
        (get-in request [:params :file])
        uri (generate-uri)
        user (future (get-logged-in-user request))
        file-extension (re-find #"\.[a-z]+" filename)
        original-filename (str uri file-extension)]
    (async/thread (io/copy tempfile (java.io.File. (str original-img-folder original-filename))))
    (async/thread (generate-altered-image-mock-up uri original-filename))
    (async/thread (db/create-image! {:uri uri, :original original-filename, :altered nil, :user @user, :timestamp (java.util.Date.)}))
    (-> (response/found "/")
        (assoc :flash {:success "Image being salmoned. Check your inbox soon!"}))))

(defroutes upload-routes
           (POST "/upload" request (upload-image! request)))