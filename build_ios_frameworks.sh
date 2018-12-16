#!/bin/bash

if [ "$VOXEET_SKIP_IOS_BUILD" = "true" ]; then 
    echo "Skipping ios build"
    exit
else
    cd ./src/ios
    carthage update --platform ios
fi
