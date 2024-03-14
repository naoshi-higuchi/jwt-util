package org.nopware.jwt_util;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.nopware.jwt_util.cli.commands.VerifyCommand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

    private final String CLAIMS_JSON = """
            {
              "sub": "1234567890",
              "name": "John Doe"
            }
            """;

    /**
     * A JWT data from <a href="https://jwt.io/">jwt.io</a>
     */
    private final String JWT_HS256 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String SECRET_HS256 = "your-256-bit-secret";

    @Test
    void encodeWithRS256(@TempDir Path tmpDir) throws IOException, URISyntaxException {
        encodeAndTest(tmpDir, "rsa-private.pem", "RS256");
    }

    @Test
    void encodeWithHS256(@TempDir Path tmpDir) throws IOException, URISyntaxException {
        encodeAndTest(tmpDir, "secret-hs256.bin", "HS256");
    }

    private void encodeAndTest(Path tmpDir, String keyResourceName, String algorithm) throws IOException, URISyntaxException {
        Path payloadFile = tmpDir.resolve("claims.json");
        Files.writeString(payloadFile, CLAIMS_JSON);

        Path keyFile = Paths.get(Resources.getResource(keyResourceName).toURI());

        ByteArrayOutputStream captor = new ByteArrayOutputStream();
        PrintStream standardOut = System.out;
        try {
            System.setOut(new PrintStream(captor));

            int exit = Application.execute(new String[]{
                    "encode",
                    "--algorithm", algorithm,
                    "--key", keyFile.toString(),
                    payloadFile.toString()
            });

            assertThat(exit).isEqualTo(0);
        } finally {
            System.setOut(standardOut);
        }

        String encoded = captor.toString();
        assertThat(encoded).isNotEmpty();
        System.out.println(encoded);
    }

    @Test
    void decodeWithHS256(@TempDir Path tmpDir) throws IOException, URISyntaxException {
        decodeAndTest(tmpDir, JWT_HS256);
    }

    void decodeAndTest(Path tmpDir, String jwt) throws IOException {
        Path jwtFile = tmpDir.resolve("jwt");
        Files.writeString(jwtFile, jwt);

        ByteArrayOutputStream captor = new ByteArrayOutputStream();
        PrintStream standardOut = System.out;

        try {
            System.setOut(new PrintStream(captor));

            int exit = Application.execute(new String[]{
                    "decode",
                    jwtFile.toString()
            });

            assertThat(exit).isEqualTo(0);
        } finally {
            System.setOut(standardOut);
        }

        String decoded = captor.toString();
        assertThat(decoded).isNotEmpty();
        System.out.println(decoded);
    }

    @Test
    void verifyWithHS256(@TempDir Path tmpDir) throws IOException, URISyntaxException {
        verifyAndTest(tmpDir, JWT_HS256);
    }

    void verifyAndTest(Path tmpDir, String jwt) throws IOException {
        Path jwtFile = tmpDir.resolve("jwt");
        Files.writeString(jwtFile, jwt);

        Path keyOrSecretFile = tmpDir.resolve("keyOrSecret");
        Files.write(keyOrSecretFile, SECRET_HS256.getBytes());

        ByteArrayOutputStream captor = new ByteArrayOutputStream();
        PrintStream standardOut = System.out;

        try {
            System.setOut(new PrintStream(captor));

            int exit = Application.execute(new String[]{
                    "verify",
                    "--key", keyOrSecretFile.toString(),
                    jwtFile.toString()
            });

            assertThat(exit).isEqualTo(0);
        } finally {
            System.setOut(standardOut);
        }

        String verified = captor.toString();
        assertThat(verified).isNotEmpty();
        assertThat(verified).isEqualTo(VerifyCommand.MSG_VALID + System.lineSeparator());
        System.out.println(verified);
    }
}
