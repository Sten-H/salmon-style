# salmon-style

generated using Luminus version "2.9.10.73"

FIXME

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

## To do
### Tests
yeah.
### Restructure database
I feel like I need an inbox table in a way. And maybe a table with a user name tied to an id, faster probably?
### Send email
Send email when image is done if user checks it. Checking it does nothing right now.
### Image upload
Stop user from uploading non-images, and very large images.
<timvisher> javax.imageio
<timvisher> java.awt.image
<timvisher> hmm… i actually use org.jdesktop.swingx.graphics.GraphicsUtilities/loadCompatibleImage
<timvisher> i believe mikera has a library force image processing as well
http://www.joelonsoftware.com/articles/Unicode.html
http://nedbatchelder.com/text/unipain.html
## License
Copyright © 2016 FIXME
