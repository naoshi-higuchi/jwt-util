package org.nopware.jwt_util;

import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {

    @Test
    void forSigningRS256() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream inputStream = Resources.getResource("rsa-private.pem").openStream()) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            Algorithm algorithm = Algorithms.forSigning(Alg.RS256, keyInPem);
            assertNotNull(algorithm);
        }
    }

    @Test
    void forVerifyingRS256() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream inputStream = Resources.getResource("rsa-public.pem").openStream()) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            Algorithm algorithm = Algorithms.forVerifying(Alg.RS256, keyInPem);
            assertNotNull(algorithm);
        }
    }

    @Test
    void forSigningES256() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream inputStream = Resources.getResource("ec256-key-pair.pem").openStream()) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            Algorithm algorithm = Algorithms.forSigning(Alg.ES256, keyInPem);
            assertNotNull(algorithm);
        }
    }

    @Test
    void forVerifyingES256() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream inputStream = Resources.getResource("ec256-public.pem").openStream()) {
            byte[] keyInPem = KeyUtil.readPemObject(inputStream);
            Algorithm algorithm = Algorithms.forVerifying(Alg.ES256, keyInPem);
            assertNotNull(algorithm);
        }
    }
}