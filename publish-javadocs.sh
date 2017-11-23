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


#echo "Checking for on master branch"
#if [ ${CIRCLE_BRANCH=local} != "master" ]; then
#    exit_with_error 1 "Not on master branch"
#fi

remote=$(git config remote.origin.url)


rm -f -r $LOCAL_CLONE_DIR

mkdir $LOCAL_CLONE_DIR
cd $LOCAL_CLONE_DIR

git config --global user.name "CircleCI" > /dev/null 2>&1
git config --global user.email "brett@annalytics.co.uk" > /dev/null 2>&1
git init

echo "Get gh-pages from remote"
git remote add --fetch origin "$remote"

# switch into the the gh-pages branch
if git rev-parse --verify origin/gh-pages > /dev/null 2>&1
then
    git checkout gh-pages
    # delete any old site as we are going to replace it
    # Note: this explodes if there aren't any, so moving it here for now
    # git rm -rf .
else
    git checkout --orphan gh-pages
fi


echo "Copy in latest javadocs"
cp  -r ../../build/docs/javadoc/ javadoc/

echo "Commit and push javadoc to gh-pages"
# stage any changes and new files
git add -A
# now commit, ignoring branch gh-pages doesn't seem to work, so trying skip
git commit --allow-empty -m "Deploy to GitHub pages [ci skip]"
# and push, but send any output to /dev/null to hide anything sensitive
git push --force --quiet origin gh-pages

cd ../..

rm -rf $LOCAL_CLONE_DIR

echo "Finished - javadoc docs automatically updated"
