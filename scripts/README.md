# scripts

All scripts in this directory are for developers only.
Not for jwt-util users.

## For testing

Generate keypair and secret for testing.

These files are not committed to this repository.
So you need to generate them before running tests or skip tests with -DskipTests=true option.

```bash
$ sh ./generate-keypair.sh
$ sh ./generate-secret.sh
$ sh ./copy-to-testResources.sh
```

Run tests.

```bash
$ cd ..
$ mvn test
```

## For building native image

Prepare configuration files for native-image tool.

These files are generated in advance and committed to this repository.
So you don't need to regenerate them unless you modify the source code.

```bash
$ sh ./exec-with-agent.sh
```

Build native image.

```bash
$ cd ..
$ mvn clean install -DskipTests=true -Pnative
```