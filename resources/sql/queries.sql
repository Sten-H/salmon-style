-- :name create-image! :! :n
-- :doc creates image unique url as key
INSERT INTO images
(uri, original, user, timestamp)
VALUES (:uri, :original, :user, :timestamp)

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
SET altered = :altered, uploader_viewed = "no"
WHERE uri = :uri

-- :name delete-image! :! :n
-- :doc deletes image if the logged in user is the uploader. Returns 1 if delete was successful.
DELETE FROM images
WHERE uri = :uri AND user = :user

-- :name get-user-images :? :*
-- :doc gets all images made by user
SELECT * FROM images where user = :user

-- :name get-new-image-count :? :1
-- :doc returns the number of unviewed images
SELECT count(*) FROM images WHERE user = :user AND uploader_viewed = "no"

-- :name create-user! :! :n
-- :doc creates a user in users table
INSERT INTO users
(name, email, password)
VALUES (:name, :email, :password)

-- :name get-user :? :1
-- :doc selects the entire row of user with id supplied
SELECT * FROM users where name = :name