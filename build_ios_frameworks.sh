#!/bin/bash
CURR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

echo "starting carthage in $CURR_DIR"
carthage update --platform ios --verbose --project-directory $CURR_DIR/src/ios
