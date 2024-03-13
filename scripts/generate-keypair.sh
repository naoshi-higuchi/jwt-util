#!/bin/sh

# Generate RSA keys
# The JOSE standard recommends a minimum RSA key size of 2048 bits.
openssl genpkey -algorithm RSA --quiet -pkeyopt rsa_keygen_bits:2048 -out rsa-private.pem
openssl pkey -in rsa-private.pem -pubout -out rsa-public.pem

# Generate Elliptic Curve keys
for curve in 256 384 521; do
  openssl genpkey -algorithm EC --quiet -pkeyopt ec_paramgen_curve:P-$curve -out ec${curve}-key-pair.pem
  openssl ec -in ec${curve}-key-pair.pem -pubout -out ec${curve}-public.pem
done

# Generate RSA-PSS keys
openssl genpkey -algorithm RSA-PSS -quiet -pkeyopt rsa_keygen_bits:2048 -out rsa-pss-private.pem
openssl pkey -in rsa-pss-private.pem -pubout -out rsa-pss-public.pem
