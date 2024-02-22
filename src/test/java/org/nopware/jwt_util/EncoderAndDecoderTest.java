package org.nopware.jwt_util;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EncoderAndDecoderTest {

    @Test
    void encodeAndDecode() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String headerJson = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"admin\":true}";

        URL urlRsaPrivatePem = Resources.getResource("rsa-private.pem");
        URL urlRsaPublicPem = Resources.getResource("rsa-public.pem");

        byte[] rsaPrivateKeyInPem = KeyUtil.readPemObject(Resources.asCharSource(urlRsaPrivatePem, Charsets.US_ASCII).read());
        byte[] rsaPublicKeyInPem = KeyUtil.readPemObject(Resources.asCharSource(urlRsaPublicPem, Charsets.US_ASCII).read());

        Algorithm algorithmForSigning = Algorithms.forSigning(Alg.RS256, rsaPrivateKeyInPem);

        String token = Encoder.encode(Optional.of(headerJson), payloadJson, algorithmForSigning);
        assertNotNull(token);

        DecodedJWT decodedJWT = Decoder.decode(token);

        // Check header.
        assertEquals("RS256", decodedJWT.getAlgorithm());
        assertEquals("JWT", decodedJWT.getType());

        // Check payload.
        assertEquals("1234567890", decodedJWT.getSubject());
        assertEquals("John Doe", decodedJWT.getClaim("name").asString());
        assertTrue(decodedJWT.getClaim("admin").asBoolean());

        try {
            Algorithm algorithmForVerifying = Algorithms.forVerifying(Alg.RS256, rsaPublicKeyInPem);
            DecodedJWT verifiedJWT = Decoder.verify(token, algorithmForVerifying);
            assertNotNull(verifiedJWT.getSignature());
        } catch (Exception e) {
            fail(e);
        }
    }
}