package org.nopware.jwt_util.cli.commands;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.nopware.jwt_util.Decoder;
import org.nopware.jwt_util.IOUtil;
import org.nopware.jwt_util.cli.common.HelpOption;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "decode", description = "Decodes a JWT token.")
@Slf4j
public class DecodeCommand implements Callable<Integer> {
    @Mixin
    private HelpOption helpOption;

    @Parameters(index = "0", arity = "1", description = "The JWT file to decode.")
    private Path jwtPath;

    @ArgGroup(exclusive = true, multiplicity = "0..1")
    private OnlyOption onlyOption;

    static class OnlyOption {
        @Option(names = {"--header-only"}, description = "Only decode the header of the token.")
        private boolean headerOnly;

        @Option(names = {"--payload-only"}, description = "Only decode the payload of the token.")
        private boolean payloadOnly;

        @Option(names = {"--signature-only"}, description = "Only decode the signature of the token.")
        private boolean signatureOnly;
    }

    @Override
    public Integer call() {
        try {
            String jwt = IOUtil.readStringFromFileOrStdin(jwtPath);
            DecodedJWT decode = Decoder.decode(jwt);

            if (onlyOption != null) {
                if (onlyOption.headerOnly) {
                    System.out.write(IOUtil.base64Decode(decode.getHeader()));
                } else if (onlyOption.payloadOnly) {
                    System.out.write(IOUtil.base64Decode(decode.getPayload()));
                } else if (onlyOption.signatureOnly) {
                    System.out.write(decode.getSignature().getBytes());
                }
            } else {
                System.out.write(IOUtil.base64Decode(decode.getHeader()));
                System.out.println();
                System.out.write(IOUtil.base64Decode(decode.getPayload()));
                System.out.println();
                System.out.write(decode.getSignature().getBytes());
            }
        } catch (IOException e) {
            //log.error("Error decoding JWT file", e);
            return 1;
        }

        return 0;
    }
}
