package org.nopware.jwt_util;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    void readKeyFromSecret() throws URISyntaxException {
        try {
            KeyUtil.readKeyOrSecret(Alg.RS256, Paths.get(Resources.getResource("secret-hs256.bin").toURI()));
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            // expected
            assertThat(e.getMessage()).isEqualTo(KeyUtil.EXMSG_NO_PEM_OBJECT_FOUND);
            System.out.println(e.getMessage());
        }
    }

    // RSA KeyFactory does not support RSASSA-PSS keys.
    @Test
    void demonstrateKeyFactoryBehaviour_forRSA() throws NoSuchAlgorithmException, URISyntaxException, IOException {
        // Load a key for RSASSA-PSS.
        byte[] keyInPem = IOUtil.readAllBytesFromFileOrStdin(Paths.get(Resources.getResource("rsa-pss-256-private.pem").toURI()));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyInPem);

        // KeyFactory for RSA. Not for RSASSA-PSS.
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // KeyFactory for RSA rejects RSASSA-PSS keys.
        InvalidKeySpecException expected = assertThrows(InvalidKeySpecException.class,
                () -> keyFactory.generatePrivate(keySpec));
        System.out.println(expected.getMessage());
    }

    // RSASSA-PSS KeyFactory does not support RSA keys.
    @Test
    void demonstrateKeyFactoryBehaviour_forRSASSA_PSS() throws NoSuchAlgorithmException, URISyntaxException, IOException {
        // Load a key for RSA.
        byte[] keyInPem = IOUtil.readAllBytesFromFileOrStdin(Paths.get(Resources.getResource("rsa-private.pem").toURI()));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyInPem);

        // KeyFactory for RSASSA-PSS. Not for RSA.
        KeyFactory keyFactory = KeyFactory.getInstance("RSASSA-PSS");

        // KeyFactory for RSASSA-PSS rejects RSA keys.
        InvalidKeySpecException invalidKeySpecException = assertThrows(InvalidKeySpecException.class,
                () -> keyFactory.generatePrivate(keySpec));
        System.out.println(invalidKeySpecException.getMessage());
    }

    // RSASSA-PSS Signature supports RSA keys.
    @Test
    void demonstrateSignatureBehaviour_forRSASSA_PSS() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        // Load a key for RSA.
        RSAPrivateKey rsaPrivateKey;
        try (InputStream inputStream = getResource("rsa-private.pem")) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            rsaPrivateKey = KeyUtil.readRSAPrivateKey(keyInPem);
        }

        // Signature for RSASSA-PSS is not supported by default. Add Bouncy Castle.
        Security.addProvider(new BouncyCastleProvider());

        // Signature for RSASSA-PSS. Not for RSA.
        Signature signature = Signature.getInstance("SHA256withRSA/PSS");
        signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));

        // Signature for RSASSA-PSS accepts RSA keys.
        signature.initSign(rsaPrivateKey);
        signature.update("Hello, world!".getBytes());

        byte[] signatureBytes = signature.sign();
        assertThat(signatureBytes).isNotEmpty();
    }

    // RSA Signature does not support RSASSA-PSS keys.
    @Test
    void demonstrateSignatureBehaviour_forRSA() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        // Load a key for RSA.
        RSAPrivateKey rsassapssPrivateKey;
        try (InputStream inputStream = getResource("rsa-pss-256-private.pem")) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            rsassapssPrivateKey = KeyUtil.readRSAPSSPrivateKey(keyInPem);
        }

        // Signature for RSASSA-PSS. Not for RSA.
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Signature for RSA rejects RSASSA-PSS keys.
        InvalidKeyException invalidKeyException = assertThrows(InvalidKeyException.class,
                () -> signature.initSign(rsassapssPrivateKey));
        System.out.println(invalidKeyException.getMessage());
        System.out.println(invalidKeyException.getCause().getMessage());
    }
}