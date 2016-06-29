-- :name save-image! :! :n
-- :doc save image file paths with unique url as key
INSERT INTO images
(uri, altered, original, user, timestamp, uploader_viewed)
VALUES (:uri, :altered, :original, :user, :timestamp, "no")

-- :name get-image :? :1
-- :doc get the entire row of the image with the queried uri
SELECT * FROM images WHERE uri = :uri

-- :name update-image-viewed! :! :n
-- :doc updates uploader_viewed in image
UPDATE images
SET uploader_viewed = :uploader-viewed
WHERE uri = :uri

-- :name update-image-altered! :! :n
-- :doc updates image in images table if image uploader = :user
UPDATE images
SET altered = :altered
WHERE uri = :uri


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