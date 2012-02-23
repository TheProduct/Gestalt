#!/bin/sh

export PATH_TO_GESTALT_JAR=$1

# create a header file -- create to native class
javah -classpath $PATH_TO_GESTALT_JAR gestalt.candidates.NativeMovieTextureProducer


