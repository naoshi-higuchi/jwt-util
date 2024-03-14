package org.nopware.jwt_util.cli.commands;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.nopware.jwt_util.*;
import org.nopware.jwt_util.cli.common.HelpOption;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "verify", description = "Verifies a JWT token.")
@Slf4j
public class VerifyCommand implements Callable<Integer> {

    public static final String MSG_VALID = "OK";
    public static final String MSG_INVALID = "INVALID: ";
    public static final String EXMSG_FAILED_TO_READ_JWT = "Failed to read JWT file: ";

    @Value
    @Builder
    @Jacksonized
    private static class Header {
        String alg;
        String typ;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mixin
    private HelpOption helpOption;

    @Parameters(index = "0", arity = "0..1", description = "The JWT file to decode.")
    private Path jwtPath;

    @Option(names = {"--key"}, required = true, description = "The key or secret for verifying.")
    private Path key;

    @Override
    public Integer call() throws Exception {
        try {
            String jwt = IOUtil.readStringFromFileOrStdin(jwtPath);
            DecodedJWT decode = Decoder.decode(jwt);
            String headerStr = new String(IOUtil.base64Decode(decode.getHeader()));

            Header header = objectMapper.readValue(headerStr, Header.class);

            Alg alg = Alg.valueOf(header.getAlg());
            byte[] keyOrSecret = alg == Alg.NONE ? null : KeyUtil.readKeyOrSecret(alg, key);
            Algorithm algorithm = Algorithms.forVerifying(alg, keyOrSecret);

            try {
                DecodedJWT ignore = Decoder.verify(jwt, algorithm);
                System.out.println(MSG_VALID);
            } catch (JWTVerificationException e) {
                System.out.println(MSG_INVALID + e.getMessage());
                log.debug("Failed to verify JWT.", e);
                return CommandLine.ExitCode.SOFTWARE;
            }
        } catch (IOException e) {
            System.out.println(EXMSG_FAILED_TO_READ_JWT + e.getMessage());
            log.debug("Failed to read JWT file.", e);
            return CommandLine.ExitCode.SOFTWARE;
        }

        return CommandLine.ExitCode.OK;
    }
}
