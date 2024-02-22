package org.nopware.jwt_util.cli.commands;

import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.nopware.jwt_util.*;
import org.nopware.jwt_util.cli.common.HelpOption;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.concurrent.Callable;

@Command(name = "encode", description = "Encodes a JWT token.")
@Slf4j
public class EncodeCommand implements Callable<Integer> {
    @Mixin
    private HelpOption helpOption;

    @Parameters(index = "0", arity = "0..1", description = "The payload file to encode.")
    private Path payloadPath;

    @Option(names = {"--header"}, required = false, description = "The header JSON string.")
    private String header;

    @Option(names = {"--algorithm"}, required = false, description = "The algorithm for encoding.")
    private Alg alg;

    @Option(names = {"--key"}, required = false, description = "The key for signing.")
    private Path key;

    @Override
    public Integer call() {
        try {
            if (alg == null) {
                alg = Alg.NONE;
            }

            String payload = IOUtil.readStringFromFileOrStdin(payloadPath);

            String keyString = Files.readString(key);
            byte[] keyInPem = KeyUtil.readPemObject(keyString);
            Algorithm algorithm = Algorithms.forSigning(alg, keyInPem);
            String encode = Encoder.encode(Optional.ofNullable(header), payload, algorithm);
            System.out.write(encode.getBytes());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            //log.error("Error encoding token", e);
            return 1;
        }

        return 0;
    }
}