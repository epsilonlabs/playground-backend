#!/bin/bash

# exit on any failure
set -e

GIT_REPO=https://github.com/eclipse-epsilon/epsilon-website.git
LOCAL_CLONE=build/epsilon-website
SOURCE_DIR="$LOCAL_CLONE/mkdocs/docs/playground/"
TARGET_DIR=build/frontend/public

if ! test -d "$LOCAL_CLONE"; then
  git clone --depth 1 "$GIT_REPO" "$LOCAL_CLONE"
else
  pushd "$LOCAL_CLONE" >/dev/null
  git pull
  popd >/dev/null
fi

rsync --exclude-from=frontend-exclusions.txt \
  --delete --delete-excluded -ravz \
  "$SOURCE_DIR" "$TARGET_DIR"
