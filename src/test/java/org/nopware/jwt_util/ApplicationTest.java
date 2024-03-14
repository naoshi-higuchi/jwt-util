package org.nopware.jwt_util;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

    private final String CLAIMS_JSON = """
            {
              "sub": "1234567890",
              "name": "John Doe"
            }
            """;

    @Test
    void encodeWithRS256(@TempDir Path tmpDir) throws IOException, URISyntaxException {
        Path tmpFile = tmpDir.resolve("claims.json");
        Files.writeString(tmpFile, CLAIMS_JSON);

        URL keyResource = Resources.getResource("rsa-private.pem");
        Path keyFile = Paths.get(keyResource.toURI());

        PrintStream standardOut = System.out;
        ByteArrayOutputStream captor = new ByteArrayOutputStream();

        try {
            System.setOut(new java.io.PrintStream(captor));

            int exit = Application.execute(new String[]{
                    "encode",
                    "--algorithm", "RS256",
                    "--key", keyFile.toString(),
                    tmpFile.toString()
            });

            assertThat(exit).isEqualTo(0);
            System.out.flush();
        } finally {
            System.setOut(standardOut);
        }

        String encoded = captor.toString();
        assertThat(encoded).isNotEmpty();
        System.out.println(encoded);
    }
}
