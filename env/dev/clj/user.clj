(ns user
  (:require [mount.core :as mount]
            salmon-style.core))

(defn start []
  (mount/start-without #'salmon-style.core/repl-server))

(defn stop []
  (mount/stop-except #'salmon-style.core/repl-server))

(defn restart []
  (stop)
  (start))


