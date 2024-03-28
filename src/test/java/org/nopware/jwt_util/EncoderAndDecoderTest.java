package org.nopware.jwt_util;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EncoderAndDecoderTest {
    @Test
    void encodeAndDecodeByHSXYZ() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        testEncodeAndDecode("HS256", "secret-hs256.bin");
        testEncodeAndDecode("HS384", "secret-hs384.bin");
        testEncodeAndDecode("HS512", "secret-hs512.bin");
    }

    @Test
    void encodeAndDecodeByRSXYZ() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        testEncodeAndDecode("RS256", "rsa-private.pem", "rsa-public.pem");
        testEncodeAndDecode("RS384", "rsa-private.pem", "rsa-public.pem");
        testEncodeAndDecode("RS512", "rsa-private.pem", "rsa-public.pem");
    }

    @Test
    void encodeAndDecodeByESXYZ() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        testEncodeAndDecode("ES256", "ec256-key-pair.pem", "ec256-public.pem");
        testEncodeAndDecode("ES384", "ec384-key-pair.pem", "ec384-public.pem");
        testEncodeAndDecode("ES512", "ec521-key-pair.pem", "ec521-public.pem");
    }

    @Test
    void encodeAndDecodeByPSXYZ() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        testEncodeAndDecode("PS256", "rsa-pss-256-private.pem", "rsa-pss-256-public.pem");
        testEncodeAndDecode("PS384", "rsa-pss-384-private.pem", "rsa-pss-384-public.pem");
        testEncodeAndDecode("PS512", "rsa-pss-512-private.pem", "rsa-pss-512-public.pem");
    }

    void testEncodeAndDecode(String algorithm, String secretFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        URL resource = Resources.getResource(secretFile);
        Path path = Paths.get(resource.getPath());
        byte[] secret = Files.readAllBytes(path);
        testEncodeAndDecode(algorithm, secret, secret);
    }

    void testEncodeAndDecode(String algorithm, String privateKeyInPemFile, String publicKeyInPemFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyInPem = readPemObject(privateKeyInPemFile);
        byte[] publicKeyInPem = readPemObject(publicKeyInPemFile);
        testEncodeAndDecode(algorithm, privateKeyInPem, publicKeyInPem);
    }

    void testEncodeAndDecode(String algorithm, byte[] privateKeyInPem, byte[] publicKeyInPem) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String headerJson = "{\"alg\":\"" + algorithm + "\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"admin\":true}";

        Algorithm algorithmForSigning = Algorithms.forSigning(Alg.valueOf(algorithm), privateKeyInPem);

        String token = Encoder.encode(Optional.of(headerJson), payloadJson, algorithmForSigning);
        assertNotNull(token);

        DecodedJWT decodedJWT = Decoder.decode(token);

        // Check header.
        assertEquals(algorithm, decodedJWT.getAlgorithm());
        assertEquals("JWT", decodedJWT.getType());

        // Check payload.
        assertEquals("1234567890", decodedJWT.getSubject());
        assertEquals("John Doe", decodedJWT.getClaim("name").asString());
        assertTrue(decodedJWT.getClaim("admin").asBoolean());

        try {
            Algorithm algorithmForVerifying = Algorithms.forVerifying(Alg.valueOf(algorithm), publicKeyInPem);
            DecodedJWT verifiedJWT = Decoder.verify(token, algorithmForVerifying);
            assertNotNull(verifiedJWT.getSignature());
        } catch (Exception e) {
            fail(e);
        }
    }

    byte[] readPemObject(String pemFile) throws IOException {
        URL urlPem = Resources.getResource(pemFile);
        return KeyUtil.readPemObject(Resources.asCharSource(urlPem, Charsets.US_ASCII).read());
    }
}