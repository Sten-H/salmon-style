(ns salmon-style.test.db.core
  (:require [salmon-style.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [salmon-style.config :refer [env]]
            [mount.core :as mount]
            [clj-time.coerce :as coerce]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'salmon-style.config/env
      #'salmon-style.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
                            ; Test user row creation
                            (is (= 1 (db/create-user!
                                       t-conn
                                       {:name "Samwell"
                                        :password "asd"
                                        :email "sam@well.com"})))
                            ; Make sure row created properly
                            (is (= {:name       "Samwell"
                                    :email      "sam@well.com"
                                    :password   "asd"}
                                   (db/get-user t-conn {:name "Samwell"})))
                            ; Test username case sensitivity (should not be case sensitive)
                            (is (= {:name       "Samwell"
                                    :email      "sam@well.com"
                                    :password   "asd"}
                                   (db/get-user t-conn {:name "samwell"})))))

(deftest test-images
  (jdbc/with-db-transaction [t-conn *db*]
                            (jdbc/db-set-rollback-only! t-conn)
                            (let [date (java.util.Date.)]
                              ; Test image row creation
                              (is (= 1 (db/create-image!
                                         t-conn
                                         {:uri  "SamWells"
                                          :user "Samwell"
                                          :original "SamWells.jpg"
                                          :timestamp date})))
                              ; Update image's altered path
                              (is (= 1 (db/update-image-altered!
                                         t-conn
                                         {:uri "SamWells"
                                          :altered "SamWells.jpg"})))
                              ; Check that image row has been created and updated correctly
                              (is (= (update (db/get-image t-conn {:uri "SamWells"}) :timestamp coerce/to-date)
                                     {:uri "SamWells"
                                      :user "Samwell"
                                      :original "SamWells.jpg"
                                      :altered "SamWells.jpg"
                                      :uploader_viewed "no"
                                      :timestamp date}))
                              ; Test deleting image as user who did not upload (should not be possible)
                              (is (= 0 (db/delete-image! t-conn {:uri "SamWells" :user "Frodo"})))
                              ; Test image row deletion (user can delete from inbox)
                              (is (= 1 (db/delete-image! t-conn {:uri "SamWells" :user "Samwell"})))
                              )))