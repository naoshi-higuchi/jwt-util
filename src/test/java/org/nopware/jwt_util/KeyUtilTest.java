package org.nopware.jwt_util;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

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
            assertEquals(1024, rsaPrivateKey.getModulus().bitLength());
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
            assertEquals(1024, rsaPublicKey.getModulus().bitLength());
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
}