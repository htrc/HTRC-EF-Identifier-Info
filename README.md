# Extracted Features identifier information

This service provides a browser-accessible endpoint that displays
information about an extracted-features (EF) volume.  

Each EF file is encoded as JSON-LD and contains a unique identifier
encoded as a URL. When that URL is accessed via a web browser, this
service handles that request and generates a user-friendly interface
containing useful information about that specific volume.

The information returned represents a subset of the volume's 
metadata, as well as provides links and information to how that
volume can be downloaded via the browser or the command line.

## Building the code

`sbt clean compile test` -- to compile and run unit tests  
`sbt dist` -- to create a deployable ZIP package

## Generating a Docker image

`sbt clean docker:publishLocal` -- to publish the image to the local Docker server  
`sbt clean docker:publish` -- to publish the image to the HTRC Nexus Docker repository