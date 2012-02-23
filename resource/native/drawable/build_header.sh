#!/bin/sh

export PATH_TO_GESTALT_JAR=$1

# create a header file -- point classpath to gestalt jar
javah -classpath $PATH_TO_GESTALT_JAR gestalt.candidates.NativeDrawable
