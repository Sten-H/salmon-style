(ns salmon-style.routes.image-upload
  (:require [salmon-style.layout :as layout]
            [salmon-style.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.core.async :as async
                      :refer [go thread timeout]]
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

(defn generate-altered-image-mock-up [uri original-filename]
  "Simulates an image being processed, updates database entry when done"
  (Thread/sleep (+ 5000 (rand-int 10000)))
  ; FIXME it is technically possible that this finishes before the database entry has been made
  (db/update-image-altered! {:uri uri :altered original-filename}))

(defn upload-image! [img-file user]
  "Saves image to disk and saves img path to database,
  right now it does not save file ending, fix that."
  (let [{:keys [tempfile filename]} img-file
        uri (generate-uri)
        file-extension (re-find #"\.[a-z]+" filename)
        original-filename (str uri file-extension)
        ]
    (io/copy tempfile (java.io.File. (str original-img-folder original-filename)))
    (async/thread (generate-altered-image-mock-up uri original-filename))
    (async/thread (db/save-image! {:uri uri, :original original-filename, :altered nil, :user user, :timestamp (java.util.Date.)}))
    (-> (response/found "/")
        (assoc :flash {:success "Image being salmoned. Check your inbox soon!"}))))

(defroutes upload-routes
           (POST "/upload" request (upload-image! (get-in request [:params :file]) (get-in request [:cookies "user" :value]))))