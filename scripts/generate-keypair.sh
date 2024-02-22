#!/bin/sh

openssl genrsa 1024 > rsa-private.pem
openssl rsa -in rsa-private.pem -pubout -out rsa-public.pem
