#!/bin/bash

[[ "${VOXEET_SKIP_IOS_BUILD}" == "true" ]] && echo "skip build ios" && exit 0

CURR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

echo "starting carthage in $CURR_DIR"
carthage update --platform ios --verbose --project-directory $CURR_DIR/src/ios
