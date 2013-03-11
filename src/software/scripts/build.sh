# Copyright 2012 Christe, Anthony
# 
# This file is part of KiP.
#
# KiP is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# KiP is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with KiP.  If not, see <http://www.gnu.org/licenses/>.

#!/bin/bash

# Base directory of source
SRC="../main/java"

# Location to store temporary class files
BIN="../bin"

# Location of resulting jar
DIST="../../../dist"

# Location of resulting tar.gz release
RELEASE="../../../release"

# Release name
RELEASE_NAME="KiP"

# Version
VERSION="1.0"

# Name of jar file
JAR_TITLE="$RELEASE_NAME-$VERSION.jar"

# Name of archive
ARCHIVE_TITLE="$RELEASE_NAME-$VERSION.tar.gz"

# Manifest file
MANIFEST="$DIST/Manifest.txt"

# kip.utils source
UTILS="../main/java/kip/utils/*.java"

# kip.client source
CLIENT="../main/java/kip/client/*.java"

# kip.client.ui source
UI="../main/java/kip/client/ui/*.java"

# kip.emulator source
EMULATOR="../main/java/kip/emulator/*.java"

# kip.ssl source
SSL="../main/java/kip/ssl/*.java"

# GRAL library
LIBGRAL="GRAL-0.8-dep.jar"
#LIBGRAL="gral-core-9.0.jar"

# Apache Commons math library
LIBAMATH="commons-math3-3.0.jar"

# Location of libraries
LIB="../../../lib/$LIBGRAL:../../../lib/$LIBAMATH"

# Location to lib base
LIBBASE="../../../lib"

# Location of libraries in dist dir
DISTLIB=$DIST/lib

echo "Preparing build"
rm -rf $DIST
rm -rf $RELEASE
mkdir -p $BIN $DIST $DISTLIB $RELEASE
cp $LIBBASE/* $DISTLIB/.
echo "Class-Path: lib/$LIBGRAL lib/$LIBAMATH" > $MANIFEST

echo "Building"
javac -Xlint -cp $LIB -d $BIN $UTILS $CLIENT $UI $EMULATOR $SSL

echo "Generating jar"
jar cmf $MANIFEST $DIST/$JAR_TITLE -C $BIN . $SRC/*

echo "Packaging"
tar czf $RELEASE/$ARCHIVE_TITLE $DIST/$JAR_TITLE $DIST/lib

echo "Cleaning up"
rm -r $BIN
rm $MANIFEST

