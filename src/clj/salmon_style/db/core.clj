(ns salmon-style.db.core
  (:require
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    [salmon-style.config :refer [env]]))

(defstate ^:dynamic *db*
          :start (conman/connect!
                   {:datasource
                    (doto (org.sqlite.SQLiteDataSource.)
                          (.setUrl (env :database-url)))})
          :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")

