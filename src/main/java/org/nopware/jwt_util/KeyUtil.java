package org.nopware.jwt_util;

import com.google.common.base.Charsets;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class KeyUtil {
    private static final Random fRandom = new Random();

    /**
     * Reads a PEM object from input stream.
     *
     * <p>This method does not close the input stream.
     *
     * @param inputStream the input stream.
     * @return the content of the PEM object
     * @throws IOException if the file cannot be read
     */
    public static byte[] readPemObject(InputStream inputStream) throws IOException {
        try (PemReader pemReader = new PemReader(new InputStreamReader(inputStream, Charsets.US_ASCII))) {
            PemObject pemObject = pemReader.readPemObject();
            return pemObject.getContent();
        }
    }

    public static byte[] readPemObject(String pemString) throws IOException {
        try (PemReader pemReader = new PemReader(new StringReader(pemString))) {
            PemObject pemObject = pemReader.readPemObject();
            return pemObject.getContent();
        }
    }

    /**
     * Reads an RSA private key.
     * @param keyInPem the RSA private key in PEM format
     * @return the RSA private key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey readRSAPrivateKey(byte[] keyInPem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyInPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * Reads an EC (Elliptic Curve) private key from a PEM (Privacy Enhanced Mail) format.
     *
     * @param keyInPem the EC private key in PEM format
     * @return the EC private key
     * @throws NoSuchAlgorithmException if the "EC" algorithm is not available in the environment
     * @throws InvalidKeySpecException if the provided key specification is inappropriate
     */
    public static ECPrivateKey readECPrivateKey(byte[] keyInPem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyInPem);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * Reads an RSA public key.
     *
     * @param keyInPem the RSA public key in PEM format
     * @return the RSA public key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey readRSAPublicKey(byte[] keyInPem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyInPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * Reads an EC (Elliptic Curve) public key.
     *
     * @param keyInPem the EC public key in PEM format
     * @return the EC public key
     * @throws NoSuchAlgorithmException if the "EC" algorithm is not available in the environment
     * @throws InvalidKeySpecException if the provided key specification is inappropriate
     */
    public static ECPublicKey readECPublicKey(byte[] keyInPem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyInPem);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return (ECPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static byte[] random(int length) {
        byte[] bytes = new byte[length];
        fRandom.nextBytes(bytes);
        return bytes;
    }
}
