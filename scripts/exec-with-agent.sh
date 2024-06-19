#!/bin/sh

JAR=../target/jwt-util.jar
OUTPUTDIR=../src/main/resources/META-INF/native-image

for hashLen in 256 384 512; do
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} encode ./claims.json --alg RS${hashLen} --key ./rsa-private.pem > ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} decode ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} verify --key ./rsa-public.pem ./jwt
done

for hashLen in 256 384 512; do
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} encode ./claims.json --alg PS${hashLen} --key ./rsa-pss-${hashLen}-private.pem > ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} decode ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} verify --key ./rsa-pss-${hashLen}-public.pem ./jwt
done

for hashLen in 256 384; do
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} encode ./claims.json --alg ES${hashLen} --key ./ec${hashLen}-key-pair.pem > ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} decode ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} verify --key ./ec${hashLen}-public.pem ./jwt
done
java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} encode ./claims.json --alg ES512 --key ./ec521-key-pair.pem > ./jwt
java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} decode ./jwt
java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} verify --key ./ec521-public.pem ./jwt


for hashLen in 256 384 512; do
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} encode ./claims.json --alg HS${hashLen} --key ./secret-hs${hashLen}.bin > ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} decode ./jwt
  java -agentlib:native-image-agent=config-merge-dir=${OUTPUTDIR} -jar ${JAR} verify --key ./secret-hs${hashLen}.bin ./jwt
done
