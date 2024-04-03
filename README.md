# jwt-util
JWT utility CLI tool.

## Compile

### Prerequisites

Install GraalVM and native-image.

### Build native image

```
$ mvn clean install -DskipTests=true -Pnative
```

## Install

It has no installer. Just copy the binary `target/jwt-util` to your desired location.

## Usage

### Help

```
$ jwt-util --help
```

### Auto completion

```
$ source <(jwt-util --auto-completion-script)
$ echo "source <(jwt-util --auto-completion-script)" >> ~/.bashrc
```

### Decode

```
$ jwt-util decode <jwtPath>

$ jwt-util decode <jwtPath> --payload-only

$ jwt-util decode ./jwt --header-only
```

### Encode

```
$ jwt-util encode --alg <algorithm> --key <keyPath> <payloadPath>

$ jwt-util encode --alg RS256 --key private.pem ./payload.json

$ jwt-util encode --alg RS256 --key private.pem --header '{"foo":"bar"}' ./payload.json
```
`typ` and `alg` are added to the header automatically.


### Verify

```
jwt-util verify --key <keyPath> <jwtPath>

jwt-util verify --key secret-hs256.bin ./jwt
```
Exit with 0 if the signature is valid, 1 otherwise.

### Supported algorithms

- HS256
- HS384
- HS512
- RS256
- RS384
- RS512
- ES256
- ES384
- ES512
- PS256
- PS384
- PS512
- none

## ToDo

- Include test resources in the native test image. It is not working now. 'rsa-private.pem' and 'rsa-public.pem' are not found in the native test image and the tests fail. I work around it by skipping the tests in the native image build.
