package org.nopware.jwt_util;

import com.auth0.jwt.algorithms.Algorithm;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

public class Algorithms {
    public static Algorithm forSigning(Alg alg, byte[] keyInPem) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        switch (alg) {
            case HS256:
            case HS384:
            case HS512:
            case RS256:
                RSAPrivateKey rsaPrivateKey = KeyUtil.readRSAPrivateKey(keyInPem);
                return Algorithm.RSA256(null, rsaPrivateKey);
            case RS384:
            case RS512:
            case ES256:
            case ES384:
            case ES512:
            case PS256:
            case PS384:
            case PS512:
            case NONE:
        }
        throw new IllegalArgumentException("Unsupported algorithm: " + alg);
    }
    public static Algorithm forVerifying(Alg alg, byte[] keyInPem) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        switch (alg) {
            case HS256:
            case HS384:
            case HS512:
            case RS256:
                RSAPublicKey rsaPublicKey = KeyUtil.readRSAPublicKey(keyInPem);
                return Algorithm.RSA256(rsaPublicKey, null);
            case RS384:
            case RS512:
            case ES256:
            case ES384:
            case ES512:
            case PS256:
            case PS384:
            case PS512:
            case NONE:
        }
        throw new IllegalArgumentException("Unsupported algorithm: " + alg);
    }
}
