package org.nopware.jwt_util;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class KeyUtilTest {

    private static InputStream getResource(String name) throws IOException {
        return Resources.getResource(name).openStream();
    }

    @Test
    void readPemObject() {
        try (InputStream inputStream = getResource("rsa-private.pem")) {
            byte[] content = KeyUtil.readPemObject(inputStream);
            assertNotNull(content);
            assertTrue(content.length > 0);

            try {
                int ignore = inputStream.read();
                fail("Expected input stream to be closed");
            } catch (IOException e) {
                // expected
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void readRSAPrivateKey() {
        try (InputStream inputStream = getResource("rsa-private.pem")) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            RSAPrivateKey rsaPrivateKey = KeyUtil.readRSAPrivateKey(keyInPem);
            assertNotNull(rsaPrivateKey);
            assertEquals("RSA", rsaPrivateKey.getAlgorithm());
            assertEquals("PKCS#8", rsaPrivateKey.getFormat());
            assertEquals(2048, rsaPrivateKey.getModulus().bitLength());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void readRSAPublicKey() {
        try (InputStream inputStream = getResource("rsa-public.pem")) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            RSAPublicKey rsaPublicKey = KeyUtil.readRSAPublicKey(keyInPem);
            assertNotNull(rsaPublicKey);
            assertEquals("RSA", rsaPublicKey.getAlgorithm());
            assertEquals("X.509", rsaPublicKey.getFormat());
            assertEquals(2048, rsaPublicKey.getModulus().bitLength());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void random() {
        byte[] bytes = KeyUtil.random(32);
        assertNotNull(bytes);
        assertEquals(32, bytes.length);

        /*
         * Testing randomness is hard.
         */
        System.out.println("Random: " + Arrays.toString(bytes));
    }

    @Test
    void readECPrivateKey() {
        try (InputStream inputStream = getResource("ec256-key-pair.pem")) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            KeyUtil.readECPrivateKey(keyInPem);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void readECPublicKey() {
        try (InputStream inputStream = getResource("ec256-public.pem")) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            KeyUtil.readECPublicKey(keyInPem);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void readKeyOrSecret() throws URISyntaxException, IOException {
        final Map<Alg, String> keys = ImmutableMap.<Alg, String>builder()
                .put(Alg.HS256, "secret-hs256.bin")
                .put(Alg.HS384, "secret-hs384.bin")
                .put(Alg.HS512, "secret-hs512.bin")
                .put(Alg.RS256, "rsa-private.pem")
                .put(Alg.RS384, "rsa-private.pem")
                .put(Alg.RS512, "rsa-private.pem")
                .put(Alg.ES256, "ec256-key-pair.pem")
                .put(Alg.ES384, "ec384-key-pair.pem")
                .put(Alg.ES512, "ec521-key-pair.pem")
                .put(Alg.PS256, "rsa-private.pem")
                .put(Alg.PS384, "rsa-private.pem")
                .put(Alg.PS512, "rsa-private.pem")
                .build();

        for (Alg alg : keys.keySet()) {
            URL resource = Resources.getResource(keys.get(alg));
            Path path = Paths.get(resource.toURI());

            byte[] keyOrSecret = KeyUtil.readKeyOrSecret(alg, path);
            assertThat(keyOrSecret).isNotEmpty();
        }
    }

    @Test
    void readKeyOrSecretWithNone() throws URISyntaxException, IOException {
        byte[] none = KeyUtil.readKeyOrSecret(Alg.NONE, Paths.get(Resources.getResource("secret-hs256.bin").toURI()));
        assertThat(none).isEmpty();
    }

    @Test
    void readKeyOrSecretWithUnmatchedAlgAndKey() throws URISyntaxException {
        try {
            KeyUtil.readKeyOrSecret(Alg.RS256, Paths.get(Resources.getResource("secret-hs256.bin").toURI()));
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            // expected
            assertThat(e.getMessage()).isEqualTo("No PEM object found");
            System.out.println(e.getMessage());
        }
    }
}