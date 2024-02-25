# jwt-util
JWT utility CLI tool.

## Compile

$ mvn clean install -Pnative

## Install

It has no installer. Just copy the binary `target/jwt-util` to your desired location.

## Usage

### Help

$ jwt-util --help

### Decode

$ jwt-util decode <jwtPath>

$ jwt-util decode <jwtPath> --payload-only

$ jwt-util decode ./jwt --header-only

### Encode

$ jwt-util encode --payload <payloadPath> --key <keyPath> --algorithm <algorithm>

$ jwt-util encode --payload ./payload.json --key private.pem --algorithm RS256 --header '{"foo":"bar"}'

### ToDo

- Specify 'sun.security.x509.X509CertImpl' in src/main/resources/META-INF/native-image/reflection-config.json. I code it by hand now. It should be generated automatically.
- Include test resources in the native test image. It is not working now. 'rsa-private.pem' and 'rsa-public.pem' are not found in the native test image and the tests fail. I work around it by skipping the tests in the native image build.
- Support algorithms other than RS256.
