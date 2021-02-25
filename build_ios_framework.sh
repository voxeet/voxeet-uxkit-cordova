#!/bin/bash

echo "use of this script is deprecated and will be removed in the future"

[[ "${VOXEET_SKIP_IOS_BUILD}" == "true" ]] && echo "skip build ios" && exit 0
echo "use of this script is deprecated and will be removed in the future -- if using from the plugin add command, nothing to do"

echo "Installing \"cordova-plugin-voxeet\" ios dependencies (carthage)"
carthage update --platform ios --project-directory `dirname "$0"`/src/ios