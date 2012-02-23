#!/bin/sh

# This requires ffmpeg to be installed. (For example via MacPorts)
#
# Setting build paths is also not a bad idea:
# 
# export LIBRARY_PATH=/opt/local/lib
# export C_INCLUDE_PATH=/opt/local/include 
# export CPLUS_INCLUDE_PATH=/opt/local/include
# 


# build a dynamic library for Mac OS X
gcc -c -I/System/Library/Frameworks/JavaVM.framework/Headers  gestalt_candidates_NativeMovieTextureProducer.c
gcc -dynamiclib -o libNativeMovieTextureProducer.jnilib gestalt_candidates_NativeMovieTextureProducer.o -framework JavaVM -framework OpenGL -lavutil -lavformat -lavcodec -lz -lavutil -lm
rm *.o

# cp libNativeMovieTextureProducer.jnilib ~/Library/Java/Extensions/
