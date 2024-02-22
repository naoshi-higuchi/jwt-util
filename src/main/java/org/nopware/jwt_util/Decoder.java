package org.nopware.jwt_util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Decoder {

    /**
     * Decodes the given token without verifying its signature.
     *
     * @param token
     * @return
     */
    public static DecodedJWT decode(String token) {
        return JWT.decode(token);
    }

    /**
     * Decodes and verifies the given token.
     *
     * @param token
     * @param algorithm
     * @return
     */
    public static DecodedJWT verify(String token, Algorithm algorithm) throws JWTVerificationException {
        return JWT.require(algorithm).build().verify(token);
    }
}
