#!/usr/bin/env bash

if [ -z "$1" ] ; then
    echo "$0 VERSION"
    echo "eg, $0 2.8.1-SNAPSHOT"
    echo "would copy all exoplayer 2.8.1-SNAPSHOT artifacts from ~/.m2/repository/ to the ~/my_maven_local/ directory"
    exit 1
fi

VERSION=$1
DESTDIR=../mavenlocal
FILES=`find ~/.m2/repository/com/google/android/exoplayer/ -type f | grep 'ffmpeg' | grep "$VERSION" | grep -v "$VERSION-sources.jar" | grep -v "$VERSION-javadoc.jar"`
if [ -z "$FILES" ] ; then
    echo "No Files for VERSION: $VERSION"
    exit 2
fi

rm -rf ${DESTDIR}/com/google/android/exoplayer

for file in ${FILES} ; do
    echo "FILE: $file"
    CUR=$(dirname ~/.m2/repository/com/)
    DEST=$(echo "${file}" | sed s"@${CUR}@${DESTDIR}@"g)
    echo "DEST: $DEST"
    echo ""
    mkdir -p $(dirname ${DEST})
    cp -fv ${file} ${DEST}
done

