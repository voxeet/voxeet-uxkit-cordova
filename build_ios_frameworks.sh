#!/bin/bash
CURR_DIR=`dirname "$0"`

echo "starting carthage"
carthage update --platform ios --verbose --project-directory $CURR_DIR/src/ios
