#!/bin/bash

set -e

LOCAL_CLONE_DIR="temp/gh-pages"

function exit_with_error {
    tput setaf 1;
    echo "ERROR: $2"
    tput sgr0
    exit $1
}

./gradlew build javadoc


echo "Checking for on master branch"
if [ ${CIRCLE_BRANCH=local} != "master" ]; then
    exit_with_error 1 "Not on master branch"
fi

GITHUB_CLONE_URL=`git remote get-url origin`


rm -f -r $LOCAL_CLONE_DIR

echo "Getting gh-pages branch from GitHub: $LOCAL_CLONE_DIR"
git clone $GITHUB_CLONE_URL $LOCAL_CLONE_DIR
if [ ! -d "$LOCAL_CLONE_DIR" ]; then
    exit_with_error 2 "Failed to get gh-pages branch from GitHub!"
fi


(cd $LOCAL_CLONE_DIR
    git fetch origin
    git checkout gh-pages

    echo "delete all old documentation pages and replace with new javadoc"    
    rm -r *
    cp  -r ../../build/docs/javadoc/ javadoc/
    
    echo "Commit and push javadoc to gh-pages"
    git init
    git config user.name "CircleCI"
    git config user.email "brett@annalytics.co.uk"
    git add *

    git commit -a -m "javadoc automatically updated"
    git  push
)


echo "Finished - javadoc docs automatically updated"
