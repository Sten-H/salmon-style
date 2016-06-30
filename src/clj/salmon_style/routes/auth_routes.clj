(ns salmon-style.routes.auth-routes
  (:require [salmon-style.layout :as layout]
            [salmon-style.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [buddy.hashers :as hashers]
            ))

(defn get-logged-in-user [{:keys [cookies]}]
  "Gets the user from request map cookie, returns nil if not logged in"
  (get-in cookies ["user" :value]))

(defn validate-user-login [name password]
  "Returns eventual errors from user's login form, if nil
  is returned the form had no errors."
  (if-let [query-row (db/get-user {:name name})]
    (if-not (hashers/check password (:password query-row))
      {:password "Incorrect password."})
    {:name "Username not recognized."}
    ))

(defn validate-user-registration [params]
  "Returns eventual errors from user's registration form, if nil
  is returned the form had no errors."
  (if-let [error (first (b/validate
                          params
                          :name [v/required [v/min-count 3]]
                          :password [v/required [v/min-count 7]]
                          :email v/email))]
    error
    (if (db/get-user params)
      {:name "Username is already taken."})))

(defn register-user! [params]
  "If username is not already in database and input does not
  raise validation errors a user is created, otherwise errors are returned"
  (let [{:keys [name email password]} params]
    (if-let [errors (validate-user-registration params)]
      (-> (response/found "/register")
          (assoc :flash {:errors errors}))
      (do
        (db/register-user! (assoc params :password (hashers/encrypt password)))
        (-> (response/found "/login")
            (assoc :flash {:success "User registered."}))))))

(defn login-user! [{:keys [name password]}]
  "Validates the login information if it contains no
  errors the users should be logged in (user session login not done yet)"
  (if-let [errors (validate-user-login name password)]
    (-> (response/found "/login")
        (assoc :flash {:errors errors}))
    (-> (response/found "/")
        (assoc :cookies {:user name})
        (assoc :flash {:success
                       (str "Welcome back " name "!")}))))

(defn logout-user! [{:keys [cookies]}]
  (-> (response/found "/")
      (assoc-in [:cookies "user"] {:value "something" :max-age 0}))) ; can't set value to nil or "" for some reason.

(defroutes auth-routes
           (POST "/login" request (login-user! (:params request)))

           (POST "/register" request
             (register-user! (:params request)))

           (GET "/logout" request (logout-user! request)))
