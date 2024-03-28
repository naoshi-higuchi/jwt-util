package org.nopware.jwt_util.psalg;

import javax.annotation.Nullable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PS256 extends PS {
    public PS256(@Nullable PublicKey publicKey, @Nullable PrivateKey privateKey) {
        super(256, publicKey, privateKey);
    }
}
