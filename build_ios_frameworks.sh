#!/bin/bash

echo "use of this script is deprecated and will be removed in the future -- if using from the plugin add command, nothing to do"

echo "Installing \"cordova-plugin-voxeet\" ios dependencies (carthage)"
carthage update --platform ios --project-directory `dirname "$0"`/src/ios
