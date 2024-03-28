package org.nopware.jwt_util.psalg;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.nopware.jwt_util.Alg;
import org.nopware.jwt_util.Algorithms;
import org.nopware.jwt_util.KeyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTest {
    private Path pathFor(String filename) {
        URL resource = Resources.getResource(filename);
        return Paths.get(resource.getPath());
    }

    private byte[] loadPemObject(String filename) throws IOException {
        Path path = pathFor(filename);
        try (InputStream inputStream = Files.newInputStream(path)) {
            return KeyUtil.readPemObject(inputStream);
        }
    }

    @Test
    public void testDoSignAndDoVerify() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, SignatureException, InvalidKeyException {
        byte[] privateKeyBytes = loadPemObject("rsa-pss-256-private.pem");
        byte[] publicKeyBytes = loadPemObject("rsa-pss-256-public.pem");

        PS sign = (PS) Algorithms.forSigning(Alg.PS256, privateKeyBytes);
        PS verify = (PS) Algorithms.forVerifying(Alg.PS256, publicKeyBytes);

        byte[] message = "Hello, world!".getBytes();
        byte[] signature = sign.doSign(message);

        boolean b = verify.doVerify(message, signature);

        assertTrue(b);
    }

    @Test
    public void testKeyCompatibility() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, SignatureException, InvalidKeyException {
        byte[] privateKeyBytes = loadPemObject("rsa-private.pem");
        byte[] publicKeyBytes = loadPemObject("rsa-public.pem");

        PS sign = (PS) Algorithms.forSigning(Alg.PS256, privateKeyBytes);
        PS verify = (PS) Algorithms.forVerifying(Alg.PS256, publicKeyBytes);

        byte[] message = "Hello, world!".getBytes();
        byte[] signature = sign.doSign(message);

        boolean b = verify.doVerify(message, signature);

        assertTrue(b);
    }
}