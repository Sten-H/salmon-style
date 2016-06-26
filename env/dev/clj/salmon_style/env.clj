(ns salmon-style.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [salmon-style.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[salmon-style started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[salmon-style has shut down successfully]=-"))
   :middleware wrap-dev})
