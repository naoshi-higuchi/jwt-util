# jwt-util
JWT utility CLI tool.

It can encode, decode, and verify JWT.

## Quick start

```
$ jwt-util encode ./payload.json --alg RS256 --key ./rsa-private.pem > ./jwt
$ jwt-util decode ./jwt
$ jwt-util verify ./jwt --key ./rsa-public.pem
```

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

### Decode

```
$ jwt-util decode <jwtPath>

$ jwt-util decode <jwtPath> --payload-only

$ jwt-util decode ./jwt --header-only
```

### Encode

```
$ jwt-util encode --alg <algorithm> --key <keyPath> <payloadPath>

$ jwt-util encode --alg RS256 --key ./private.pem ./payload.json

$ jwt-util encode --alg RS256 --key ./private.pem --header '{"foo":"bar"}' ./payload.json
```
`typ` and `alg` are added to the header automatically.


### Verify

```
jwt-util verify --key <keyPath> <jwtPath>

jwt-util verify --key ./secret-hs256.bin ./jwt
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

### Tips

#### Read from stdin

Use '-' as the file path to read from stdin.

```
$ cat ./jwt | jwt-util decode -
$ cat ./payload.json | jwt-util encode - --alg RS256 --key ./rsa-private.pem
```

#### Auto completion

```
$ source <(jwt-util --auto-completion-script)
$ echo "source <(jwt-util --auto-completion-script)" >> ~/.bashrc
```

#### Test

If you want to run `mvn test`, you need to generate keypair and secret.
Refer to the [scripts/README.md](./scripts/README.md).

## ToDo

- Include test resources in the native test image. It is not working now. 'rsa-private.pem' and 'rsa-public.pem' are not found in the native test image and the tests fail. I work around it by skipping the tests in the native image build.
