package org.nopware.jwt_util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class IOUtilTest {

    @Test
    void readStringFromFileOrStdin(@TempDir Path tmpDir) throws IOException {
        final String EXPECTED = "readStringFromFileOrStdin";

        Path testFile = tmpDir.resolve("test.txt");
        Files.writeString(testFile, EXPECTED);

        String result = IOUtil.readStringFromFileOrStdin(testFile);
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    void readAllBytesFromFileOrStdin() throws IOException {
        byte[] bytes = IOUtil.readAllBytesFromFileOrStdin(Paths.get("src/test/resources/rsa-private.pem"));
        assertThat(bytes).isNotEmpty();
        assertThat(bytes.length).isGreaterThan(0);
    }

    @Test
    void readAllBytesFromFileOrStdinWithNullPath() throws IOException {
        final InputStream stdin = System.in;
        final String EXPECTED = "readAllBytesFromFileOrStdinWithNullPath";
        try (ByteArrayInputStream in = new ByteArrayInputStream(EXPECTED.getBytes());) {
            System.setIn(in);
            byte[] bytes = IOUtil.readAllBytesFromFileOrStdin(null); // null path
            assertThat(bytes).isEqualTo(EXPECTED.getBytes());
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    void readAllBytesFromFileOrStdinWithDashPath() throws IOException {
        final InputStream stdin = System.in;
        final String EXPECTED = "readAllBytesFromFileOrStdinWithDashPath";
        try (ByteArrayInputStream in = new ByteArrayInputStream(EXPECTED.getBytes());) {
            System.setIn(in);
            byte[] bytes = IOUtil.readAllBytesFromFileOrStdin(Paths.get("-")); // "-" path
            assertThat(bytes).isEqualTo(EXPECTED.getBytes());
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    void readAllByteFromFileOrStdinWithDashPath_dashFileExists() throws IOException {
        final String EXPECTED = "readAllByteFromFileOrStdinWithDashPath_dashFileExists";

        // This test case is reading the file "-" exists in the current directory.
        // Don't use @TempDir because it will create a dashFile with unnecessary leading path, such as "/foobarbaz/-".
        Path dashFile = Paths.get("-");

        try {
            Files.writeString(dashFile, EXPECTED); // "-" path

            byte[] bytes = IOUtil.readAllBytesFromFileOrStdin(dashFile);
            assertThat(bytes).isEqualTo(EXPECTED.getBytes());
        } finally {
            Files.deleteIfExists(dashFile);
        }
    }

    /**
     * Test vectors from <a href="https://tools.ietf.org/html/rfc4648#section-10">rfc4648#section-10</a>
     */
    private static final BiMap<String, String> BASE64_TEST_VECTORS = ImmutableBiMap.<String, String>builder()
            .put("", "")
            .put("f", "Zg==")
            .put("fo", "Zm8=")
            .put("foo", "Zm9v")
            .put("foob", "Zm9vYg==")
            .put("fooba", "Zm9vYmE=")
            .put("foobar", "Zm9vYmFy")
            .build();

    @Test
    void base64Decode() {
        for (String expected : BASE64_TEST_VECTORS.keySet()) {
            String base64 = BASE64_TEST_VECTORS.get(expected);
            byte[] bytes = IOUtil.base64Decode(base64);
            assertThat(new String(bytes)).isEqualTo(expected);
        }
    }

    @Test
    void base64Encode() {
        for (String plain : BASE64_TEST_VECTORS.keySet()) {
            String expected = BASE64_TEST_VECTORS.get(plain);
            String base64 = IOUtil.base64Encode(plain.getBytes());
            assertThat(base64).isEqualTo(expected);
        }
    }
}