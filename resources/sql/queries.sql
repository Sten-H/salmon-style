-- :name save-image! :! :n
-- :doc save image file paths with unique url as key
INSERT INTO images
(uri, altered, original, user, timestamp)
VALUES (:uri, :altered, :original, :user, :timestamp)

-- :name get-image :? :1
-- :doc get the entire row of the image with the queried uri
SELECT * FROM images WHERE uri = :uri

-- :name get-user-images :? :*
-- :doc gets all images made by user
SELECT * FROM images where user = :user

-- :name register-user! :! :n
-- :doc registers user
INSERT INTO users
(name, email, password)
VALUES (:name, :email, :password)

-- :name get-user :? :1
-- :doc selects the entire row of user with id supplied
SELECT * FROM users where name = :name