package org.nopware.jwt_util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    public void readAllBytesFromFileOrStdinWithNullPath() throws IOException {
        final String EXPECTED = "readAllBytesFromFileOrStdinWithNullPath";
        try (ByteArrayInputStream in = new ByteArrayInputStream(EXPECTED.getBytes());) {
            System.setIn(in);
            byte[] bytes = IOUtil.readAllBytesFromFileOrStdin(null); // null path
            assertThat(bytes).isEqualTo(EXPECTED.getBytes());
        } finally {
            System.setIn(System.in);
        }
    }

    @Test
    public void readAllBytesFromFileOrStdinWithDashPath() throws IOException {
        final String EXPECTED = "readAllBytesFromFileOrStdinWithDashPath";
        try (ByteArrayInputStream in = new ByteArrayInputStream(EXPECTED.getBytes());) {
            System.setIn(in);
            byte[] bytes = IOUtil.readAllBytesFromFileOrStdin(Paths.get("-")); // "-" path
            assertThat(bytes).isEqualTo(EXPECTED.getBytes());
        } finally {
            System.setIn(System.in);
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