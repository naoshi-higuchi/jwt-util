#!/bin/sh

JAR=../target/jwt-util.jar
OUTPUTDIR=../src/main/resources/META-INF/native-image
java -agentlib:native-image-agent=config-output-dir=${OUTPUTDIR} -jar ${JAR} encode ./claims.json --algorithm RS256 --key ./rsa-private.pem > ./jwt
#java -agentlib:native-image-agent=config-output-dir=${OUTPUTDIR} -jar ${JAR} decode ./jwt
