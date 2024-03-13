package org.nopware.jwt_util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Optional;

public class Encoder {
    public static String encode(Optional<String> headerJson, String payloadJson, Algorithm algorithm) {
        JWTCreator.Builder builder = JWT.create();
        headerJson.map(builder::withHeader);

        return builder.withPayload(payloadJson)
                .sign(algorithm);
    }
}
