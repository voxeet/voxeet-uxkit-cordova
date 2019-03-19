#!/bin/bash

# remove files
mkdir -p build/dist
rm -rf build/dist

# cd to the library files
cd www

#run typescript compiler
tsc

#back to main
cd ..

#cp proper files into the dist folder
cp ./package.json ./build/dist/
cp ./build_ios_frameworks.sh ./build/dist/build_ios_frameworks.sh

#copy platforms src
mkdir -p build/dist/src
cp -r src build/dist/

#copy back README
cp README.md build/dist/README.md
cp plugin.xml build/dist/plugin.xml
cp RELEASENOTES.md build/dist/RELEASENOTES.md
cp SAMPLE_ANDROID.md build/dist/SAMPLE_ANDROID.md
cp NOTICE build/dist/NOTICE

cp plugin.xml build/dist/plugin.xml