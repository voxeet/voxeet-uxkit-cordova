#!/bin/bash

# remove files
rm -rf build/dist
mkdir -p build/dist/www

rm www/*

# cd to the library files
cd lib

#run typescript compiler
tsc

# they will be put in the www folder

#back to main
cd ..

#cp proper files into the dist folder
cp -r ./www/* ./build/dist/www/
cp ./package.json ./build/dist/
cp ./build_ios_frameworks.sh ./build/dist/build_ios_frameworks.sh
cp ./voxeet_application.js ./build/dist/voxeet_application.js

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
