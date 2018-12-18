#!/bin/bash

[[ "${VOXEET_SKIP_IOS_BUILD}" == "true" ]] && echo "skip build ios" && exit 0

echo "starting carthage"
carthage update --platform ios --verbose --project-directory ./src/ios
