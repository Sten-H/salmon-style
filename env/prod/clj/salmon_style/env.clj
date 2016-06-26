(ns salmon-style.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[salmon-style started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[salmon-style has shut down successfully]=-"))
   :middleware identity})
