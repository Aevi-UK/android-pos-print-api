#!/bin/bash

set -e

LOCAL_CLONE_DIR="temp/gh-pages"
JAVA_DOC_LOCATION="build/docs/javadoc/"

function exit_with_error {
    echo "ERROR: $2"
    exit $1
}

if [ ! -d $JAVA_DOC_LOCATION ]; then
    exit_with_error 1 "Javadocs have not been built!"
fi

echo "Checking for on master branch"
if [ ${CIRCLE_BRANCH=local} != "master" ]; then
    exit_with_error 1 "Not on master branch"
fi

remote=$(git config remote.origin.url)


rm -f -r $LOCAL_CLONE_DIR

mkdir -p $LOCAL_CLONE_DIR
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
    rm -f -r *
else
    git checkout --orphan gh-pages
fi


echo "Copy in latest javadocs"
cp  -r ../../$JAVA_DOC_LOCATION javadoc/

echo "Commit and push javadoc to gh-pages"
# stage any changes and new files
git add -A
# now commit, ignoring branch gh-pages doesn't seem to work, so trying skip
git commit --allow-empty -m "Auto deploy to GitHub pages"
# and push, but send any output to /dev/null to hide anything sensitive
git push --force --quiet origin gh-pages

cd ../..

rm -rf $LOCAL_CLONE_DIR

echo "Finished - javadoc docs automatically updated"
