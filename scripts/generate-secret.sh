#!/usr/bin/bash

for bytes in 32 48 64; do
  bits=$(expr $bytes \* 8)
  openssl rand -out secret-hs${bits}.bin $bytes
done