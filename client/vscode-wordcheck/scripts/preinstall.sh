#!/bin/bash
set -e

workdir=`pwd`

# Use maven to build fat jar of the language server
cd ${workdir}/../../server/example-wordcheck
./build.sh

rm -fr ${workdir}/jars
mkdir -p ${workdir}/jars
cp target/*.jar ${workdir}/jars/wordcheck-language-server.jar
