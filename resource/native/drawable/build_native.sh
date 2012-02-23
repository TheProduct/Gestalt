#!/bin/sh

# build a dynamic library for Mac OS X
cc -c -I/System/Library/Frameworks/JavaVM.framework/Headers gestalt_candidates_NativeDrawable.c
cc -dynamiclib -o libNativeDrawable.jnilib gestalt_candidates_NativeDrawable.o -framework JavaVM -framework OpenGL
rm *.o